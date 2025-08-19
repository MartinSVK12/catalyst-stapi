package sunsetsatellite.catalyst.multipart.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;
import sunsetsatellite.catalyst.multipart.block.entity.CarpenterWorkbenchBlockEntity;

public class SlotPartPicker extends Slot {

	public int variableIndex = 0;
	public CarpenterWorkbenchBlockEntity tile;

	public SlotPartPicker(CarpenterWorkbenchBlockEntity inventory, int id, int x, int y) {
		super(inventory, id, x, y);
		this.variableIndex = id;
		this.tile = inventory;
	}

	@Override
	public void onTakeItem(ItemStack itemstack) {
		super.onTakeItem(itemstack);
		if(tile.contents[0] != null && tile.contents[1] != null && tile.contents[1].getItem() instanceof AxeItem) {
			tile.removeStack(0,1);
			tile.contents[1].damage(1,null);
		}
	}

    @Environment(EnvType.SERVER)
	@Override
	public boolean equals(Inventory container, int i) {
		return container == tile && i == variableIndex;
	}

	@Override
	public @Nullable ItemStack getStack() {
		if(variableIndex >= 0 && variableIndex < tile.parts.size()){
			return tile.parts.get(variableIndex);
		}
		return null;
	}

	@Override
	public boolean canInsert(ItemStack itemstack) {
		return false;
	}

	@Override
	public boolean hasStack() {
		return getStack() != null;
	}

	@Override
	public @Nullable ItemStack takeStack(int i) {
		if(variableIndex < tile.parts.size()){
			return tile.parts.get(variableIndex);
		}
		return null;
	}

	@Override
	public void setStack(@Nullable ItemStack itemstack) {

	}

	public int getSlotIndex() {
		return index;
	}

}
