package cc.sleek.client.elements.impl.watermark;

import cc.sleek.client.elements.IRenderer;
import cc.sleek.client.elements.util.ScreenPosition;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class WatermarkRenderer implements IRenderer{

	private final String text = "Sleek";

	private final FontRenderer fontRenderer;
	
	public WatermarkRenderer() {
		fontRenderer = Minecraft.getMinecraft().fontRendererObj;
	}
	
	@Override
	public void save(ScreenPosition position) {
		WatermarkConfig.X_1 = position.getRelativeX();
		WatermarkConfig.Y_1 = position.getRelativeY();
	}

	@Override
	public ScreenPosition load() {
		return ScreenPosition.fromRelativePosition(WatermarkConfig.X_1, WatermarkConfig.Y_1);
	}

	@Override
	public void render(ScreenPosition position) {
		Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(text, position.getAbsoluteX(), position.getAbsoluteY(), 0xFFFFFF);
	}

	@Override
	public int getHeight() {
		return Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT;
	}

	@Override
	public int getWidth() {
		return Minecraft.getMinecraft().fontRendererObj.getStringWidth(text);
	}

	@Override
	public void renderDummy(ScreenPosition position) {
		this.render(position);
	}

}
