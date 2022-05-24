package cc.sleek.client.module.impl.movement;

import cc.sleek.client.module.Module;
import cc.sleek.client.module.api.Category;
import cc.sleek.client.module.api.ModuleInfo;
import cc.sleek.client.property.impl.NumberValue;

/**
 * @author Kansio
 */
@ModuleInfo(name = "Timer", description = "Timer", category = Category.MOVEMENT)
public class Timer extends Module {

    private final NumberValue<Double> timer = new NumberValue<Double>("Timer", 1.0, 0.1, 20.0, 0.1);

    @Override
    public void onEnable() {
        mc.timer.timerSpeed = timer.getValue().floatValue();
    }



    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1f;
    }
}
