package cc.sleek.client.module.impl.combat;

import cc.sleek.client.event.impl.PacketEvent;
import cc.sleek.client.module.api.Category;
import cc.sleek.client.module.api.ModuleInfo;
import cc.sleek.client.util.IPacketUtil;
import cc.sleek.client.util.PlayerUtil;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;

@ModuleInfo(name = "Criticals", description = "Gets critical hits", category = Category.COMBAT)
public class Criticals extends cc.sleek.client.module.Module {

    @EventLink
    Listener<PacketEvent> packetEventListener = event -> {
        if (event.getPacket() instanceof C02PacketUseEntity) {
            C02PacketUseEntity c02 = event.getPacket();
            if (c02.getAction().equals(C02PacketUseEntity.Action.ATTACK)) {
                doCrit();
            }
        }
    };

    public void doCrit() {
        if (mc.thePlayer.onGround) {
            IPacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0625, mc.thePlayer.posZ, false));
            IPacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
        }
    }
}
