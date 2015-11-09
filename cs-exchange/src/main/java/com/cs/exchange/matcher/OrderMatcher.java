package com.cs.exchange.matcher;

import com.cs.exchange.domain.Order;

/**
 * Order matching interface
 *
 */
public interface OrderMatcher {

    /**
     * Check whether the two specified orders match or not
     * @param o1 first order
     * @param o2 second order
     * @return true if matched, else false
     */
    public boolean checkIfOrdersMatch(Order o1, Order o2);
    
}
