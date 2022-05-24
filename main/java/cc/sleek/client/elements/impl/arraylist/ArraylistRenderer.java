package cc.sleek.client.elements.impl.arraylist;

import cc.sleek.client.Sleek;
import cc.sleek.client.elements.IRenderer;
import cc.sleek.client.elements.util.ScreenPosition;
import cc.sleek.client.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ArraylistRenderer implements IRenderer{

	private final FontRenderer fontRenderer;
	private List<Module> enabledModules = new ArrayList<Module>();
	
	public ArraylistRenderer() {
		fontRenderer = Minecraft.getMinecraft().fontRendererObj;
	}
	
	@Override
	public void save(ScreenPosition position) {
		ArraylistConfig.X_1 = position.getRelativeX();
		ArraylistConfig.Y_1 = position.getRelativeY();
	}

	@Override
	public ScreenPosition load() {
		return ScreenPosition.fromRelativePosition(ArraylistConfig.X_1, ArraylistConfig.Y_1);
	}

	@Override
	public void render(ScreenPosition position) {
		enabledModules = Sleek.INSTANCE.getModuleManager().getModules().stream().filter(Module::isToggled).collect(Collectors.toList());

		int y = 0;
		for (Module module : enabledModules) {
			Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(module.getName(), position.getAbsoluteX(), position.getAbsoluteY() + y, 0xFFFFFF);
			y += 11;
		}
	}

	@Override
	public int getHeight() {
		return enabledModules.size() * 11;
	}

	@Override
	public int getWidth() {
		return 80;
	}

	@Override
	public void renderDummy(ScreenPosition position) {
		this.render(position);
	}

}
