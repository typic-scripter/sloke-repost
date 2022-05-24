package cc.sleek.client.gui.buttons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

/**
 * dont question the name alright
 */
public abstract class ExtendableButton extends GuiButton {

    protected Minecraft mc = Minecraft.getMinecraft();
    public ExtendableButton(int buttonId, int x, int y, String buttonText) {
        super(buttonId, x, y, buttonText);
    }

    public ExtendableButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
    }

    public abstract void draw(int mouseX, int mouseY);

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        draw(mouseX, mouseY);
    }
}
