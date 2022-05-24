package cc.sleek.client.event.impl;

import cc.sleek.client.event.Event;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class Render2DEvent extends Event {

    private final ScaledResolution sr;

    public Render2DEvent() {
        this.sr = new ScaledResolution(Minecraft.getMinecraft());
    }

    public ScaledResolution getSr() {
        return sr;
    }

}
