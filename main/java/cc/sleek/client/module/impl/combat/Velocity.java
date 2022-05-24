package cc.sleek.client.module.impl.combat;

import cc.sleek.client.event.impl.PacketEvent;
import cc.sleek.client.module.Module;
import cc.sleek.client.module.api.Category;
import cc.sleek.client.module.api.ModuleInfo;
import cc.sleek.client.property.impl.BooleanValue;
import cc.sleek.client.property.impl.NumberValue;
import cc.sleek.client.util.ChatUtil;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

/**
 * @author Kansio
 */
@ModuleInfo(name = "Velocity", description = "Prevents you from taking knockback", category = Category.COMBAT)
public class Velocity extends Module {

    private final BooleanValue onlyInCombat = new BooleanValue("Only Combat (*)", true); //experimental: checks if you've been damaged
    private final BooleanValue showSuspiciousVelocity = new BooleanValue("Only Combat Debug", true); //prints in chat if it detects a velocity packet without damage

    private final BooleanValue knockbackDebug = new BooleanValue("Knockback Debug", false); //why? idk

    private final BooleanValue cancelAllVelocity = new BooleanValue("Cancel all velocity", false); //why? because the sliders sometimes fuck themselves and don't let you get the value you want
    private final BooleanValue cancelExplosion = new BooleanValue("Cancel explosion packets", true); //todo: make it so if this is enabled it no show sliders yes yes

    private final NumberValue<Integer> horizontalVelocity = new NumberValue<>("Horizontal", 0, -200, 200, 1);
    private final NumberValue<Integer> verticalVelocity = new NumberValue<>("Vertical", 0, 0, 100, 1);
    @EventLink
    private final Listener<PacketEvent> eventListener = event -> {
        if (event.getPacket() instanceof S12PacketEntityVelocity) {

            if (mc.thePlayer == null)
                return;
            if (((S12PacketEntityVelocity) event.getPacket()).getEntityID() != mc.thePlayer.getEntityId())
                return;

            S12PacketEntityVelocity s12 = event.getPacket();
            if (s12.getEntityID() == mc.thePlayer.getEntityId()) {


                if (knockbackDebug.getValue()) {
                    ChatUtil.logNoPrefix("§cKnockback received on tick " + mc.thePlayer.ticksExisted);
                }

                if (cancelAllVelocity.getValue()) {
                    event.setCancelled(true);
                    return;
                }

                // prob a lot better way to do this
                // yea fuck that im changing it rn
                // i changed spectre code bc it was kinda dumb
                if (verticalVelocity.getValue() == 0 && horizontalVelocity.getValue() == 0) {
                    event.setCancelled(true);
                } else {
                    if (horizontalVelocity.getValue() == 0) {
                        s12.setMotionX(0);
                        s12.setMotionZ(0);
                    } else {

                        s12.setMotionX(s12.getMotionX() * (100 / horizontalVelocity.getValue()));
                        s12.setMotionZ(s12.getMotionZ() * (100 / horizontalVelocity.getValue()));
                    }
                    if (verticalVelocity.getValue() == 0) {
                        s12.setMotionY(0);
                    } else {
                        s12.setMotionY(s12.getMotionY() * (100 / verticalVelocity.getValue()));
                    }

                }
            }
        }

        //cancel the explosion packet, as some servers use it to cause knockback (hypixel....)
        else if (event.getPacket() instanceof S27PacketExplosion && cancelExplosion.getValue()) {
//            if (onlyInCombat.getValue() && mc.thePlayer.hurtTime <= 0) {
//
//                if (showSuspiciousVelocity.getValue())
//                    ChatUtil.log("Received a explosion packet, but you haven't been damaged recently...");
//
//
//                return;
//            }

            if (knockbackDebug.getValue()) {
                ChatUtil.logNoPrefix("§cExplosion received on tick " + mc.thePlayer.ticksExisted);
            }

            event.setCancelled(true);

        }
    };

    public Velocity() {
        register(onlyInCombat, showSuspiciousVelocity, knockbackDebug, cancelAllVelocity, cancelExplosion, horizontalVelocity, verticalVelocity);
    }


}
