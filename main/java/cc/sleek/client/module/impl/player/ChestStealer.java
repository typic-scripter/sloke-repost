package cc.sleek.client.module.impl.player;

import cc.sleek.client.event.impl.UpdateEvent;
import cc.sleek.client.module.Module;
import cc.sleek.client.module.api.Category;
import cc.sleek.client.module.api.ModuleInfo;
import cc.sleek.client.property.impl.BooleanValue;
import cc.sleek.client.property.impl.NumberValue;
import cc.sleek.client.util.Stopwatch;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.item.ItemStack;

@ModuleInfo(name = "ChestStealer", description = "Steals items from chests", category = Category.PLAYER)
public class ChestStealer extends Module {

    private final NumberValue<Integer> delay = new NumberValue<Integer>("Delay", 50, 0, 1000, 5);
    private final BooleanValue closeEmpty = new BooleanValue("Close if empty", true);
    private final BooleanValue checkChest = new BooleanValue("Check if chest", true);
    private final BooleanValue tools = new BooleanValue("Tools", true);

    private final Stopwatch stopwatch = new Stopwatch();

    @EventLink
    Listener<UpdateEvent> updateEventListener = event -> {
        if (!(mc.currentScreen instanceof GuiChest))
            return;

        GuiChest chest = (GuiChest) mc.currentScreen;
        int windowId = chest.inventorySlots.windowId;

        if (checkChest.getValue() && !(chest.lowerChestInventory.getDisplayName().getUnformattedText().contains("Chest"))) {
            return;
        }

        if (closeEmpty.getValue()) {
            int items = 0;

            for (int i = 0; chest.lowerChestInventory.getSizeInventory() > i; i++) {
                if (chest.lowerChestInventory.getStackInSlot(i) != null) {
                    items++;
                }

                if (items == 0) {
                    mc.thePlayer.closeScreen();
                    return;
                }
            }
        }

        for (int i = 0; chest.lowerChestInventory.getSizeInventory() > i; i++) {
            if (chest.lowerChestInventory.getStackInSlot(i) != null) {
                ItemStack stack = chest.lowerChestInventory.getStackInSlot(i);
                if (isValid(stack)) {
                    if (stopwatch.timeElapsed(delay.getValue())) {
                        stopwatch.resetTime();
                        mc.playerController.windowClick(windowId, i, 0, 1, mc.thePlayer);
                    } else {
                        break;
                    }
                }
            }

        }
    };

    private boolean isValid(ItemStack stack) {
//        Item item = stack.getItem();
//        if (item instanceof ItemTool && tools.getValue()) {
//            return true;
//        }
//        if ((item == Items.wooden_sword || item == Items.stone_sword || item == Items.golden_sword || item == Items.iron_sword || item == Items.diamond_sword) || item instanceof ItemBlock || item instanceof ItemArmor) {
//            return true;
//        }
//        return false;
        return true;
    }

}
