package sunsetsatellite.catalyst.multipart.block;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.gui.screen.container.GuiHelper;
import net.modificationstation.stationapi.api.template.block.TemplateBlockWithEntity;
import net.modificationstation.stationapi.api.util.Identifier;
import sunsetsatellite.catalyst.CatalystMultipart;
import sunsetsatellite.catalyst.core.util.Side;
import sunsetsatellite.catalyst.core.util.io.InventoryWrapper;
import sunsetsatellite.catalyst.multipart.block.entity.CarpenterWorkbenchBlockEntity;
import sunsetsatellite.catalyst.multipart.screen.handler.CarpenterWorkbenchScreenHandler;

public class CarpenterWorkbenchBlock extends TemplateBlockWithEntity {
    public CarpenterWorkbenchBlock(Identifier identifier, Material material) {
        super(identifier, material);
    }

    @Override
    protected BlockEntity createBlockEntity() {
        return new CarpenterWorkbenchBlockEntity();
    }

    @Override
    public int getTexture(int s) {
        Side side = Side.values()[s];
        switch (side) {
            case DOWN -> {
                return CatalystMultipart.carpenterWorkbenchBottom;
            }
            case UP -> {
                return CatalystMultipart.carpenterWorkbenchTop;
            }
            case NORTH -> {
                return CatalystMultipart.carpenterWorkbenchFront;
            }
            case SOUTH, WEST, EAST -> {
                return CatalystMultipart.carpenterWorkbenchSide;
            }
        }
        return super.getTexture(s);
    }

    @Override
    public boolean onUse(World world, int x, int y, int z, PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(x, y, z);
        GuiHelper.openGUI(player,CatalystMultipart.NAMESPACE.id("open_carpenter_workbench"), (Inventory) blockEntity,new CarpenterWorkbenchScreenHandler(player.inventory, (CarpenterWorkbenchBlockEntity) blockEntity));
        return true;
    }

    @Override
    public void onBreak(World world, int x, int y, int z) {
        BlockEntity blockEntity = world.getBlockEntity(x, y, z);
        InventoryWrapper inv = new InventoryWrapper((Inventory) blockEntity);
        inv.ejectAll(world, x, y, z);
        super.onBreak(world, x, y, z);
    }
}
