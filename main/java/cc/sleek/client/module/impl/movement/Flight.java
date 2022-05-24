package cc.sleek.client.module.impl.movement;

import cc.sleek.client.event.impl.CollideEvent;
import cc.sleek.client.event.impl.MoveEvent;
import cc.sleek.client.event.impl.PacketEvent;
import cc.sleek.client.event.impl.UpdateEvent;
import cc.sleek.client.module.Module;
import cc.sleek.client.module.api.Category;
import cc.sleek.client.module.api.ModuleInfo;
import cc.sleek.client.property.impl.BooleanValue;
import cc.sleek.client.property.impl.EnumValue;
import cc.sleek.client.property.impl.NumberValue;
import cc.sleek.client.util.*;
import com.sun.javafx.util.TempState;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.block.BlockAir;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

import java.util.concurrent.ThreadLocalRandom;

@ModuleInfo(
        name = "Flight",
        description = "I believe I can fly",
        category = Category.MOVEMENT
)
public class Flight extends Module {

    private final EnumValue<Modes> mode = new EnumValue<>("Mode", Modes.values());

    private final NumberValue<Double> speed = new NumberValue<>("Speed", 1.2, 0.0, 8.0, 0.1);
    private final NumberValue<Double> launchMotY = new NumberValue<>("Motion Y", 0.5, 0.1, 5.0, 0.001, () -> mode.getValue() == Modes.LAUNCH);
    private final BooleanValue jump = new BooleanValue("Jump", false, () -> mode.getValue() == Modes.CLIP);
    private final BooleanValue zeroMotion = new BooleanValue("No Motion before clip", true, () -> mode.getValue() == Modes.CLIP);
    private final BooleanValue silentClip = new BooleanValue("Silent", false, () -> mode.getValue() == Modes.CLIP);
    private final BooleanValue clipMultipleTimes = new BooleanValue("Clip multiple times", false, () -> mode.getValue() == Modes.CLIP);
    private final NumberValue<Integer> clipTimes = new NumberValue<>("Clip Times", 2, 1, 100, 1, () -> mode.getValue() == Modes.CLIP && clipMultipleTimes.getValue());
    private final BooleanValue clipUntilS08 = new BooleanValue("Clip until lagback", false, () -> mode.getValue() == Modes.CLIP && !clipMultipleTimes.getValue());
    private final NumberValue<Float> clipAmount = new NumberValue<>("Clip Amount", 0.0784F, 0.05F, 0.5F, 0.01F, () -> mode.getValue() == Modes.CLIP);
    private final BooleanValue damageBoost = new BooleanValue("Damage", false, () -> mode.getValue() == Modes.OLD_NCP);
    private final BooleanValue timerBoost = new BooleanValue("Timer Boost", false, () -> mode.getValue() == Modes.OLD_NCP);
    private final EnumValue<OldNCPThing> ncp_method = new EnumValue<>("NCP Method", OldNCPThing.values(), () -> mode.getValue() == Modes.OLD_NCP);
    double spood = 0.0F;
    int stage = 0;
    double distanceToLastPos = 0F;
    int thing = 0;
    private double startY;
    private boolean damaged = false;
    private boolean clipped = false;


    public Flight() {

    }

    @Override
    public void onEnable() {
        stage = 0;
        startY = mc.thePlayer.posY - 1.0;
        damaged = false;
        distanceToLastPos = 0.0F;

        if (mode.getValue() == Modes.LAUNCH) {
            mc.thePlayer.setSprinting(true);
            PlayerUtil.setSpeed(speed.getValue());
            mc.thePlayer.motionY += launchMotY.getValue();
            this.toggle();
        }
        if (mode.getValue() == Modes.VERUS_DAMAGE) {
            PlayerUtil.damageVerus();
       //     mc.thePlayer.motionY = 0.42F;
        }
        clipped = false;
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1.0f;

        mc.thePlayer.motionZ = 0;
        mc.thePlayer.motionX = 0;
    }

