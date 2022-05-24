package cc.sleek.client.module.impl.render;

import cc.sleek.client.gui.cgui.htb.GuiRijinClick;
import cc.sleek.client.gui.cgui.lime.LimeGUI;
import cc.sleek.client.module.Module;
import cc.sleek.client.module.api.Category;
import cc.sleek.client.module.api.ModuleInfo;
import cc.sleek.client.property.impl.EnumValue;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

@ModuleInfo(
        name = "ClickGUI",
        description = "Original this time",
        category = Category.RENDER,
        bind = Keyboard.KEY_RSHIFT
)
public class ClickGUI extends Module {

    private final EnumValue<Mode> mode = new EnumValue<>("Mode", Mode.values());
    private GuiScreen gui;

    @Override
    public void onEnable() {
        switch (mode.getValue()) {
            case HTB:
                if (gui == null || !(gui instanceof GuiRijinClick)) {
                    gui = new GuiRijinClick();
                    ((GuiRijinClick) gui).init();
                }
                break;
            case LIME:
                if (gui == null || !(gui instanceof LimeGUI)) {
                    gui = new LimeGUI();
                }
                break;
        }
        mc.displayGuiScreen(gui);
        toggle();
    }


    private enum Mode {
        HTB, LIME// to be continued
    }
}
