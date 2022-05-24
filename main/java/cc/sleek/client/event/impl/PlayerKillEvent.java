package cc.sleek.client.event.impl;

import cc.sleek.client.event.Event;
import lombok.Getter;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @author Kansio
 */
@Getter
public class PlayerKillEvent extends Event {

    private String playerName;
    private EntityPlayer entityPlayer;

    public PlayerKillEvent(EntityPlayer entityPlayer) {
        if (entityPlayer == null) return;
        this.entityPlayer = entityPlayer;
        this.playerName = entityPlayer.getName();
    }

}
