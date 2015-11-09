package com.cs.exchange;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.cs.exchange.data.OrderGenerator;
import com.cs.exchange.domain.Direction;
import com.cs.exchange.domain.OpenInterest;
import com.cs.exchange.domain.Order;
import com.cs.exchange.matcher.OrderMatcherImpl;


public class ExchangeTest {

    private static final String RIC = "VOD.L";
    private static final String USER_1 = "User 1";
    private static final String USER_2 = "User 2";
    private Exchange ex;
    
    @Before
    public void setUp() throws Exception {
        ex = new Exchange(new OrderMatcherImpl());
    }

    @Test
    public void testAddOrders() {
        
        for (Order order : OrderGenerator.getOrders()) {
            ex.addNewOrder(order);
        }

        assertArrayEquals(OrderGenerator.getOrders().toArray(), ex.getAllOrders().toArray());
    }

    @Test
    public void testOpenInterest() {
        
        List<OpenInterest> interest = ex.getOpenInterest(RIC, Direction.BUY);
        assertTrue(interest.size() == 0);
        interest = ex.getOpenInterest(RIC, Direction.SELL);
        assertTrue(interest.size() == 0);
        
        List<Order> testOrders = OrderGenerator.getOrders();
        
        ex.addNewOrder(testOrders.get(0));
        interest = ex.getOpenInterest(RIC, Direction.BUY);
        assertTrue(interest.size() == 0);
        interest = ex.getOpenInterest(RIC, Direction.SELL);
        assertTrue(interest.size() == 1);
        assertTrue(interest.get(0).getQuantity().compareTo(new BigDecimal("1000")) == 0);
        assertTrue(interest.get(0).getPrice().compareTo(new BigDecimal("100.2")) == 0);
        
        ex.addNewOrder(testOrders.get(1));
        interest = ex.getOpenInterest(RIC, Direction.BUY);
        assertTrue(interest.size() == 0);
        interest = ex.getOpenInterest(RIC, Direction.SELL);
        assertTrue(interest.size() == 0);

        ex.addNewOrder(testOrders.get(2));
        interest = ex.getOpenInterest(RIC, Direction.BUY);
        assertTrue(interest.size() == 1);
        assertTrue(interest.get(0).getQuantity().compareTo(new BigDecimal("1000")) == 0);
        assertTrue(interest.get(0).getPrice().compareTo(new BigDecimal("99")) == 0);
        interest = ex.getOpenInterest(RIC, Direction.SELL);
        assertTrue(interest.size() == 0);
        
        ex.addNewOrder(testOrders.get(3));
        interest = ex.getOpenInterest(RIC, Direction.BUY);
        assertTrue(interest.size() == 2);
        assertTrue(interest.get(0).getQuantity().compareTo(new BigDecimal("1000")) == 0);
        assertTrue(interest.get(0).getPrice().compareTo(new BigDecimal("101")) == 0);                
        assertTrue(interest.get(1).getQuantity().compareTo(new BigDecimal("1000")) == 0);
        assertTrue(interest.get(1).getPrice().compareTo(new BigDecimal("99")) == 0);                
        interest = ex.getOpenInterest(RIC, Direction.SELL);
        assertTrue(interest.size() == 0);
        
        ex.addNewOrder(testOrders.get(4));
        interest = ex.getOpenInterest(RIC, Direction.BUY);
        assertTrue(interest.size() == 2);
        assertTrue(interest.get(0).getQuantity().compareTo(new BigDecimal("1000")) == 0);
        assertTrue(interest.get(0).getPrice().compareTo(new BigDecimal("101")) == 0);                
        assertTrue(interest.get(1).getQuantity().compareTo(new BigDecimal("1000")) == 0);
        assertTrue(interest.get(1).getPrice().compareTo(new BigDecimal("99")) == 0);                
        interest = ex.getOpenInterest(RIC, Direction.SELL);
        assertTrue(interest.size() == 1);
        assertTrue(interest.get(0).getQuantity().compareTo(new BigDecimal("500")) == 0);
        assertTrue(interest.get(0).getPrice().compareTo(new BigDecimal("102")) == 0);
        
        ex.addNewOrder(testOrders.get(5));
        interest = ex.getOpenInterest(RIC, Direction.BUY);
        assertTrue(interest.size() == 2);
        assertTrue(interest.get(0).getQuantity().compareTo(new BigDecimal("1000")) == 0);
        assertTrue(interest.get(0).getPrice().compareTo(new BigDecimal("101")) == 0);                
        assertTrue(interest.get(1).getQuantity().compareTo(new BigDecimal("1000")) == 0);
        assertTrue(interest.get(1).getPrice().compareTo(new BigDecimal("99")) == 0);                
        interest = ex.getOpenInterest(RIC, Direction.SELL);
        assertTrue(interest.size() == 0);
        
        ex.addNewOrder(testOrders.get(6));
        interest = ex.getOpenInterest(RIC, Direction.BUY);
        assertTrue(interest.size() == 1);
        assertTrue(interest.get(0).getQuantity().compareTo(new BigDecimal("1000")) == 0);
        assertTrue(interest.get(0).getPrice().compareTo(new BigDecimal("99")) == 0);                
        interest = ex.getOpenInterest(RIC, Direction.SELL);
        assertTrue(interest.size() == 0);
                
    }
    
