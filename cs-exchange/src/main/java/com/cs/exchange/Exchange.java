package com.cs.exchange;

import static com.google.common.collect.Collections2.filter;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.cs.exchange.domain.Direction;
import com.cs.exchange.domain.OpenInterest;
import com.cs.exchange.domain.Order;
import com.cs.exchange.matcher.OrderMatcher;
import com.google.common.base.Predicate;

/**
 * Exchange system that matches orders on stocks
 *
 */
public class Exchange {

    private final OrderMatcher matcher;

    // List of all orders submitted to the Exchange maintained in submission
    // order
    private final List<Order> submittedOrders = new ArrayList<Order>();

    // List of all open orders - this will grow and shrink...hence LinkedList
    private final List<Order> openOrders = new LinkedList<Order>();

    // Map of stock identifier (RIC) against execution
    private final Map<String, List<Execution>> execStockMap = new HashMap<String, List<Execution>>();

    public Exchange(OrderMatcher matcher) {
        this.matcher = matcher;
    }

    /**
     * Submit a new order to the Exchange
     * 
     * @param o
     *            - the order to be added
     */
    public void addNewOrder(Order o) {

        if (o == null) {
            throw new RuntimeException("Unable to add a NULL order");
        }

        validateOrderDetails(o);
        submittedOrders.add(o);

        // Avoid inconsistent collection state when matching and processing
        // orders
        synchronized (this) {
            List<Order> matchingOrders = getMatchingOrders(o);
            processMatchingOrders(o, matchingOrders);
        }
    }

    /**
     * Get a list of all the orders that were submitted to the Exchange
     * 
     * @return the list of all submitted orders
     */
    public List<Order> getAllOrders() {
        return new ArrayList<Order>(submittedOrders);
    }

    /**
     * Get the list of "open interest" for the given stock identifier (RIC) and
     * direction
     * 
     * Open interest is defined as the total quantity of all open orders on a
     * per price point basis
     * 
     * @param ric
     *            - the stock identifier for which open interest is required
     * @param buySell
     *            - the direction for which open interest is required
     * @return a list of open interest entries sorted in descending order of
     *         price
     */
    public List<OpenInterest> getOpenInterest(String ric, Direction buySell) {
        List<OpenInterest> interest = new ArrayList<OpenInterest>();

        Collection<Order> matchingOpenOrders = filter(openOrders, new Predicate<Order>() {

            @Override
            public boolean apply(Order input) {
                return input.getRic().equals(ric) && input.getDirection().equals(buySell);
            }

        });

        if (matchingOpenOrders != null && matchingOpenOrders.size() > 0) {
            // Now we aggregate quantity for each price, but we don't want to
            // use BigDecimal as key in a HashMap as equals/compareTo relationship
            // is broken...so we use a TreeMap which only uses compareTo
            Map<BigDecimal, BigDecimal> totalQtyByPriceMap = new TreeMap<BigDecimal, BigDecimal>();

            for (Order order : matchingOpenOrders) {
                BigDecimal qty = totalQtyByPriceMap.get(order.getPrice());

                if (qty == null) {
                    qty = BigDecimal.ZERO;
                }

                totalQtyByPriceMap.put(order.getPrice(), qty.add(order.getQuantity()));
            }

            for (BigDecimal price : totalQtyByPriceMap.keySet()) {
                interest.add(new OpenInterest(totalQtyByPriceMap.get(price), price));
            }

            // Sort the open interest entries by descending price
            Collections.sort(interest, new Comparator<OpenInterest>() {

                @Override
                public int compare(OpenInterest o1, OpenInterest o2) {
                    return o2.getPrice().compareTo(o1.getPrice());
                }

            });
        }

        return interest;
    }

    /**
     * Get the average execution price for the given stock identifier (RIC)
     * 
     * The average execution price is the average price per unit of all
     * executions of the stock.
     * 
     * @param ric
     *            - the stock identifier for which average execution price is
     *            required
     * @return - the average execution price or null if no executions have
     *         occurred
     */
    public BigDecimal getAverageExecutionPrice(String ric) {

        List<Execution> execs = execStockMap.get(ric);

        if (execs != null) {
            BigDecimal totalQty = BigDecimal.ZERO;
            BigDecimal totalAmt = BigDecimal.ZERO;
            MathContext mc = MathContext.DECIMAL64;

            for (Execution execution : execs) {
                totalQty = totalQty.add(execution.quantity);
                totalAmt = totalAmt.add(execution.getQuantity().multiply(execution.getPrice(), mc));
            }

            return totalAmt.divide(totalQty, mc).setScale(4, RoundingMode.HALF_UP);
        }

        return null;
    }

