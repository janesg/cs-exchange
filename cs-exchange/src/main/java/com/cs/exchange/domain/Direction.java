package com.cs.exchange.domain;

/*
 * Enumerated type representing the direction of an order
 */
public enum Direction {
    BUY("Buy"), 
    SELL("Sell");

    private final String displayValue;

    private Direction(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }

}
