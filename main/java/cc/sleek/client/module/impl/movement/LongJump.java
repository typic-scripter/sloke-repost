package cc.sleek.client.module.impl.movement;

import cc.sleek.client.event.impl.MoveEvent;
import cc.sleek.client.event.impl.PacketEvent;
import cc.sleek.client.event.impl.UpdateEvent;
import cc.sleek.client.module.Module;
import cc.sleek.client.module.api.Category;
import cc.sleek.client.module.api.ModuleInfo;
import cc.sleek.client.property.impl.BooleanValue;
import cc.sleek.client.property.impl.EnumValue;
import cc.sleek.client.property.impl.NumberValue;
import cc.sleek.client.util.ChatUtil;
import cc.sleek.client.util.IPacketUtil;
import cc.sleek.client.util.PlayerUtil;
import cc.sleek.client.util.Stopwatch;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

@ModuleInfo(
        name = "LongJump",
        description = "Makes you jump farther",
        category = Category.MOVEMENT
)
public class LongJump extends Module {

    private final Stopwatch stopwatch = new Stopwatch();
    private final EnumValue<Mode> mode = new EnumValue<>("Mode", Mode.values());
    private final NumberValue<Double> speed = new NumberValue<>("Speed", 1.0, 0.0, 10.0, 0.1);
    private final NumberValue<Float> height = new NumberValue<>("Vertical", 0.42F, 0.05F, 1.5F, 0.1F);
    private final EnumValue<DamageMode> damageMode = new EnumValue<>("Damage Mode", DamageMode.values());
    private final BooleanValue autoShoot = new BooleanValue("Auto Shoot", true, () -> mode.getValue() == Mode.NCP && damageMode.getValue() == DamageMode.BOW);
    private final BooleanValue timerShootValue = new BooleanValue("Timer Shoot", true, () -> autoShoot.getValue() && mode.getValue() == Mode.NCP && damageMode.getValue() == DamageMode.BOW);
    private final BooleanValue test = new BooleanValue("test", true, () -> autoShoot.getValue() && mode.getValue() == Mode.NCP);
    @EventLink
    Listener<PacketEvent> packetEventListener = event -> {

    };
    private boolean damaged;
    private int ticks = 0;
    private int stage;
    private double distanceToLastPos;
    @EventLink
    Listener<UpdateEvent> updateEventListener = event -> {
        switch (mode.getValue()) {
            case VANILLA:
                if (mc.thePlayer.isMovingOnGround()) {
                    mc.thePlayer.motionY = height.getValue().doubleValue();
                }
                PlayerUtil.setSpeed(speed.getValue());
                break;
            case NCP:
                if (event.isPre()) {
                    if (mc.thePlayer.hurtTime == 9 && !damaged) {
                        mc.thePlayer.motionY = height.getValue().doubleValue();
                        damaged = true;
                    }
                    double x = mc.thePlayer.posX - mc.thePlayer.lastTickPosX;
                    double z = mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ;
                    distanceToLastPos = Math.sqrt(x * x + z * z);

                    if (!damaged) {


                        switch (damageMode.getValue()) {
                            case SAFE:
                                if (mc.thePlayer.onGround) {
                                    ticks++;
                                }
                                switch (ticks) {
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
                                }
                                break;
                            case PACKET:
                                ticks++;
                                if (ticks == 1) {
                                    PlayerUtil.damageHypixel();
                                }

                                break;
                            case EDIT:
                                ticks++;
                                if (ticks < 97) {
                                    ChatUtil.log(ticks);
                                    event.setCancelled(true);
                                } else if (ticks == 97) {
                                    PlayerUtil.damageHypixel();
                                }
                                break;
                            case BOW:
                                ticks++;
                                event.setPitch(-89.9F);

                                if (!timerShootValue.getValue()) {
                                    switch (ticks) {
                                        case 1:
                                            IPacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                                            break;
                                        case 5:
                                            IPacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                                            break;
                                    }

                                } else if (ticks == 1) {
                                    IPacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                                    IPacketUtil.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, event.getYaw(), event.getPitch(), true));
                                    IPacketUtil.sendPacketNoEvent(new C03PacketPlayer(true));
                                    IPacketUtil.sendPacketNoEvent(new C03PacketPlayer(true));
                                    IPacketUtil.sendPacketNoEvent(new C03PacketPlayer(true));
                                    IPacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                                    ticks = 5;
                                }
                        }
                    }

                }
                break;
            case FUNCRAFT:
                if (event.isPre()) {
                    double x = mc.thePlayer.posX - mc.thePlayer.lastTickPosX;
                    double z = mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ;
                    distanceToLastPos = Math.sqrt(x * x + z * z);
                }
                break;
        }
    };
    private double spood = 0.0;
    @EventLink
    Listener<MoveEvent> moveEventListener = event -> {
        switch (mode.getValue()) {
            case NCP:
                if (!damaged) {
                    event.setMotionX(mc.thePlayer.motionX = 0.0);
                    event.setMotionZ(mc.thePlayer.motionZ = 0.0);

                }
                if (damaged) {

                    if (mc.thePlayer.isMoving()) {
                        switch (stage) {
                            case 1:
                                spood = PlayerUtil.getBaseMoveSpeedFloat() * 1.75F;
                                stage = 2;
                                break;
                            case 2:
                                stage++;
                                spood *= speed.getValue();
                                break;
                            case 3:
                                stage++;
                                double diff = 0.01 * (distanceToLastPos - PlayerUtil.getBaseMoveSpeed());
                                spood = distanceToLastPos - diff;
                                break;
                            default:

                                if (mc.thePlayer.isMovingOnGround() || mc.thePlayer.hurtResistantTime == 18) {
                                    if (stage == 4) {
                                        toggle();
                                    }
                                    stage = 1;
                                }
                                if (test.getValue()) {
                                    if ((event.getMotionY() < 0.0F && test.getValue())) {
                                        event.setMotionY(mc.thePlayer.motionY = (event.getMotionY() * 0.70));
                                    }
                                }
                                spood *= 0.91F;
                                break;
                        }
                    }
                    PlayerUtil.setSpeed(event, Math.max(PlayerUtil.getBaseMoveSpeedFloat(), spood));
                }
                break;
            case FUNCRAFT:
                if (mc.thePlayer.isMoving()) {
                    switch (stage) {
                        case 1:
                            event.setMotionY(mc.thePlayer.motionY = 0.42F);
                            spood = PlayerUtil.getBaseMoveSpeedFloat() * 1.75F;
                            stage = 2;
                            break;
                        case 2:
                            stage++;
                            spood *= speed.getValue();
                            break;
                        case 3:
                            stage++;
                            double diff = 0.7 * (distanceToLastPos - PlayerUtil.getBaseMoveSpeed());
                            spood = distanceToLastPos - diff;
                            break;
                        default:

                            if (mc.thePlayer.isMovingOnGround()) {
                                if (stage == 4) {
                                    toggle();
                                }
                                stage = 1;
                            }
                            spood = distanceToLastPos - distanceToLastPos / 152F;
                            break;
                    }
                }
                PlayerUtil.setSpeed(event, Math.max(PlayerUtil.getBaseMoveSpeedFloat(), spood));
                break;
        }
    };


    public LongJump() {

    }

    @Override
    public void onEnable() {
        damaged = false;
        ticks = 0;
        spood = 0;
        stage = 0;
        distanceToLastPos = 0;
    }

    private enum Mode {
        VANILLA, NCP, FUNCRAFT
    }

    private enum DamageMode {
        PACKET, EDIT, BOW, SAFE;
    }
}
