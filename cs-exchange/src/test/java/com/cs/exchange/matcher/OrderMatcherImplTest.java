package com.cs.exchange.matcher;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.cs.exchange.data.OrderGenerator;
import com.cs.exchange.domain.Order;
import com.cs.exchange.matcher.OrderMatcherImpl;


public class OrderMatcherImplTest {

    private Order buyOrder1;
    private Order buyOrder2;
    private Order sellOrder1;
    private Order sellOrder2;
    
    private OrderMatcherImpl matcher;
    
    @Before
    public void setUp() throws Exception {
        matcher = new OrderMatcherImpl();
        List<Order> orders = OrderGenerator.getOrders();
        sellOrder1 = orders.get(0);
        buyOrder1 = orders.get(1);
        sellOrder2 = orders.get(4);
        buyOrder2 = orders.get(5);
    }

    @Test
    public void testOrdersMatch() {
        assertTrue("Orders should match", matcher.checkIfOrdersMatch(sellOrder1, buyOrder1));
        assertTrue("Orders should match", matcher.checkIfOrdersMatch(sellOrder2, buyOrder2 ));
    }

    @Test
    public void testOrdersDoNotMatch() {
        assertFalse("Null orders should not match", matcher.checkIfOrdersMatch(null, null));
        assertFalse("Null order should not match", matcher.checkIfOrdersMatch(sellOrder2, null));
        assertFalse("Orders should not match", matcher.checkIfOrdersMatch(sellOrder1, sellOrder2));
        assertFalse("Orders should not match", matcher.checkIfOrdersMatch(sellOrder1, buyOrder2));
        assertFalse("Orders should not match", matcher.checkIfOrdersMatch(sellOrder2, buyOrder1));
    }

}
