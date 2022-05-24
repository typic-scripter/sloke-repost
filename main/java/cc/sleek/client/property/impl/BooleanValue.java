package cc.sleek.client.property.impl;

import cc.sleek.client.property.Value;

public class BooleanValue extends Value<Boolean> {
    public BooleanValue(String name, boolean value) {
        super(name, value);
    }

    public BooleanValue(String name, boolean value, Visibility visible) {
        super(name, value, visible);
    }
}
