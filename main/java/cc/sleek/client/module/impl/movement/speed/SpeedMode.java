package cc.sleek.client.module.impl.movement.speed;

import cc.sleek.client.Sleek;
import cc.sleek.client.event.impl.FrictionEvent;
import cc.sleek.client.event.impl.MoveEvent;
import cc.sleek.client.event.impl.StrafeEvent;
import cc.sleek.client.event.impl.UpdateEvent;
import cc.sleek.client.module.impl.movement.Speed;
import cc.sleek.client.property.impl.NumberValue;
import cc.sleek.client.util.IUtil;

public abstract class SpeedMode implements IUtil {

    private final String name;

    public SpeedMode(String name) {
        this.name = name;
    }

    public void onEnable() {}
    public void onDisable() {}
    public void handleStrafe(StrafeEvent event) {}
    public void handleMove(MoveEvent event) {}
    public void handleFriction(FrictionEvent event) {}
    public void handleUpdate(UpdateEvent event) {}

    public Speed getSpeedMod() {
        return Sleek.INSTANCE.getModuleManager().getModuleByClass(Speed.class);
    }

    public NumberValue<Float> getSpeed() {
        return (NumberValue<Float>) getSpeedMod().getValue("speed");
    }

    public String getName() {
        return name;
    }
}
