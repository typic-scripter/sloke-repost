package cc.sleek.client.module.impl.movement;

import cc.sleek.client.module.Module;
import cc.sleek.client.module.api.Category;
import cc.sleek.client.module.api.ModuleInfo;
import cc.sleek.client.property.impl.BooleanValue;

@ModuleInfo(
        name = "NoSlow",
        description = "No slowdown when using items",
        category = Category.MOVEMENT
)
public class NoSlow extends Module {
    public final BooleanValue sprint = new BooleanValue("Sprint", true);

    public void NoSlow(){
        register(sprint);
    }

    @Override
    public void onEnable() {

    }
}
