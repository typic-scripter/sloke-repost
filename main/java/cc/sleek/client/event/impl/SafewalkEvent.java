package cc.sleek.client.event.impl;

import cc.sleek.client.event.Event;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class SafewalkEvent extends Event {

    private boolean safewalk;

    public SafewalkEvent() {
        this.safewalk = false;
    }

    public boolean isSafewalk() {
        return safewalk;
    }

    public void setSafewalk(boolean safewalk) {
        this.safewalk = safewalk;
    }
}
