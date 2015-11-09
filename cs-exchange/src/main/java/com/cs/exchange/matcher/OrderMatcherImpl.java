package com.cs.exchange.matcher;

import com.cs.exchange.domain.Direction;
import com.cs.exchange.domain.Order;

/**
 * Simple order matching implementation
 *
 */
public class OrderMatcherImpl implements OrderMatcher {

    /**
     * Match the two specified orders
     * @param o1 first order
     * @param o2 second order
     * @return true if matched, else false
     */
    @Override
    public boolean checkIfOrdersMatch(Order o1, Order o2) {
        
        if (o1 == null || o2 == null) {
            return false;
        }
        
        if (o1.getDirection().equals(o2.getDirection())) {
            return false;
        }
        
        if (!o1.getRic().equals(o2.getRic())) {
            return false;
        }
        
        if (o1.getQuantity().compareTo(o2.getQuantity()) != 0) {
            return false;
        }
        
        if ((o1.getDirection().equals(Direction.SELL) &&
             o1.getPrice().compareTo(o2.getPrice()) <= 0) || 
            (o2.getDirection().equals(Direction.SELL) &&
             o2.getPrice().compareTo(o1.getPrice()) <= 0)) {
            return true;
        }
        
        return false;
    }

}
