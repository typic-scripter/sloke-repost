package cc.sleek.client.module.impl.combat;

import cc.sleek.client.event.impl.UpdateEvent;
import cc.sleek.client.module.Module;
import cc.sleek.client.module.api.Category;
import cc.sleek.client.module.api.ModuleInfo;
import cc.sleek.client.util.MathUtil;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@ModuleInfo(name = "AntiBot", category = Category.COMBAT, description = "")
public class AntiBot extends Module {

    private final CopyOnWriteArrayList<Entity> supposedBots = new CopyOnWriteArrayList<>();

    @EventLink
    Listener<UpdateEvent> listener = event -> {
        List<EntityPlayer> players = mc.theWorld.playerEntities;
        players.forEach(entity -> {
            if (entity.ticksExisted < 100) {
                supposedBots.add(entity);
            }
        });
        for (Entity supposedBot : supposedBots) {
            supposedBot.setInvisible(true);
            if (supposedBot.ticksExisted > MathUtil.toTicks(10)) {
                supposedBot.setInvisible(false);
                supposedBots.remove(supposedBot);
            }
        }
    };

}
