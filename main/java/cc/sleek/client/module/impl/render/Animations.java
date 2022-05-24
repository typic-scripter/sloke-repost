package cc.sleek.client.module.impl.render;


import cc.sleek.client.module.Module;
import cc.sleek.client.module.api.ModuleInfo;
import cc.sleek.client.module.api.Category;
import cc.sleek.client.util.ChatUtil;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import cc.sleek.client.event.impl.RenderItemEvent;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import org.lwjgl.opengl.GL11;

@ModuleInfo(
        name = "Animations",
        description = "Block hit animation",
        category = Category.RENDER

)
public class Animations extends Module {

    @EventLink
    Listener<RenderItemEvent> listener = event -> {
        event.setOverriding(true);
//        ChatUtil.log("rendering");
        ItemRenderer itemRenderer = mc.getItemRenderer();
        itemRenderer.transformFirstPersonItem(event.getUseProgress(), event.getSwingProgress());
        itemRenderer.func_178103_d();
        GL11.glTranslated(-0.25D, 0.2D, 0.0D);
        GL11.glTranslatef(-0.05F, mc.thePlayer.isSneaking() ? -0.2F : 0.0F, 0.1F);
    };

}
