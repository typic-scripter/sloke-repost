package cc.sleek.client.gui.cgui.htb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cc.sleek.client.gui.cgui.htb.frame.Frame;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class GuiRijinClick extends GuiScreen {

	private final List<Frame> frames = new ArrayList<>();

	public void init() {
		GL11.glColor4f(1, 1, 1, 1);
		System.out.println("done");
		frames.add(new Frame("sleek", 2, 2, 250, 250) {
			@Override
			public void init() {
				System.out.println("hi");
			}
		});
		frames.forEach(Frame::init);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		frames.forEach(frame -> frame.drawScreen(mouseX, mouseY, partialTicks));
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		frames.forEach(frame -> frame.mouseClicked(mouseX, mouseY, mouseButton));
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);
		frames.forEach(frame -> frame.mouseReleased(mouseX, mouseY, state));
	}

	@Override
	public void keyTyped(char typedChar, int key) {
		frames.forEach(frame -> frame.keyTyped(typedChar, key));
		if (key == Keyboard.KEY_ESCAPE) {
			Minecraft.getMinecraft().thePlayer.closeScreen();
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
}