    @Test
    public void testAverageExecutionPrice() {
        BigDecimal avExecPrice = ex.getAverageExecutionPrice(RIC);
        assertNull(avExecPrice);
                
        List<Order> testOrders = OrderGenerator.getOrders();
        
        ex.addNewOrder(testOrders.get(0));
        avExecPrice = ex.getAverageExecutionPrice(RIC);        
        assertNull(avExecPrice);
        
        ex.addNewOrder(testOrders.get(1));
        avExecPrice = ex.getAverageExecutionPrice(RIC);        
        assertTrue(new BigDecimal("100.2000").compareTo(avExecPrice) == 0);
        
        ex.addNewOrder(testOrders.get(2));
        avExecPrice = ex.getAverageExecutionPrice(RIC);        
        assertTrue(new BigDecimal("100.2000").compareTo(avExecPrice) == 0);
        
        ex.addNewOrder(testOrders.get(3));
        avExecPrice = ex.getAverageExecutionPrice(RIC);        
        assertTrue(new BigDecimal("100.2000").compareTo(avExecPrice) == 0);
        
        ex.addNewOrder(testOrders.get(4));
        avExecPrice = ex.getAverageExecutionPrice(RIC);        
        assertTrue(new BigDecimal("100.2000").compareTo(avExecPrice) == 0);
        
        ex.addNewOrder(testOrders.get(5));
        avExecPrice = ex.getAverageExecutionPrice(RIC);        
        assertTrue(new BigDecimal("101.1333").compareTo(avExecPrice) == 0);
        
        ex.addNewOrder(testOrders.get(6));
        avExecPrice = ex.getAverageExecutionPrice(RIC);        
        assertTrue(new BigDecimal("99.8800").compareTo(avExecPrice) == 0);
        
    }
    
    @Test
    public void testExecutedQuantityForUser() {
        BigDecimal execQuantity = ex.getExecutedQuantityForUser(RIC, USER_1);
        assertTrue(BigDecimal.ZERO.compareTo(execQuantity) == 0);
        execQuantity = ex.getExecutedQuantityForUser(RIC, USER_2);
        assertTrue(BigDecimal.ZERO.compareTo(execQuantity) == 0);
        
        List<Order> testOrders = OrderGenerator.getOrders();
        
        ex.addNewOrder(testOrders.get(0));
        execQuantity = ex.getExecutedQuantityForUser(RIC, USER_1);
        assertTrue(BigDecimal.ZERO.compareTo(execQuantity) == 0);
        execQuantity = ex.getExecutedQuantityForUser(RIC, USER_2);
        assertTrue(BigDecimal.ZERO.compareTo(execQuantity) == 0);
        
        ex.addNewOrder(testOrders.get(1));
        execQuantity = ex.getExecutedQuantityForUser(RIC, USER_1);
        assertTrue(new BigDecimal("-1000").compareTo(execQuantity) == 0);
        execQuantity = ex.getExecutedQuantityForUser(RIC, USER_2);
        assertTrue(new BigDecimal("1000").compareTo(execQuantity) == 0);
        
        ex.addNewOrder(testOrders.get(2));
        execQuantity = ex.getExecutedQuantityForUser(RIC, USER_1);
        assertTrue(new BigDecimal("-1000").compareTo(execQuantity) == 0);
        execQuantity = ex.getExecutedQuantityForUser(RIC, USER_2);
        assertTrue(new BigDecimal("1000").compareTo(execQuantity) == 0);
        
        ex.addNewOrder(testOrders.get(3));
        execQuantity = ex.getExecutedQuantityForUser(RIC, USER_1);
        assertTrue(new BigDecimal("-1000").compareTo(execQuantity) == 0);
        execQuantity = ex.getExecutedQuantityForUser(RIC, USER_2);
        assertTrue(new BigDecimal("1000").compareTo(execQuantity) == 0);
        
        ex.addNewOrder(testOrders.get(4));
        execQuantity = ex.getExecutedQuantityForUser(RIC, USER_1);
        assertTrue(new BigDecimal("-1000").compareTo(execQuantity) == 0);
        execQuantity = ex.getExecutedQuantityForUser(RIC, USER_2);
        assertTrue(new BigDecimal("1000").compareTo(execQuantity) == 0);
        
        ex.addNewOrder(testOrders.get(5));
        execQuantity = ex.getExecutedQuantityForUser(RIC, USER_1);
        assertTrue(new BigDecimal("-500").compareTo(execQuantity) == 0);
        execQuantity = ex.getExecutedQuantityForUser(RIC, USER_2);
        assertTrue(new BigDecimal("500").compareTo(execQuantity) == 0);
        
        ex.addNewOrder(testOrders.get(6));
        execQuantity = ex.getExecutedQuantityForUser(RIC, USER_1);
        assertTrue(new BigDecimal("500").compareTo(execQuantity) == 0);
        execQuantity = ex.getExecutedQuantityForUser(RIC, USER_2);
        assertTrue(new BigDecimal("-500").compareTo(execQuantity) == 0);
                        
    }
    
}
