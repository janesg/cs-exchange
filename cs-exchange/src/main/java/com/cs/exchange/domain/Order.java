package com.cs.exchange.domain;

import java.math.BigDecimal;

/**
 * Simple equity order
 *
 */
public class Order {

    private final Direction direction;
    private final String ric;
    private final BigDecimal quantity;
    private final BigDecimal price;
    private final String user;

    public Order(Direction direction, String ric, BigDecimal quantity, BigDecimal price, String user) {
        this.direction = direction;
        this.ric = ric;
        this.quantity = quantity;
        this.price = price;
        this.user = user;
    }

    public Direction getDirection() {
        return direction;
    }

    public String getRic() {
        return ric;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getUser() {
        return user;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((direction == null) ? 0 : direction.hashCode());
        result = prime * result + ((price == null) ? 0 : price.hashCode());
        result = prime * result + ((quantity == null) ? 0 : quantity.hashCode());
        result = prime * result + ((ric == null) ? 0 : ric.hashCode());
        result = prime * result + ((user == null) ? 0 : user.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Order)) {
            return false;
        }
        Order other = (Order) obj;
        if (direction != other.direction) {
            return false;
        }
        if (price == null) {
            if (other.price != null) {
                return false;
            }
        } else if (!price.equals(other.price)) {
            return false;
        }
        if (quantity == null) {
            if (other.quantity != null) {
                return false;
            }
        } else if (!quantity.equals(other.quantity)) {
            return false;
        }
        if (ric == null) {
            if (other.ric != null) {
                return false;
            }
        } else if (!ric.equals(other.ric)) {
            return false;
        }
        if (user == null) {
            if (other.user != null) {
                return false;
            }
        } else if (!user.equals(other.user)) {
            return false;
        }
        return true;
    }

}
