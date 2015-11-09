package com.cs.exchange;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cs.exchange.data.OrderGenerator;
import com.cs.exchange.domain.Direction;
import com.cs.exchange.domain.OpenInterest;
import com.cs.exchange.domain.Order;
import com.cs.exchange.matcher.OrderMatcherImpl;

public class ExchangeDemo {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExchangeDemo.class);
    
    private static final String RIC = "VOD.L";
    private static final String USER_1 = "User 1";
    private static final String USER_2 = "User 2";
    
    public static void main(String[] args) {
        
        Exchange ex = new Exchange(new OrderMatcherImpl());
        
        for (Order order : OrderGenerator.getOrders()) {
            LOGGER.info("New Order: " + 
                        String.format("%s %s %s @ %s %s", order.getDirection(),
                                                          order.getQuantity().toString(),
                                                          order.getRic(),
                                                          order.getPrice().toString(),
                                                          order.getUser()));
            
            ex.addNewOrder(order);
            
            List<OpenInterest> interest = ex.getOpenInterest(RIC, Direction.BUY);
            
            LOGGER.info(String.format("\tOpen %s %s interest = %s", 
                        RIC, Direction.BUY, (interest != null ? interest.toString() : "")));
            
            interest = ex.getOpenInterest(RIC, Direction.SELL);
            
            LOGGER.info(String.format("\tOpen %s %s interest = %s", 
                        RIC, Direction.SELL, (interest != null ? interest.toString() : "")));
            
            BigDecimal avExecPrice = ex.getAverageExecutionPrice(RIC);
            
            LOGGER.info(String.format("\tAverage %s execution price = %s", 
                        RIC, (avExecPrice != null ? avExecPrice.toString() : "")));
        
            BigDecimal execQty = ex.getExecutedQuantityForUser(RIC, USER_1);
            
            LOGGER.info(String.format("\tExecuted quantity for %s, %s = %s", 
                        RIC, USER_1, (execQty != null ? execQty.toString() : "")));
    
            execQty = ex.getExecutedQuantityForUser(RIC, USER_2);
            
            LOGGER.info(String.format("\tExecuted quantity for %s, %s = %s", 
                        RIC, USER_2, (execQty != null ? execQty.toString() : "")));
    
        }
    }

}
