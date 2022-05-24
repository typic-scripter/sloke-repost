package cc.sleek.client.util;

import cc.sleek.client.event.impl.PacketEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.*;

import java.util.function.Consumer;

public interface IPacketUtil {
    static void sendPacket(Packet packet) {
        Minecraft.getMinecraft().getNetHandler().getNetworkManager().sendPacket(packet);
    }
    static void sendPacketNoEvent(Packet packet) {
        Minecraft.getMinecraft().getNetHandler().getNetworkManager().sendPacketNoEvent(packet);
    }
    static void sendPacketNoEvent(Packet packet, int reps) {
        for (int i = 0; i < reps; i++) {
            Minecraft.getMinecraft().getNetHandler().getNetworkManager().sendPacketNoEvent(packet);
        }
    }
    static void sendDelayPacket(Packet packet, long delay) {
        PacketSleepThread packetSleepThread = new PacketSleepThread(packet, delay);
        packetSleepThread.start();
    }

    // Client

    default void onCPacketAnimation(PacketEvent e, Consumer<C0APacketAnimation> consumer) {
        if (e.getPacket() instanceof C0APacketAnimation) {
            consumer.accept(e.getPacket());
        }
    }

    default void onCPacketInput(PacketEvent e, Consumer<C0CPacketInput> consumer) {
        if (e.getPacket() instanceof C0CPacketInput) {
            consumer.accept(e.getPacket());
        }
    }

    default void onCClickWindow(PacketEvent e, Consumer<C0EPacketClickWindow> consumer) {
        if (e.getPacket() instanceof C0EPacketClickWindow) {
            consumer.accept(e.getPacket());
        }
    }

    default void onCPacketChat(PacketEvent e, Consumer<C01PacketChatMessage> consumer) {
        if (e.getPacket() instanceof C01PacketChatMessage) {
            consumer.accept(e.getPacket());
        }
    }

    default void onCUseEntity(PacketEvent e, Consumer<C02PacketUseEntity> consumer) {
        if (e.getPacket() instanceof C02PacketUseEntity) {
            consumer.accept(e.getPacket());
        }
    }

    default void onCPacketDigging(PacketEvent e, Consumer<C07PacketPlayerDigging> consumer) {
        if (e.getPacket() instanceof C07PacketPlayerDigging) {
            consumer.accept(e.getPacket());
        }
    }

    default void onCBlockPlacement(PacketEvent e, Consumer<C08PacketPlayerBlockPlacement> consumer) {
        if (e.getPacket() instanceof C08PacketPlayerBlockPlacement) {
            consumer.accept(e.getPacket());
        }
    }

    default void onCHeldItem(PacketEvent e, Consumer<C09PacketHeldItemChange> consumer) {
        if (e.getPacket() instanceof C09PacketHeldItemChange) {
            consumer.accept(e.getPacket());
        }
    }

    default void onCPlayerCapabilties(PacketEvent e, Consumer<C13PacketPlayerAbilities> consumer) {
        if (e.getPacket() instanceof C13PacketPlayerAbilities) {
            consumer.accept(e.getPacket());
        }
    }

    default void onCCloseWindow(PacketEvent e, Consumer<C0DPacketCloseWindow> consumer) {
        if (e.getPacket() instanceof C0DPacketCloseWindow) {
            consumer.accept(e.getPacket());
        }
    }

    default void onCPacketPlayer(PacketEvent e, Consumer<C03PacketPlayer> consumer) {
        if (e.getPacket() instanceof C03PacketPlayer) {
            consumer.accept(e.getPacket());
        }
    }

    default void onCTransaction(PacketEvent e, Consumer<C0FPacketConfirmTransaction> consumer) {
        if (e.getPacket() instanceof C0FPacketConfirmTransaction) {
            consumer.accept(e.getPacket());
        }
    }

    default void onCKeepAlive(PacketEvent e, Consumer<C00PacketKeepAlive> consumer) {
        if (e.getPacket() instanceof C00PacketKeepAlive) {
            consumer.accept(e.getPacket());
        }
    }

    default void onCEntityAction(PacketEvent e, Consumer<C0BPacketEntityAction> consumer) {
        if (e.getPacket() instanceof C0BPacketEntityAction) {
            consumer.accept(e.getPacket());
        }
    }

    // Server

    default void onSPosLook(PacketEvent e, Consumer<S08PacketPlayerPosLook> consumer) {
        if (e.getPacket() instanceof S08PacketPlayerPosLook) {
            consumer.accept(e.getPacket());
        }
    }

    default void onSVelocity(PacketEvent e, Consumer<S12PacketEntityVelocity> consumer) {
        if (e.getPacket() instanceof S12PacketEntityVelocity) {
            consumer.accept(e.getPacket());
        }
    }

    default void onSRespawn(PacketEvent e, Consumer<S07PacketRespawn> consumer) {
        if (e.getPacket() instanceof S07PacketRespawn) {
            consumer.accept(e.getPacket());
        }
    }

    default void onSOpenWindow(PacketEvent e, Consumer<S2DPacketOpenWindow> consumer) {
        if (e.getPacket() instanceof S2DPacketOpenWindow) {
            consumer.accept(e.getPacket());
        }
    }

    default void onSCloseWindow(PacketEvent e, Consumer<S2EPacketCloseWindow> consumer) {
        if (e.getPacket() instanceof S2EPacketCloseWindow) {
            consumer.accept(e.getPacket());
        }
    }

    // Utils

    default TimedPacket create(Packet<?> p) {
        return new TimedPacket(p, System.currentTimeMillis());
    }
}