    @EventLink
    private final Listener<MoveEvent> moveEventListener = event -> {
        switch (mode.getValue()) {
            case VIPERMC_GLIDE: {
                mc.timer.timerSpeed = 0.2f;
                for (int i = 0; i < 17; ++i) {
                    PlayerUtil.teleportOnGround((PlayerUtil.isMoving() ? 0.38 : 0), 0);
                }
                break;
            }
            case OLD_NCP: {
                if (mc.thePlayer.isMoving() && !mc.thePlayer.isCollidedHorizontally) {
                    if (stage > 1 && stage < 25 && timerBoost.getValue()) {
                        mc.timer.timerSpeed = 3.0F;
                    } else {
                        mc.timer.timerSpeed = 1.0F;
                    }
                    switch (stage) {
                        case 1:
                            if (mc.thePlayer.hurtResistantTime == 19 || !damageBoost.getValue()) {

                                event.setMotionY(mc.thePlayer.motionY = 0.42F);
                                spood = PlayerUtil.getBaseMoveSpeedFloat() * 1.5F;
                                stage = 2;
                            }

                            break;
                        case 2:
                            stage++;
                            spood *= speed.getValue().floatValue();
                            break;
                        case 3:
                            stage++;
                            double diff = (damageBoost.getValue() ? 0.01F : 0.1F) * (distanceToLastPos - PlayerUtil.getBaseMoveSpeed());
                            spood = (distanceToLastPos - diff);
                            break;

                        default:
                            if (stage != 0) {
                                stage++;
                            }

                            if (mc.thePlayer.isMovingOnGround()) {
                                if (damageBoost.getValue()) {
                                    if (!damaged) {
                                        PlayerUtil.damageHypixel();
                                        damaged = true;
                                    }
                                }
                                stage = 1;


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
                break;
            }
        }
    };



    @EventLink
    Listener<UpdateEvent> updateEventListener = event -> {
        setSuffix(StringUtil.getModeName(mode.getValue().name()));
        if (mode.getValue() == Modes.OLD_NCP) {
            setSuffix(String.format("%s [%s]", StringUtil.getModeName(mode.getValue().name()), StringUtil.getModeName(ncp_method.getValue().name())));
        }

        switch (mode.getValue()) {
            case VANILLA:
                // good code
                if (mc.gameSettings.keyBindJump.isKeyDown()) {
                    mc.thePlayer.motionY = speed.getValue() / 2;
                } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                    mc.thePlayer.motionY = -(speed.getValue() / 2);
                } else {
                    mc.thePlayer.motionY = 0.0F;
                }
                PlayerUtil.setSpeed(speed.getValue());
                break;
            case VIPERMC_GLIDE:
                mc.thePlayer.motionY = 0.0F;
                if (mc.gameSettings.keyBindJump.isKeyDown()) {
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 175.25E-02, mc.thePlayer.posZ);
                } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 8.125E-02, mc.thePlayer.posZ);
                } else {
                    event.setY(Math.round(mc.thePlayer.posY));
                }

                event.setOnGround(true);
                break;
            case ZONECRAFT: {
                if (event.isPre()) {
                    mc.thePlayer.motionY = 0.0F;
                    if (stage == 1) {
                        mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY  + 0.42F, mc.thePlayer.posZ);
                        stage = 2;
                    } else {
                        if (mc.thePlayer.isMovingOnGround()) {
                            stage = 1;
                        }
                    }

                    if (stage > 1) {
                        mc.thePlayer.motionX = mc.thePlayer.motionZ = 0.0F;
                        final double posX = mc.thePlayer.posX;
                        final double posY = mc.thePlayer.posY;
                        final double posZ = mc.thePlayer.posZ;
                        final double x = -Math.sin(PlayerUtil.getDirection()) * (PlayerUtil.getBaseMoveSpeedFloat() * 0.9F);
                        final double y = mc.thePlayer.ticksExisted % 2 == 0 ? -0.017 : 0;
                        final double z = Math.cos(PlayerUtil.getDirection()) * (PlayerUtil.getBaseMoveSpeedFloat() * 0.9F);
                        mc.thePlayer.setPosition(posX + x, (posY + y), posZ + z);
                    }
                }
                break;
            }
            case ZONECRAFT_GLIDE: {
                if (event.isPre()) {
                    mc.thePlayer.motionY = 0.0F;
                    if (stage == 1) {
                        mc.thePlayer.motionY = .42F;
                        stage = 2;
                    } else {
                        if (mc.thePlayer.isMovingOnGround()) {
                            stage = 1;
                        }
                    }

                    if (stage > 1) {
                        mc.thePlayer.motionX = mc.thePlayer.motionZ = 0.0F;
                        final double posX = mc.thePlayer.posX;
                        final double posY = mc.thePlayer.posY;
                        final double posZ = mc.thePlayer.posZ;
                        final double x = -Math.sin(PlayerUtil.getDirection()) * (PlayerUtil.getBaseMoveSpeedFloat() * 0.9F);
                        final double z = Math.cos(PlayerUtil.getDirection()) * (PlayerUtil.getBaseMoveSpeedFloat() * 0.9F);
                        mc.thePlayer.setPosition(posX + x, (posY - 0.015625F), posZ + z);
                    }
                }
                break;
            }
            case VERUS_JUMP:
                if (mc.thePlayer.isMovingOnGround() && event.isPre()) {
                    mc.thePlayer.motionY = 0.42F;
                }
                break;
            case TEST: {
                if (event.isPre()) {
                    if (mc.thePlayer.onGround) {
                        stage++;
                    }
                    switch (stage) {
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                            event.setOnGround(false);
                            if (mc.thePlayer.onGround) {

                                mc.thePlayer.jump();
                            }
                            break;
                        case 5:
                            event.setOnGround(true);
                            break;
                        case 6:
                            mc.thePlayer.jump();
                            stage++;
                            break;

                        default:
                            stage++;
                            if (stage >= 10) {
                                mc.thePlayer.motionY = -0.2F;
                            }
                            break;

                    }
                    ChatUtil.log(stage, mc.thePlayer.onGround);
                }
                break;
            }
            case VIPERMC_OTHER:
                mc.thePlayer.onGround = true;
                if (mc.thePlayer.motionY < 0) {
                    // Mess around with the values here
                    mc.thePlayer.motionY = 0.225;
                }
                break;
            case VERUS_DAMAGE: {
                if (event.isPre()) {
                    if (mc.thePlayer.onGround) {
                        mc.thePlayer.motionY =  0.42F;
                    }

                if (mc.thePlayer.hurtTime > 0) {
                    spood = speed.getValue();
                } else {
                    spood = 0.22F;
                }
                PlayerUtil.setSpeed(spood);
            }
                break;
            }
            case HYPIXEL_TEST: {
                if (event.isPre()) {
                    stage++;
                    switch (stage) {
                        case 1:
                            PlayerUtil.damageHypixel();
                            break;
                        default:
                            mc.thePlayer.motionY = 0.0F;
                            mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + ThreadLocalRandom.current().nextDouble(6E-9, 10E-9), mc.thePlayer.posZ);
                            break;
                    }
                }
                break;
            }
            case OLD_NCP:
                if (event.isPre()) {
                    double xDifference = mc.thePlayer.posX - mc.thePlayer.lastTickPosX;
                    double zDifference = mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ;
                    distanceToLastPos = Math.sqrt(xDifference * xDifference + zDifference * zDifference);


                    if (stage > 1) {
                        mc.thePlayer.motionY = 0.0F;
                        switch (ncp_method.getValue()) {
                            case NORMAL:
                                mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 1E-4, mc.thePlayer.posZ);
                                break;
                            case VL_ABUSE:
                                mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1E-4, mc.thePlayer.posZ);
                                break;
                            case HYPIXEL_1:
                                mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 6E-9, mc.thePlayer.posZ);
                                break;
                            case HYPIXEL_2:
                                mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + ThreadLocalRandom.current().nextDouble(1E-6, 1E-3), mc.thePlayer.posZ);
                                break;
                        }

                    }
                }

                break;
            case CLIP: {
                if (event.isPre()) {
                    switch (stage) {
                        case 0:
                            if (zeroMotion.getValue()) {
                                PlayerUtil.setSpeed(0.0F);
                            }
                            if (PlayerUtil.isOnGround()) {
                                if (jump.getValue()) {
                                    mc.thePlayer.jump();
                                    stage = 1;
                                } else if (!clipped || clipUntilS08.getValue() || clipMultipleTimes.getValue()) {
                                    if (clipMultipleTimes.getValue()) {
                                        for (int i = 0; i < clipTimes.getValue(); i++) {
                                            if (silentClip.getValue())
                                                IPacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - clipAmount.getValue(), mc.thePlayer.posZ,false));
                                            else
                                                mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - clipAmount.getValue(), mc.thePlayer.posZ);
                                        }
                                        clipped = true;
                                    } else {
                                        if (silentClip.getValue())
                                            IPacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - clipAmount.getValue(), mc.thePlayer.posZ,false));
                                        else
                                            mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - clipAmount.getValue(), mc.thePlayer.posZ);
                                        clipped = true;
                                    }
                                }
                            }
                            break;
                        case 1:

                            if (jump.getValue()) {
                                if (zeroMotion.getValue()) {
                                    PlayerUtil.setSpeed(0.0F);
                                }
                                if (PlayerUtil.isOnGround()) {
                                    if (!clipped || clipUntilS08.getValue()) {
                                        if (clipMultipleTimes.getValue()) {
                                            for (int i = 0; i < clipTimes.getValue(); i++) {
                                                if (silentClip.getValue())
                                                    IPacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - clipAmount.getValue(), mc.thePlayer.posZ,false));
                                                else
                                                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - clipAmount.getValue(), mc.thePlayer.posZ);
                                                clipped = true;
                                            }
                                        } else {
                                            if (silentClip.getValue())
                                                IPacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - clipAmount.getValue(), mc.thePlayer.posZ,false));
                                            else
                                                mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - clipAmount.getValue(), mc.thePlayer.posZ);
                                            clipped = true;
                                        }
                                    }
                                }
                            }
                            break;
                        default:
                            stage++;
                            if ((thing == clipTimes.getValue() || !clipMultipleTimes.getValue()) || silentClip.getValue()) {
                                mc.thePlayer.motionY = 0.0F;
                                PlayerUtil.setSpeed(PlayerUtil.getBaseMoveSpeedFloat());
                            }
                            if (stage == 3) {
//                                PlayerUtil.damageHypixel();
                            }
                            if (stage > 3) {
//                                mc.thePlayer.setPosition();
                                event.setY(event.getY() - 1E-4);
                            }
                            break;
                    }
                }
                break;
            }


        }
    };

    @EventLink
    Listener<PacketEvent> packetEventListener = event -> {
        switch (mode.getValue()) {
            case CLIP: {
                if (event.getPacket() instanceof S08PacketPlayerPosLook) {
                    if (clipped) {
                        stage = 2;

                    }
                }
                break;
            }
//            case TEST:
//                if (event)
        }
    };


    @EventLink
    Listener<CollideEvent> collisionEventListener = event -> {
        switch (mode.getValue()) {
            case VERUS_DAMAGE:
            case VERUS_JUMP:
                if (event.getBlock() instanceof BlockAir) {
                    if (event.getY() <= startY) {
                        event.setAxisAlignedBB(AxisAlignedBB.fromBounds(-5, -1, -5, 5, 1.0F, 5).offset(event.getX(), event.getY(), event.getZ()));
                    }
                }
        }
    };

    /**
     * modes goofy
     */
    public enum Modes {
        VANILLA, VIPERMC_GLIDE, VIPERMC_OTHER, VERUS_JUMP, ZONECRAFT, ZONECRAFT_GLIDE, TEST, VERUS_DAMAGE, LAUNCH, OLD_NCP, CLIP, HYPIXEL_TEST
    }

    public enum OldNCPThing {
        NORMAL, VL_ABUSE, HYPIXEL_1, HYPIXEL_2
    }

}
