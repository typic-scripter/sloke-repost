package cc.sleek.client.module.impl.render;

import cc.sleek.client.Sleek;
import cc.sleek.client.event.impl.Render2DEvent;
import cc.sleek.client.module.Module;
import cc.sleek.client.module.api.Category;
import cc.sleek.client.module.api.ModuleInfo;
import cc.sleek.client.property.impl.BooleanValue;
import cc.sleek.client.property.impl.ColorValue;
import cc.sleek.client.property.impl.EnumValue;
import cc.sleek.client.property.impl.NumberValue;
import cc.sleek.client.util.PlayerUtil;
import cc.sleek.client.util.RenderUtils;
import cc.sleek.client.util.fonts.Fonts;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.client.gui.Gui;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.concurrent.CopyOnWriteArrayList;

@ModuleInfo(name = "HUD", category = Category.RENDER, description = "Heads-up Display")
public class HUD extends Module {

    private final EnumValue<WatermarkMode> watermark = new EnumValue<>("Watermark", WatermarkMode.values());
    private final EnumValue<LinePositionMode> lineMode = new EnumValue<>("Line Position", LinePositionMode.values());
    private final EnumValue<TargetHUDMode> targetHud = new EnumValue<>("TargetHUD", TargetHUDMode.values());

    private final BooleanValue bpsCounter = new BooleanValue("BPS Counter", true);
    private final BooleanValue font = new BooleanValue("Font", true);

    private final ColorValue color = new ColorValue("Color", new Color(255, 255, 255).getRGB());
    private final NumberValue<Integer> ypos = new NumberValue<>("Y-Position", 3, 0, 100, 1);


    @EventLink
    private final Listener<Render2DEvent> render2DEventListener = event -> {
        int y = ypos.getValue();
        Color color = new Color(this.color.getValue());

        if (bpsCounter.getValue()) {
            String bps = String.format("BPS: %s", new DecimalFormat("#.##").format(PlayerUtil.getBPS()));
            drawString(bps, event.getSr().getScaledWidth() - getWidth(bps), event.getSr().getScaledHeight() - 10, color.getRGB());
        }

        CopyOnWriteArrayList<Module> mods = new CopyOnWriteArrayList<>();

        switch (watermark.getValue()) {
            case TEXT: {
                drawString("Sleek", 4, 4, color.getRGB());
                break;
            }

            case ONETAP: {
                RenderUtils.drawRect(5, 5, 150, 17, new Color(35, 35, 35, 255).getRGB());
                RenderUtils.drawRect(5, 5, 150, 2, color.getRGB());

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();

                mc.fontRendererObj.drawOutlinedString("sleek.cc | 1.0-DEV | " + dtf.format(now),  8, 10, -1, 1);
                break;
            }

            case TRANSPARENT: {
                RenderUtils.drawRect(5, 5, 150, 17, new Color(0, 0, 0, 85).getRGB());
                RenderUtils.drawRect(5, 5, 150, 2, color.getRGB());

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();

                mc.fontRendererObj.drawOutlinedString("sleek.cc | 1.0-DEV | " + dtf.format(now),  8, 10, -1, 1);
                break;
            }
            case OLD_PULSIVE:
                Fonts.asdsadadsadadasda.drawStringWithShadow("§lS§fleek", 4, 4, color.getRGB());
                break;

        }

        Sleek.INSTANCE.getModuleManager().getModules().forEach((m) -> {
            if (m.isToggled() && !m.isHidden()) mods.add(m);
        });

        mods.sort(Comparator.comparingInt((mod) -> {
            Module m = (Module) mod;
            String name = m.getName();
            String suffix = "";
            if (m.getSuffix() != null) {
                suffix =m.getSuffix();
            }
            String formatted = String.format("%s§7%s", name, !suffix.isEmpty() ? String.format(" %s", suffix) : "");
            return getWidth(formatted);
        }).reversed());

        for (Module mod : mods) {
            String name = mod.getName();
            String suffix = mod.getSuffix() != null ?mod.getSuffix() : "";

            String formatted = String.format("%s§7%s", name, !suffix.isEmpty() ? String.format(" %s", suffix) : "");
            float width = getWidth(formatted);
            float xPos = event.getSr().getScaledWidth() - width - 6;

            switch (lineMode.getValue()) {
                case RIGHT: {
                    RenderUtils.drawRect(event.getSr().getScaledWidth() - 1, y, 1, mc.fontRendererObj.FONT_HEIGHT + 2, color.getRGB());
                    break;
                }
                case LEFT: {
                    RenderUtils.drawRect(xPos, y, xPos - 1, y + 11, color.getRGB());
                    break;
                }
                case TOP: {
                    //check if its the first iteration
                    if (y == ypos.getValue()) {
                        Gui.drawRect(xPos, y, event.getSr().getScaledWidth() - 1, y - 1, color.getRGB());
                    }
                    break;
                }
            }
            //background
            Gui.drawRect(xPos, y, event.getSr().getScaledWidth() - 1, y + 11, 0x72000000);


            RenderUtils.drawRect(event.getSr().getScaledWidth() - 1, y, 1, mc.fontRendererObj.FONT_HEIGHT + 2, color.getRGB());
            Gui.drawRect(xPos, y, event.getSr().getScaledWidth() - 1, y + 11, 0x90000000);
            drawString(formatted, xPos + 2, (float) (y + 1.5), color.getRGB());
            y += 11;
        }
    };

    private float drawString(String str, float x, float y, int color) {
        if (font.getValue()) {
            return Fonts.productSans20.drawString(str, x, y, color);
        } else {
            return mc.fontRendererObj.drawString(str, (int) x, (int) y, color);
        }
    }
    private int getWidth(String str) {
        if (font.getValue()) {
            return Fonts.productSans20.getStringWidth(str);
        } else {
            return mc.fontRendererObj.getStringWidth(str);
        }
    }

    public EnumValue<WatermarkMode> getWatermark() {
        return watermark;
    }

    public EnumValue<LinePositionMode> getLineMode() {
        return lineMode;
    }

    public EnumValue<TargetHUDMode> getTargetHud() {
        return targetHud;
    }

    public enum WatermarkMode {
        TEXT, ONETAP, TRANSPARENT, NONE, OLD_PULSIVE
    }

    public enum LinePositionMode {
        TOP, RIGHT, LEFT, NONE
    }

    public enum TargetHUDMode {
        WEIRD, BETTER
    }
}
