package cc.sleek.client.module.impl.player;

import cc.sleek.client.event.impl.UpdateEvent;
import cc.sleek.client.module.Module;
import cc.sleek.client.module.api.Category;
import cc.sleek.client.module.api.ModuleInfo;
import cc.sleek.client.util.ChatUtil;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

@ModuleInfo(name = "InvManager", description = "Manages your inventory", category = Category.PLAYER)
public class InvManager extends Module {


    // make UpdateEvent listenner
    @EventLink
    Listener<UpdateEvent> updateEventListener = event -> {
        // auto armor
//        ChatUtil.log(mc.thePlayer.inventory.armorInventory[0], mc.thePlayer.inventory.armorInventory[1], mc.thePlayer.inventory.armorInventory[2], mc.thePlayer.inventory.armorInventory[3]);
        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventory.mainInventory[i] != null) {
                ItemStack stack = mc.thePlayer.inventory.mainInventory[i];
                if (stack.getItem() instanceof ItemArmor) {
                    ItemArmor armor = (ItemArmor) stack.getItem();
                    int slot = 0;
                    switch (armor.armorType) {
                        case 0: {
                            slot = 4;
                            break;
                        }
                        case 1: {
                            slot = 3;
                            break;
                        }
                        case 2: {
                            slot = 2;
                            break;
                        }
                        case 3: {
                            slot = 1;
                            break;
                        }
                    }
                    if (mc.thePlayer.inventory.armorInventory[slot - 1] == null) {

                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, -1, 0, 1, mc.thePlayer);
                    }
                }
            }

        }
    };

}
