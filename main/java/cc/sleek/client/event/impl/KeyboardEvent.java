package cc.sleek.client.event.impl;

import cc.sleek.client.event.Event;

public class KeyboardEvent extends Event {
    private final int keyCode;

    public KeyboardEvent(int key) {
        this.keyCode = key;
    }

    @SuppressWarnings("all")
    public int getKeyCode() {
        return this.keyCode;
    }
}