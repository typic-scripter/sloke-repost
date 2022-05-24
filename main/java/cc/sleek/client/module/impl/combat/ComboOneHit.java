package cc.sleek.client.module.impl.combat;

import cc.sleek.client.event.impl.PacketEvent;
import cc.sleek.client.module.Module;
import cc.sleek.client.module.api.Category;
import cc.sleek.client.module.api.ModuleInfo;
import cc.sleek.client.util.IPacketUtil;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.network.play.client.C02PacketUseEntity;

@ModuleInfo(
        name = "ComboOneHit",
        description = "The NoRules funny",
        category = Category.COMBAT
)
public class ComboOneHit extends Module {

    @EventLink
    Listener<PacketEvent> packetEventListener = packetEvent -> {
       if (packetEvent.getPacket() instanceof C02PacketUseEntity) {
           IPacketUtil.sendPacketNoEvent(packetEvent.getPacket(), 100);
       }
    };

}
