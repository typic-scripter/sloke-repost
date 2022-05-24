package cc.sleek.client.module.impl.player;

import cc.sleek.client.event.impl.CollideEvent;
import cc.sleek.client.event.impl.UpdateEvent;
import cc.sleek.client.module.Module;
import cc.sleek.client.module.api.Category;
import cc.sleek.client.module.api.ModuleInfo;
import cc.sleek.client.property.impl.EnumValue;
import cc.sleek.client.util.PlayerUtil;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;

@ModuleInfo(
        name = "Phase",
        description = "Toggles phase",
        category = Category.PLAYER
)
public class Phase extends Module {

    private final EnumValue<Mode> mode = new EnumValue<>("Mode", Mode.values());

    public Phase() {
        register(mode);
    }

    @EventLink
    Listener<UpdateEvent> update = event -> {
        switch (mode.getValue()) {
//            case VIPER:
            case VANILLA:
                if (mc.thePlayer.isCollidedHorizontally) {
                    double x = mc.thePlayer.posX;
                    double y = mc.thePlayer.posY;
                    double z = mc.thePlayer.posZ;
                    double xAdd = -Math.sin(PlayerUtil.getDirection()) * 0.1;
                    double zAdd = Math.cos(PlayerUtil.getDirection()) * 0.1;
                    mc.thePlayer.setPosition(x + xAdd, y, z + zAdd);
                }
                break;
            case SKIP:
                break;

        }
    };

    public enum Mode {
        VANILLA, SKIP, /*VIPER,*/
    }

}
