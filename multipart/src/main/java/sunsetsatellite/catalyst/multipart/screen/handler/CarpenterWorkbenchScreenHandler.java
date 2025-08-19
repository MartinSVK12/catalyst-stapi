package sunsetsatellite.catalyst.multipart.screen.handler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import sunsetsatellite.catalyst.multipart.block.entity.CarpenterWorkbenchBlockEntity;
import sunsetsatellite.catalyst.multipart.util.SlotPartPicker;

public class CarpenterWorkbenchScreenHandler extends ScreenHandler {

    public final CarpenterWorkbenchBlockEntity tile;

    public CarpenterWorkbenchScreenHandler(Inventory playerInventory, CarpenterWorkbenchBlockEntity tile) {
        this.tile = tile;

        this.addSlot(new Slot(tile, 0, 34, 35));
        this.addSlot(new Slot(tile, 1, 65, 53));

        int j1;
        int l1;
        for(j1 = 0; j1 < 3; ++j1) {
            for(l1 = 0; l1 < 3; ++l1) {
                this.addSlot(new SlotPartPicker(tile, l1 + j1 * 3, 92 + l1 * 18, 17 + j1 * 18));
            }
        }

        for(j1 = 0; j1 < 3; ++j1) {
            for(l1 = 0; l1 < 9; ++l1) {
                this.addSlot(new Slot(playerInventory, l1 + j1 * 9 + 9, 8 + l1 * 18, 84 + j1 * 18));
            }
        }

        for(j1 = 0; j1 < 9; ++j1) {
            this.addSlot(new Slot(playerInventory, j1, 8 + j1 * 18, 142));
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return tile.canPlayerUse(player);
    }
}
