package sunsetsatellite.catalyst.core.util.mp.gui;


import net.minecraft.screen.ScreenHandler;

public class MpGuiEntry {

	public Class<?> inventoryClass;
	public Class<? extends ScreenHandler> containerClass;

	public MpGuiEntry(Class<?> inventoryClass, Class<? extends ScreenHandler> containerClass) {
		this.inventoryClass = inventoryClass;
		this.containerClass = containerClass;
	}
}
