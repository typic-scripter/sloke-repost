package cc.sleek.client.util;

import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

/**
 * @author Moshi
 */
public class TimedPacket implements IUtil {

    private Packet<?> packet;
    private long time;

    public TimedPacket(Packet packet, long time) {
        this.time = time;
        this.packet = packet;
    }

    public long postAddTime() {
        return System.currentTimeMillis() - time;
    }

    public void send() {
        IPacketUtil.sendPacket(this.getPacket());
    }

    public void sendSilent() {
        if (getPacket().getClass().isAssignableFrom(net.minecraft.network.play.INetHandlerPlayClient.class)) {
            getPacket().processPacket(mc.getNetHandler());
        } else {
            IPacketUtil.sendPacketNoEvent(this.getPacket());
        }
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Packet getPacket() {
        return packet;
    }

    public void setPacket(Packet<?> packet) {
        this.packet = packet;
    }
}