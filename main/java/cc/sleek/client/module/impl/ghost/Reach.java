package cc.sleek.client.module.impl.ghost;

import cc.sleek.client.module.Module;
import cc.sleek.client.module.api.Category;
import cc.sleek.client.module.api.ModuleInfo;
import cc.sleek.client.property.impl.NumberValue;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lombok.Getter;

/**
 * @see net.minecraft.client.renderer.EntityRenderer#getMouseOver
 */
@ModuleInfo(name = "Reach", description = "Long arm hake", category = Category.GHOST)
public class Reach extends Module {

    @Getter private final NumberValue<Double> reach = new NumberValue<>("Reach", 3.0, 3.0, 6.0, 0.1);

}
