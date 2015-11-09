package com.cs.exchange.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.cs.exchange.domain.Direction;
import com.cs.exchange.domain.Order;

public class OrderGenerator {
    
    private static final List<Order> orders = new ArrayList<Order>();
    private static final String RIC = "VOD.L";
    
    static {
        orders.add(new Order(Direction.SELL, RIC, new BigDecimal("1000"), new BigDecimal("100.2"), "User 1"));
        orders.add(new Order(Direction.BUY, RIC, new BigDecimal("1000"), new BigDecimal("100.2"), "User 2"));
        orders.add(new Order(Direction.BUY, RIC, new BigDecimal("1000"), new BigDecimal("99"), "User 1"));
        orders.add(new Order(Direction.BUY, RIC, new BigDecimal("1000"), new BigDecimal("101"), "User 1"));
        orders.add(new Order(Direction.SELL, RIC, new BigDecimal("500"), new BigDecimal("102"), "User 2"));
        orders.add(new Order(Direction.BUY, RIC, new BigDecimal("500"), new BigDecimal("103"), "User 1"));
        orders.add(new Order(Direction.SELL, RIC, new BigDecimal("1000"), new BigDecimal("98"), "User 2"));
    }

    private OrderGenerator() {        
    }
    
    public static final List<Order> getOrders() {
        return new ArrayList<Order>(orders);
    }
}
