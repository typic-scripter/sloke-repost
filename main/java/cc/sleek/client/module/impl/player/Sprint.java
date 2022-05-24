package cc.sleek.client.module.impl.player;

import cc.sleek.client.Sleek;
import cc.sleek.client.event.impl.UpdateEvent;
import cc.sleek.client.module.Module;
import cc.sleek.client.module.api.Category;
import cc.sleek.client.module.api.ModuleInfo;
import cc.sleek.client.module.impl.movement.NoSlow;
import cc.sleek.client.property.impl.BooleanValue;
import cc.sleek.client.util.PlayerUtil;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.Priorities;
import io.github.nevalackin.homoBus.annotations.EventLink;

/**
 * @author Kansio
 */
@ModuleInfo(name = "Sprint", description = "Makes you sprint", category = Category.PLAYER)
public class Sprint extends Module {

    public Sprint(){
        register(omniSprint);
    }

    private final BooleanValue omniSprint = new BooleanValue("Omni Sprint", false);

    //disable sprint on disable
    @Override
    public void onDisable() {
        super.onDisable();
        mc.thePlayer.setSprinting(false);
    }

    @EventLink(Priorities.LOW)
    private final Listener<UpdateEvent> updateEventListener = event -> {
        Scaffold scaffold = Sleek.INSTANCE.getModuleManager().getModuleByClass(Scaffold.class);
        NoSlow noslow = Sleek.INSTANCE.getModuleManager().getModuleByClass(NoSlow.class);
        if (!scaffold.isToggled() || scaffold.getSprint().getValue()) {
            if (PlayerUtil.isMoving()) {
                if (!noslow.sprint.getValue()) {
                    if (omniSprint.getValue()) {
                        mc.thePlayer.setSprinting(!mc.thePlayer.isCollidedHorizontally && !mc.thePlayer.isSneaking() && mc.thePlayer.getFoodStats().getFoodLevel() > 5 && !mc.thePlayer.isEating() && !mc.thePlayer.isUsingItem());
                    } else {
                        mc.thePlayer.setSprinting(!mc.thePlayer.isCollidedHorizontally && !mc.thePlayer.isSneaking() && mc.thePlayer.getFoodStats().getFoodLevel() > 5 && mc.gameSettings.keyBindForward.isKeyDown() && !mc.thePlayer.isEating() && !mc.thePlayer.isUsingItem());
                    }
                } else {
                    if (omniSprint.getValue()) {
                        mc.thePlayer.setSprinting(!mc.thePlayer.isCollidedHorizontally && !mc.thePlayer.isSneaking() && mc.thePlayer.getFoodStats().getFoodLevel() > 5);
                    } else {
                        mc.thePlayer.setSprinting(!mc.thePlayer.isCollidedHorizontally && !mc.thePlayer.isSneaking() && mc.thePlayer.getFoodStats().getFoodLevel() > 5 && mc.gameSettings.keyBindForward.isKeyDown());
                    }
                }
            }
        }
    };
}
