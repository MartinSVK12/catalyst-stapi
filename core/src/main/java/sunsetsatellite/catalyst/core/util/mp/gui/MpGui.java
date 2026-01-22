package sunsetsatellite.catalyst.core.util.mp.gui;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;

public interface MpGui {
    void catalyst$displayCustomGUI(Inventory inventory, int slotIndex, boolean isArmor, String id);

    void catalyst$displayCustomGUI(BlockEntity tileEntity, String id);

    void catalyst$displayCustomGUI(BlockEntity tileEntity, String id, NbtCompound data);
}
