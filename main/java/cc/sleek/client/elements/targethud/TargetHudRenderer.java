package cc.sleek.client.elements.targethud;

import cc.sleek.client.Sleek;
import cc.sleek.client.elements.IRenderer;
import cc.sleek.client.elements.util.ScreenPosition;
import cc.sleek.client.module.Module;
import cc.sleek.client.module.impl.combat.KillAura;
import cc.sleek.client.module.impl.render.HUD;
import cc.sleek.client.util.ChatUtil;
import cc.sleek.client.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TargetHudRenderer implements IRenderer{

	private final Color color1 = new Color(30, 30, 30);
	private final Color color2 = new Color(26, 26, 26);
	private final Color color3 = new Color(37, 37, 37);
	private final Color color4 = new Color(238, 70, 70);
	private final Color color5 = new Color(26, 26, 26);
	private final Color color6 = new Color(70, 137, 238);

	@Override
	public void save(ScreenPosition position) {
		TargetHudConfig.X_1 = position.getRelativeX();
		TargetHudConfig.Y_1 = position.getRelativeY();
	}

	@Override
	public ScreenPosition load() {
		return ScreenPosition.fromRelativePosition(TargetHudConfig.X_1, TargetHudConfig.Y_1);
	}

	@Override
	public void render(ScreenPosition position) {
		Minecraft mc = Minecraft.getMinecraft();
		EntityLivingBase target = KillAura.target;
		HUD hud = Sleek.INSTANCE.getModuleManager().getModuleByClass(HUD.class);

		if (target == null) {
			return;
		}

		switch (hud.getTargetHud().getValue()) {
			case WEIRD:

				RenderUtils.drawRect(position.getAbsoluteX(), position.getAbsoluteY(), getWidth(), getHeight(), color1.getRGB()); // isnt creating a new Color instance very horrible for memory
				RenderUtils.drawRect(position.getAbsoluteX() + 5, position.getAbsoluteY() + 50, getWidth() - 10, getHeight() - 60, color2.getRGB());

				RenderUtils.drawRect(position.getAbsoluteX() + 5, position.getAbsoluteY() + 5, 40, 40, color3.getRGB());

				if (target instanceof EntityPlayer) {
					ResourceLocation skin = ((AbstractClientPlayer)target).getLocationSkin();
					RenderUtils.drawFace(skin, position.getAbsoluteX() + 5, position.getAbsoluteY() + 5, 40, 40);
				}

				mc.fontRendererObj.drawStringWithShadow(target.getName(), position.getAbsoluteX() + 55, position.getAbsoluteY() + 10, Color.WHITE.getRGB());

				//width - 10 for health bar
				//width - 60 for armor bar
				double health = target.getHealth();

				if (health > 20) {
					health = 20;
				}

				int healthWidth = (int) Math.round(health * 8.25);

				RenderUtils.drawRect(position.getAbsoluteX() + 5, position.getAbsoluteY() + 50, healthWidth, getHeight() - 60, color4.getRGB());
				RenderUtils.drawRect(position.getAbsoluteX() + 55, position.getAbsoluteY() + 30, getWidth() - 60, getHeight() - 65, color5.getRGB());
				RenderUtils.drawRect(position.getAbsoluteX() + 55, position.getAbsoluteY() + 30, (int)Math.round(target.getTotalArmorValue() * 5.73), getHeight() - 65, color6.getRGB());
				break;
			case BETTER:
//				RenderUtils.drawRect(position.getAbsoluteX(), position.getAbsoluteY(), getWidth(), getHeight(), );
		}


}

	@Override
	public int getHeight() {
		return 75;
	}

	@Override
	public int getWidth() {
		return 175;
	}

	@Override
	public void renderDummy(ScreenPosition position) {
		Minecraft mc = Minecraft.getMinecraft();

		RenderUtils.drawRect(position.getAbsoluteX(), position.getAbsoluteY(), getWidth(), getHeight(), new Color(30, 30, 30, 255).getRGB());
		RenderUtils.drawRect(position.getAbsoluteX() + 5, position.getAbsoluteY() + 50, getWidth() - 10, getHeight() - 60, new Color(26, 26, 26, 255).getRGB());

		ResourceLocation skin = mc.thePlayer.getLocationSkin();
		RenderUtils.drawFace(skin, position.getAbsoluteX() + 5, position.getAbsoluteY() + 5, 40, 40);


		mc.fontRendererObj.drawStringWithShadow(mc.thePlayer.getName(), position.getAbsoluteX() + 55, position.getAbsoluteY() + 10, Color.WHITE.getRGB());

		RenderUtils.drawRect(position.getAbsoluteX() + 5, position.getAbsoluteY() + 50, (int)Math.round(mc.thePlayer.getHealth() * 8.25), getHeight() - 60, new Color(238, 70, 70, 255).getRGB());
		RenderUtils.drawRect(position.getAbsoluteX() + 55, position.getAbsoluteY() + 30, getWidth() - 60, getHeight() - 65, new Color(26, 26, 26, 255).getRGB());
		RenderUtils.drawRect(position.getAbsoluteX() + 55, position.getAbsoluteY() + 30, (int)Math.round(mc.thePlayer.getTotalArmorValue() * 5.73), getHeight() - 65, new Color(70, 137, 238, 255).getRGB());
	}

}
