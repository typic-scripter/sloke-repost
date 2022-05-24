package cc.sleek.client.module.impl.combat;

import cc.sleek.client.event.impl.UpdateEvent;
import cc.sleek.client.module.Module;
import cc.sleek.client.module.api.Category;
import cc.sleek.client.module.api.ModuleInfo;
import cc.sleek.client.property.impl.BooleanValue;
import cc.sleek.client.property.impl.NumberValue;
import cc.sleek.client.util.ChatUtil;
import cc.sleek.client.util.IPacketUtil;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;

@ModuleInfo(
        name = "AutoHeal",
        description = "Automatically splashes potions",
        category = Category.COMBAT
)
public class AutoHeal extends Module {

    private final NumberValue<Integer> health = new NumberValue<Integer>("Health", 15, 1, 20, 1);
    private final BooleanValue speed = new BooleanValue("Speed Potions", false);
    private final BooleanValue otherPots = new BooleanValue("Healing Potions", false);
    private final BooleanValue apples = new BooleanValue("Golden Apples", false);
    private final BooleanValue soup = new BooleanValue("Soup", false);

    @EventLink
    Listener<UpdateEvent> updateEventListener = updateEvent -> {
        if (mc.thePlayer.getHealth() <= health.getValue()) {
            int oldSlot = mc.thePlayer.inventory.currentItem;
            if (apples.getValue()) {
                for (short i = 0; i < 9; i++) { // short bc yes
                    if (mc.thePlayer.inventory.getStackInSlot(i) != null) {
                        ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
                        if (stack.getItem().getUnlocalizedName().equals(Items.golden_apple.getUnlocalizedName()) || stack.getItem() == Items.skull || stack.getItem() == Items.potato) {
                            mc.thePlayer.inventory.currentItem = i;
                            mc.playerController.updateController();
                            mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem());
                            mc.playerController.onStoppedUsingItem(mc.thePlayer);
//                                mc.thePlayer.inventory.setCurrentItem();
                            break;
                        }
                    }
                }
            }
            if (soup.getValue()) {
                for (short i = 0; i < 9; i++) { // short bc yes
                    if (mc.thePlayer.inventory.getStackInSlot(i) != null) {
                        ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
                        if (stack.getItem().getUnlocalizedName().equals(Items.mushroom_stew.getUnlocalizedName())) {
                            mc.thePlayer.inventory.currentItem = i;
                            mc.playerController.updateController();
                            mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem());
                            mc.playerController.onStoppedUsingItem(mc.thePlayer);
//                                mc.thePlayer.inventory.setCurrentItem();
                            break;
                        }
                    }
                }
            }
            mc.thePlayer.inventory.currentItem = oldSlot;
            mc.playerController.updateController();
        }
    };

    private void swap(int slot) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 1, 2, mc.thePlayer);
    }

}
