package cc.sleek.client.module.impl.movement.speed.impl.misc;

import cc.sleek.client.event.impl.StrafeEvent;
import cc.sleek.client.module.impl.movement.speed.SpeedMode;
import cc.sleek.client.util.PlayerUtil;

public class PacketGroundMode extends SpeedMode {
    public PacketGroundMode() {
        super("Packet Ground");
    }

    @Override
    public void handleStrafe(StrafeEvent strafeEvent) {
        PlayerUtil.teleportOnGround(getSpeed().getValue(), 0.0f);
    }
}
