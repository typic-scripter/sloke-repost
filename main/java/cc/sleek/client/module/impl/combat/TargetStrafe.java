package cc.sleek.client.module.impl.combat;

import cc.sleek.client.Sleek;
import cc.sleek.client.event.impl.UpdateEvent;
import cc.sleek.client.module.Module;
import cc.sleek.client.module.api.Category;
import cc.sleek.client.module.api.ModuleInfo;
import cc.sleek.client.module.impl.movement.Flight;
import cc.sleek.client.property.impl.BooleanValue;
import cc.sleek.client.property.impl.NumberValue;
import cc.sleek.client.util.ChatUtil;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.block.BlockAir;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;

/**
 * @author Kansio
 */
@ModuleInfo(name = "Target Strafe", description = "Strafes around the target", category = Category.COMBAT)
public class TargetStrafe extends Module {

    private final BooleanValue control = new BooleanValue("Controllable", true);
    private final BooleanValue jump = new BooleanValue("Only Whilst Jumping", true);

    private final BooleanValue whileFlying = new BooleanValue("Always while flying", true);


    //cope about static abuse
    public double dir = -1;
    public NumberValue<Float> range = new NumberValue("Range", 2.5f, 0.1, 6, 0.1);
    // removed kansio's inconsistent static abuse

    public TargetStrafe() {
        register(range, control, jump);
    }

    //set dir to -1 on disable
    @Override
    public void onDisable() {
        dir = -1;
    }

    //invert dir value
    private void invertStrafe() {
        dir = -dir;
    }

    @EventLink
    private final Listener<UpdateEvent> updateEventListener = event -> {
        if (event.isPre()) {
            if (control.getValue()) {
                if (mc.gameSettings.keyBindLeft.isPressed()) {
                    dir = 1;
                } else if (mc.gameSettings.keyBindRight.isPressed()) {
                    dir = -1;
                }
            }

            if (mc.thePlayer.isCollidedHorizontally) {
                invertStrafe();
            }

            //void check
//            if (!Sleek.INSTANCE.getModuleManager().getModuleByName("Flight").isToggled()) {
//                if (!isBlockUnder(mc.thePlayer)) {
////                    mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
//                    invertStrafe();
//                }
//            }
        }
    };

    public boolean canStrafe() {
        KillAura aura = Sleek.INSTANCE.getModuleManager().getModuleByName("KillAura");
        Flight flight = Sleek.INSTANCE.getModuleManager().getModuleByClass(Flight.class);

        if (!aura.isToggled()) {
            return false;
        }

        if (!this.isToggled()) {
            return false;
        }

        if (whileFlying.getValue() && KillAura.target != null && flight.isToggled()) {
            return true;
        }

        //if jump value is enabled, jump down and target isnt null -> start strafing, else (if jump mode isn't enabled) check if killaura target isn't null.
        return jump.getValue() ? mc.gameSettings.keyBindJump.isKeyDown() && KillAura.target != null : KillAura.target != null;
    }

    private boolean isBlockUnder(Entity entity) {
        for (int i = (int) (entity.posY - 1.0); i > 0; --i) {
            BlockPos pos = new BlockPos(entity.posX,
                    i, entity.posZ);
            if (mc.theWorld.getBlockState(pos).getBlock() instanceof BlockAir)
                continue;
            return true;
        }
        return false;
    }

    public double getDir() {
        return dir;
    }

    public void setDir(double dir) {
        this.dir = dir;
    }

    public NumberValue<Float> getRange() {
        return range;
    }

    public void setRange(NumberValue<Float> range) {
        this.range = range;
    }
}
