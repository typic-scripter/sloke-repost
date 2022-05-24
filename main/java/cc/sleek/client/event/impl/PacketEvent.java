package cc.sleek.client.event.impl;

import cc.sleek.client.event.Event;
import net.minecraft.network.Packet;

public class PacketEvent extends Event {
    private Packet packet;
    private final PacketDirection direction;

    public PacketEvent(Packet packet, PacketDirection direction) {
        this.packet = packet;
        this.direction = direction;
    }

    public enum PacketDirection {
        INCOMING, OUTGOING
    }

    public boolean isSending() {
        return direction == PacketDirection.OUTGOING;
    }

    public <T extends Packet> T getPacket() {
        return (T) packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }
}
