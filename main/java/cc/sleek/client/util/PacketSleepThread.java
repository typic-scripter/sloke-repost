package cc.sleek.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;

public class PacketSleepThread extends Thread {

    public PacketSleepThread(Packet packet, long delay) {
        super(() -> {
            try {
                if (Minecraft.getMinecraft().thePlayer == null) {
                    System.out.println("Not sending packet due too the player being null");
                    return;
                }
                sleep(delay);
                if (packet.getClass().isAssignableFrom(net.minecraft.network.play.INetHandlerPlayClient.class)) {
                    packet.processPacket(Minecraft.getMinecraft().getNetHandler());
                } else {
                    IPacketUtil.sendPacketNoEvent(packet);
                }
                ChatUtil.log("Sent packet");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "PacketSleepThread");
    }

}
