package cc.sleek.client.property.impl;

import cc.sleek.client.property.Value;

import java.util.ArrayList;
import java.util.List;

public class EnumValue<T extends Enum<T>> extends Value<T> {
    private T[] choices;
    public EnumValue(String name, T[] values) {
        super(name, values[0]);
        choices = values;
    }

    public EnumValue(String name, T[] values, Visibility visible) {
        super(name, values[0], visible);
        choices = values;
    }

    public T[] getChoices() {
        return choices;
    }

    public void setValueString(String val) {
        for (T enom : choices) {
            if (enom.toString().equalsIgnoreCase(val)) {
                setValue(enom);
                return;
            }
        }
    }

    public void setChoices(T[] choices) {
        this.choices = choices;
    }
}
