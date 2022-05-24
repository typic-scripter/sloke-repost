package cc.sleek.client.module.impl.movement;

import cc.sleek.client.event.impl.FrictionEvent;
import cc.sleek.client.event.impl.MoveEvent;
import cc.sleek.client.event.impl.StrafeEvent;
import cc.sleek.client.event.impl.UpdateEvent;
import cc.sleek.client.module.Module;
import cc.sleek.client.module.api.Category;
import cc.sleek.client.module.api.ModuleInfo;
import cc.sleek.client.module.impl.movement.speed.SpeedMode;
import cc.sleek.client.module.impl.movement.speed.impl.hypixel.HypixelDosMode;
import cc.sleek.client.module.impl.movement.speed.impl.hypixel.HypixelMode;
import cc.sleek.client.module.impl.movement.speed.impl.misc.*;
import cc.sleek.client.module.impl.movement.speed.impl.verus.VerusMode;
import cc.sleek.client.property.impl.BooleanValue;
import cc.sleek.client.property.impl.EnumValue;
import cc.sleek.client.property.impl.NumberValue;
import cc.sleek.client.util.PlayerUtil;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;

@ModuleInfo(name = "Speed", description = "Increases the player's speed", category = Category.MOVEMENT)
public class Speed extends Module {

    private final EnumValue<Modes> mode = new EnumValue<>("Mode", Modes.values());
    private final BooleanValue useFriction = new BooleanValue("Use Friction", false);
    private final EnumValue<FrictionModes> frictionMode = new EnumValue<>("Friction Mode", FrictionModes.values(), () -> useFriction.getValue() || mode.getValue() == Modes.FRICTION);
    private final NumberValue<Float> speed = new NumberValue<Float>("Speed", 1.2f, 0.0f, 8.0f, 0.1f);


    @EventLink
    private final Listener<MoveEvent> moveEventListener = event -> mode.getValue().handler.handleMove(event);
    @EventLink
    private final Listener<StrafeEvent> strafeEventListener = event -> mode.getValue().handler.handleStrafe(event);
    @EventLink
    Listener<UpdateEvent> updateEventListener = event -> {
        setSuffix(mode.getValue().getName());
        mode.getValue().handler.handleUpdate(event);
    };
    @EventLink
    Listener<FrictionEvent> frictionEventListener = event -> mode.getValue().handler.handleFriction(event);

    @Override
    public void onEnable() {
        mc.timer.timerSpeed = 1.0f;
        mode.getValue().handler.onEnable();
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1.0f;
        PlayerUtil.setSpeed(0);
        mode.getValue().handler.onDisable();
    }

    public float handleFriction(float speed) {
        if (!PlayerUtil.isOnGround()) { // check if really onground
            speed = frictionMode.getValue().getFriction(speed);
            return Math.max(speed, PlayerUtil.getBaseMoveSpeedFloat());
        } else return speed;
    }


    private enum Modes {
        BHOP(new BhopMode()), HYPIXEL(new HypixelMode()), HYPIXEL2(new HypixelDosMode()), PACKET_GROUND(new PacketGroundMode()), FUNCRAFT(new FuncraftMode()), FRICTION(new FrictionMode()), VERUS(new VerusMode()), HVH_HOP(new HvHHopMode());
        private final SpeedMode handler;
        private final String name;
        Modes(SpeedMode mode) {
            this.handler = mode;
            this.name = handler.getName();
        }

        public String getName() {
            return name;
        }
    }

    private enum FrictionModes {
        NCP, LEGIT, TEST;
        public float getFriction(float speed) {
            switch (this) {
                case NCP:
                    speed -= speed / 159.0F;
                    break;
                case LEGIT:
                    speed *= 0.91F;
                    break;
                case TEST:
                    speed *= 0.98F;
                    break;
            }
            return speed;
        }
    }

}
