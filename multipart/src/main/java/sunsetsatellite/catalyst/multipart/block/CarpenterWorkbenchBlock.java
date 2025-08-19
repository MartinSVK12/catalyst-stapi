package sunsetsatellite.catalyst.multipart.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.gui.screen.container.GuiHelper;
import net.modificationstation.stationapi.api.template.block.TemplateBlockWithEntity;
import net.modificationstation.stationapi.api.util.Identifier;
import sunsetsatellite.catalyst.CatalystMultipart;
import sunsetsatellite.catalyst.CatalystMultipartClient;
import sunsetsatellite.catalyst.core.util.Side;
import sunsetsatellite.catalyst.core.util.io.InventoryWrapper;
import sunsetsatellite.catalyst.multipart.block.entity.CarpenterWorkbenchBlockEntity;
import sunsetsatellite.catalyst.multipart.screen.handler.CarpenterWorkbenchScreenHandler;

import java.util.List;
import java.util.Random;

import static sunsetsatellite.catalyst.CatalystMultipartClient.*;
import static sunsetsatellite.catalyst.CatalystMultipartClient.carpenterWorkbenchSide;

public class CarpenterWorkbenchBlock extends TemplateBlockWithEntity {
    public CarpenterWorkbenchBlock(Identifier identifier, Material material) {
        super(identifier, material);
    }

    @Override
    protected BlockEntity createBlockEntity() {
        return new CarpenterWorkbenchBlockEntity();
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int getTexture(int s) {
        Side side = Side.values()[s];
        switch (side) {
            case DOWN -> {
                return carpenterWorkbenchBottom;
            }
            case UP -> {
                return carpenterWorkbenchTop;
            }
            case NORTH -> {
                return carpenterWorkbenchFront;
            }
            case SOUTH, WEST, EAST -> {
                return carpenterWorkbenchSide;
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
        if(!world.isRemote){
            BlockEntity blockEntity = world.getBlockEntity(x, y, z);
            InventoryWrapper inv = new InventoryWrapper((Inventory) blockEntity);
            inv.ejectAll(world, x, y, z);
        }
        super.onBreak(world, x, y, z);
    }

    @Override
    public List<ItemStack> getDropList(World world, int x, int y, int z, BlockState state, int meta) {
        return List.of(new ItemStack(this,1));
    }
}
