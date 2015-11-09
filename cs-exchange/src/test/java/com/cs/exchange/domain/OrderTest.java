package com.cs.exchange.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;


public class OrderTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testOrder() {
        BigDecimal qty = new BigDecimal("500");
        BigDecimal price = new BigDecimal("100.2");
        
        Order o = new Order(Direction.BUY, "VOD.L", qty, price, "User 1");
        
        assertEquals(Direction.BUY, o.getDirection());
        assertEquals("VOD.L", o.getRic());
        assertTrue(qty.compareTo(o.getQuantity()) == 0);
        assertTrue(price.compareTo(price) == 0);
        assertEquals("User 1", o.getUser());
    }

}
