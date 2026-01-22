package sunsetsatellite.catalyst.core.util.mp.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.screen.ScreenHandler;

@Environment(EnvType.CLIENT)
public class MpGuiEntryClient extends MpGuiEntry {

	public Class<?> guiClass;

	public MpGuiEntryClient(Class<?> inventoryClass, Class<? extends Screen> guiClass, Class<? extends ScreenHandler> containerClass) {
		super(inventoryClass, containerClass);
		this.guiClass = guiClass;
	}
}
