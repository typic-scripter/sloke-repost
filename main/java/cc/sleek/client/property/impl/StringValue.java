package cc.sleek.client.property.impl;

import cc.sleek.client.property.Value;

public class StringValue extends Value<String> {
    public StringValue(String name, String value) {
        super(name, value);
    }

    public StringValue(String name, String value, Visibility visible) {
        super(name, value, visible);
    }
}
