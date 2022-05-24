package cc.sleek.client.module.impl.render;

import cc.sleek.client.event.impl.PlayerKillEvent;
import cc.sleek.client.module.Module;
import cc.sleek.client.module.api.Category;
import cc.sleek.client.module.api.ModuleInfo;
import cc.sleek.client.property.impl.BooleanValue;
import cc.sleek.client.property.impl.EnumValue;
import cc.sleek.client.util.ChatUtil;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.util.EnumParticleTypes;

/**
 * @author Kansio
 */
@ModuleInfo(name = "Kill Effect", description = "Plays an effect when you kill a player", category = Category.RENDER, visibility = false)
public class KillEffect extends Module {

    private final BooleanValue particles = new BooleanValue("Particles", true);
    private final EnumValue<ParticleMode> particleMode = new EnumValue<>("Particle Mode", ParticleMode.values(), () -> particles.getValue());
    private final EnumValue<EnumParticleTypes> effectMode = new EnumValue<>("Effect Mode", EnumParticleTypes.values(), () -> particles.getValue());


    @EventLink
    private final Listener<PlayerKillEvent> playerKillEventListener = event -> {
        if (event.getEntityPlayer() == null) {
            return;
        }

        if (particles.getValue()) {
            switch (particleMode.getValue()) {
                case VORTEX: {
                    try {
                        for (double i = 1.0; i < 10; i = i + 0.5) {
                            for (int d = 0; d < 360; d++) {
                                double radians = Math.toRadians(d);
                                double x = Math.cos(radians) * (i / 8);
                                double z = Math.sin(radians) * (i / 8);
                                mc.theWorld.spawnParticle(effectMode.getValue(), event.getEntityPlayer().posX + x, event.getEntityPlayer().posY - 2.5 + (i / 2), event.getEntityPlayer().posZ + z, 0, 0, 0);
                            }
                        }
                    } catch (Exception e) {
                        ChatUtil.logNoPrefix("§cThere was an error while attempting to spawn the kill effect! Please select another particle type.");
                        e.printStackTrace();
                    }
                    break;
                }
                case BLOOD: {
                    mc.theWorld.spawnParticle(effectMode.getValue(), event.getEntityPlayer().posX, event.getEntityPlayer().posY, event.getEntityPlayer().posZ, 0, 0, 0);
                    break;
                }

                case THUNDER: {
                    //spawn thunder
                    mc.theWorld.addWeatherEffect(new EntityLightningBolt(mc.theWorld, event.getEntityPlayer().posX, event.getEntityPlayer().posY, event.getEntityPlayer().posZ));
                    break;
                }
            }
        }
    };

    private enum ParticleMode {
        BLOOD,
        VORTEX,
        THUNDER,
    }
}
