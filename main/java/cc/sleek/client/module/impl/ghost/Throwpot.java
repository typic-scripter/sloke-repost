package cc.sleek.client.module.impl.ghost;

import cc.sleek.client.module.Module;
import cc.sleek.client.module.api.Category;
import cc.sleek.client.module.api.ModuleInfo;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

@ModuleInfo(
        name = "Throwpot",
        category = Category.GHOST,
        description = "Throws potion"
)
public class Throwpot extends Module {

    @Override
    public void onEnable() {
        int oldSlot = mc.thePlayer.inventory.currentItem;
        for (int i = 0; i < 9; i++) {
            if (mc.thePlayer.inventory.getStackInSlot(i) != null) {
                ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
                if (stack.getItem() == Items.potionitem) {
                    mc.thePlayer.inventory.currentItem = i;
                    mc.playerController.updateController();
                    mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem());
                    mc.playerController.onStoppedUsingItem(mc.thePlayer);
                    break;
                }
            }
        }
        mc.thePlayer.inventory.currentItem = oldSlot;
        mc.playerController.updateController();
        toggle();
    }
}
