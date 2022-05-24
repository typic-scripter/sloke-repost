package cc.sleek.client.property.impl;

import cc.sleek.client.property.Value;

public class NumberValue<T extends Number> extends Value<T> {
    private T min, max, increment;

    public NumberValue(String name, T value, T min, T max, T increment) {
        super(name, value);
        this.min = min;
        this.max = max;
        this.increment = increment;
    }

    public NumberValue(String name, T value, T min, T max, T increment, Visibility visible) {
        super(name, value, visible);
        this.min = min;
        this.max = max;
        this.increment = increment;
    }

    public T getMin() {
        return min;
    }

    public void setMin(T min) {
        this.min = min;
    }

    public T getMax() {
        return max;
    }

    public void setMax(T max) {
        this.max = max;
    }

    public T getIncrement() {
        return increment;
    }

    public void setIncrement(T increment) {
        this.increment = increment;
    }
}