    /**
     * Get the executed quantity for the given stock identifier (RIC) and user
     * 
     * The executed quantity is the sum of the quantities of all executed orders
     * of the stock by user. Quantity for buy and sell orders is netted.
     * 
     * @param ric
     *            - the stock identifier for which executed quantity is required
     * @param user
     *            - the user for which executed quantity is required
     * @return - the executed quantity
     */
    public BigDecimal getExecutedQuantityForUser(String ric, String user) {
        List<Execution> execs = execStockMap.get(ric);

        BigDecimal totalQty = BigDecimal.ZERO;

        if (execs != null) {

            for (Execution execution : execs) {
                if (execution.getBuyOrder().getUser().equals(user)) {
                    totalQty = totalQty.add(execution.getQuantity());
                } else if (execution.getSellOrder().getUser().equals(user)) {
                    totalQty = totalQty.subtract(execution.getQuantity());
                }
            }
        }

        return totalQty;
    }

    private void processMatchingOrders(Order o, List<Order> matchingOrders) {

        if (matchingOrders == null || matchingOrders.size() == 0) {
            // No matching orders, add to list of OPEN orders
            openOrders.add(o);
        } else {
            if (matchingOrders.size() == 1) {
                // Single match so create an execution
                addExecution(o, matchingOrders.get(0));
            } else {
                // Multiple possible matches
                Order priceMatch = null;

                // Find the first matching order with same price as new order
                for (Order order : matchingOrders) {
                    if (o.getPrice().compareTo(order.getPrice()) == 0) {
                        priceMatch = order;
                        break;
                    }
                }

                if (priceMatch != null) {
                    addExecution(o, priceMatch);
                } else {
                    // Sort the orders by price (lowest to highest)
                    List<Order> ordersByPrice = new ArrayList<Order>(matchingOrders);
                    Collections.sort(ordersByPrice, new Comparator<Order>() {

                        @Override
                        public int compare(Order o1, Order o2) {
                            return o1.getPrice().compareTo(o2.getPrice());
                        }
                    });

                    if (o.getDirection().equals(Direction.BUY)) {
                        // New buy order should be matched against order with
                        // lowest price (first element in sorted list)
                        addExecution(o, ordersByPrice.get(0));
                    } else {
                        // New sell order should be matched against order with
                        // highest price (last element in sorted list)
                        addExecution(o, ordersByPrice.get(ordersByPrice.size() - 1));
                    }
                }
            }
        }
    }

    private void addExecution(Order o, Order matchedOrder) {

        List<Execution> execs = execStockMap.get(o.getRic());

        if (execs == null) {
            execs = new ArrayList<Execution>();
            execStockMap.put(o.getRic(), execs);
        }

        Order buy = o.getDirection().equals(Direction.BUY) ? o : matchedOrder;
        Order sell = (o == buy) ? matchedOrder : o;

        execs.add(new Execution(buy, sell, o.getPrice()));

        // Having added the new execution, make sure matched order is no longer
        // OPEN
        openOrders.remove(matchedOrder);
    }

    private List<Order> getMatchingOrders(Order o) {

        List<Order> matching = new ArrayList<Order>();

        // Check all OPEN orders for a match
        for (Order order : openOrders) {
            if (matcher.checkIfOrdersMatch(o, order)) {
                matching.add(order);
            }
        }

        return matching;
    }

    private void validateOrderDetails(Order o) {

        if (o.getDirection() == null) {
            throw new RuntimeException("Order direction is NULL");
        }

        if (o.getRic() == null || o.getRic().trim().length() == 0) {
            throw new RuntimeException("Order stock identifier (RIC) is invalid");
        }

        if (o.getQuantity() == null || o.getQuantity().signum() != 1) {
            throw new RuntimeException("Order quantity is invalid");
        }

        if (o.getPrice() == null || o.getPrice().signum() != 1) {
            throw new RuntimeException("Order price is invalid");
        }

        if (o.getUser() == null || o.getUser().trim().length() == 0) {
            throw new RuntimeException("Order's user is invalid");
        }

    }

    private class Execution {

        private final Order buyOrder;
        private final Order sellOrder;
        private final BigDecimal quantity;
        private final BigDecimal price;

        Execution(Order buyOrder, Order sellOrder, BigDecimal price) {
            this.buyOrder = buyOrder;
            this.sellOrder = sellOrder;

            this.quantity = buyOrder.getQuantity();
            this.price = price;
        }

        Order getBuyOrder() {
            return buyOrder;
        }

        Order getSellOrder() {
            return sellOrder;
        }

        BigDecimal getQuantity() {
            return quantity;
        }

        BigDecimal getPrice() {
            return price;
        }

    }

}
