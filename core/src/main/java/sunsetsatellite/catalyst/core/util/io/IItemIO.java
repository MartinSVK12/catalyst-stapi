package sunsetsatellite.catalyst.core.util.io;


import net.minecraft.item.ItemStack;
import sunsetsatellite.catalyst.core.util.Connection;
import sunsetsatellite.catalyst.core.util.Direction;

public interface IItemIO {

    int getActiveItemSlotForSide(Direction dir);

	int getActiveItemSlotForSide(Direction dir, ItemStack stack);

	void setActiveItemSlotForSide(Direction dir, int slot);

    Connection getItemIOForSide(Direction dir);

	void setItemIOForSide(Direction dir, Connection con);

	void cycleItemIOForSide(Direction dir);

	void cycleActiveItemSlotForSide(Direction dir, boolean backwards);
}
