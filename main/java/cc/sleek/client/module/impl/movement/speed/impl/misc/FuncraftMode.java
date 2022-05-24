package cc.sleek.client.module.impl.movement.speed.impl.misc;

import cc.sleek.client.event.impl.MoveEvent;
import cc.sleek.client.event.impl.UpdateEvent;
import cc.sleek.client.module.impl.movement.speed.SpeedMode;
import cc.sleek.client.util.ChatUtil;
import cc.sleek.client.util.IPacketUtil;
import cc.sleek.client.util.PlayerUtil;
import net.minecraft.network.play.client.C03PacketPlayer;

public class FuncraftMode extends SpeedMode {

    private int stage;
    private float spood = 0.0F;
    private float distanceToLastPos = 0.0F;
    public FuncraftMode() {
        super("Funcraft");
    }

    @Override
    public void handleMove(MoveEvent event) {
        {
            if (mc.thePlayer.isMoving() && !mc.thePlayer.isCollidedHorizontally) {

                switch (stage) {
                    case 1:
                        event.setMotionY(mc.thePlayer.motionY = 0.42F);
                        spood = PlayerUtil.getBaseMoveSpeedFloat() * 1.5F;
                        stage = 2;
                        break;
                    case 3:
                        stage++;
                        spood *= getSpeed().getValue();
                        break;
                    case 2:
                        stage++;
                        float diff = (0.1F) * (distanceToLastPos - PlayerUtil.getBaseMoveSpeedFloat());
                        spood = (distanceToLastPos - diff);
                        break;

                    default:
                        if (stage != 0) {
                            stage++;
                        }

                        if (mc.thePlayer.isMovingOnGround()) {
                            stage = 1;
                            for (int i = 0; i < 7; i++) {
                                IPacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
                            }
                        }
                        spood -= spood / 159F;
                        break;
                }
                if (stage == 1) {
                    spood = 0.1F;
                }

            } else {
                spood = 0;
            }
            PlayerUtil.setSpeed(event, Math.max(stage == 1 ? 0.01 : spood, PlayerUtil.getBaseMoveSpeedFloat()));
        }
    }

    @Override
    public void handleUpdate(UpdateEvent event) {
        double xDifference = mc.thePlayer.posX - mc.thePlayer.lastTickPosX;
        double zDifference = mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ;
        distanceToLastPos = (float) Math.sqrt(xDifference * xDifference + zDifference * zDifference);
    }

    @Override
    public void onEnable() {
        stage = 0;
        spood = 0.0F;
        distanceToLastPos = 0.0F;
    }
}
