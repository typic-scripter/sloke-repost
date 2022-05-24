package cc.sleek.client.elements;

import java.util.Collection;
import java.util.Set;
import cc.sleek.client.Sleek;
import cc.sleek.client.event.impl.Render2DEvent;
import com.google.common.collect.Sets;
import cc.sleek.client.elements.util.ScreenPosition;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.client.Minecraft;

public final class HudPropertyApi {

	public static HudPropertyApi newInstance(){
		HudPropertyApi api = new HudPropertyApi();
		Sleek.INSTANCE.getEventBus().subscribe(api);
		return api;
	}

	private final Set<IRenderer> registeredRenderers = Sets.newHashSet();
	private final Minecraft mc = Minecraft.getMinecraft();

	private boolean renderOutlines = true;

	private HudPropertyApi(){}

	public void register(IRenderer... renderers){
		for(IRenderer renderer : renderers){
			this.registeredRenderers.add(renderer);
		}
	}

	public void unregister(IRenderer... renderers){
		for(IRenderer renderer : renderers){
			this.registeredRenderers.remove(renderer);
		}
	}

	public Collection<IRenderer> getHandlers(){
		return Sets.newHashSet(registeredRenderers);
	}

	public boolean getRenderOutlines(){
		return renderOutlines;
	}

	public void setRenderOutlines(boolean renderOutlines){
		this.renderOutlines = renderOutlines;
	}

	public void openConfigScreen(){
		mc.displayGuiScreen(new PropertyScreen(this));
	}

	@EventLink
	private final Listener<Render2DEvent> render2DEventListener = event -> {
		if(!(mc.currentScreen instanceof PropertyScreen)){
			registeredRenderers.forEach(this::callRenderer);
		}
	};

	private void callRenderer(IRenderer renderer){
		if(!renderer.isEnabled()){
			return;
		}
		
		ScreenPosition position = renderer.load();

		if(position == null){
			position = ScreenPosition.fromRelativePosition(0.5, 0.5);
		}

		renderer.render(position);
	}

}
