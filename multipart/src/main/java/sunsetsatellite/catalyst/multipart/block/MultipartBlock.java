package sunsetsatellite.catalyst.multipart.block;

import com.mojang.datafixers.util.Pair;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.template.block.TemplateBlockWithEntity;
import net.modificationstation.stationapi.api.util.Identifier;
import sunsetsatellite.catalyst.Catalyst;
import sunsetsatellite.catalyst.core.util.Direction;
import sunsetsatellite.catalyst.core.util.Side;
import sunsetsatellite.catalyst.core.util.section.BlockSection;
import sunsetsatellite.catalyst.core.util.section.SideInteractable;
import sunsetsatellite.catalyst.core.util.vector.Vec2f;
import sunsetsatellite.catalyst.core.util.EnhancedBlockInteraction;
import sunsetsatellite.catalyst.multipart.api.Multipart;
import sunsetsatellite.catalyst.multipart.api.SupportsMultiparts;
import sunsetsatellite.catalyst.multipart.block.entity.MultipartBlockEntity;

public class MultipartBlock extends TemplateBlockWithEntity implements MultipartRender, SideInteractable, EnhancedBlockInteraction {
    public MultipartBlock(Identifier identifier, Material material) {
        super(identifier, material);
    }

    @Override
    public void onBlockBreakStart(World world, int x, int y, int z, PlayerEntity player, Vec2f clickPosition) {
        Side side = Side.values()[Minecraft.INSTANCE.crosshairTarget.side];
        Side playerFacing = Catalyst.calculatePlayerFacing(player.yaw);
        Pair<Direction, BlockSection> pair = Catalyst.getBlockSurfaceClickPosition(world, player, side, clickPosition);
        Direction dir = pair.getSecond().toDirection(pair.getFirst(), playerFacing);
        BlockEntity tile = world.getBlockEntity(x, y, z);
        if(tile instanceof SupportsMultiparts multipartTile) {
            Multipart multipart = multipartTile.getParts().replace(dir,null);
            if(multipart != null){
                world.setBlockDirty(x,y,z);
                dropStack(world,x,y,z,multipart.getStack(1));
            }
            if(multipartTile.getParts().entrySet().stream().allMatch(entry -> entry.getValue() == null)){
                world.setBlock(x,y,z,0);
                world.removeBlockEntity(x,y,z);
            }
        }
    }

    @Override
    public void onBreak(World world, int x, int y, int z) {
        BlockEntity tile = world.getBlockEntity(x, y, z);
        if(tile instanceof SupportsMultiparts multipartTile) {
            multipartTile.getParts().forEach((dir,multipart) -> {
                if(multipart != null) dropStack(world,x,y,z,multipart.getStack(1));
            });
        }
        super.onBreak(world, x, y, z);
    }

    @Override
    public boolean isOpaque() {
        return false;
    }

    @Override
    public boolean isFullCube() {
        return false;
    }

    @Override
    protected BlockEntity createBlockEntity() {
        return new MultipartBlockEntity();
    }

    @Override
    public Multipart getMultipart(ItemStack stack) {
        return null;
    }

    @Override
    public boolean needsItemToShowOutline() {
        return false;
    }
}
