package sunsetsatellite.catalyst.core.util.model;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlas;
import net.modificationstation.stationapi.api.state.StateManager;
import net.modificationstation.stationapi.api.template.block.TemplateBlock;
import net.modificationstation.stationapi.api.template.block.TemplateBlockWithEntity;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;
import net.modificationstation.stationapi.api.world.BlockStateView;
import org.jetbrains.annotations.Nullable;

import static net.modificationstation.stationapi.api.state.property.Properties.FACING;

public abstract class FullyRotatableBlockWithEntity extends TemplateBlockWithEntity implements LayeredCubeModel {

    public static final int[] ORIENTATION_HORIZONTAL = new int[]{
            0, 1, 3, 2, 4, 5,
            0, 1, 3, 2, 4, 5,
            0, 1, 2, 3, 5, 4,
            0, 1, 3, 2, 4, 5,
            0, 1, 4, 5, 2, 3,
            0, 1, 5, 4, 3, 2
    };

    public static final int[] ORIENTATION_VERTICAL = new int[]{
            1, 0, 2, 3, 4, 5,
            0, 1, 2, 3, 4, 5
    };

    private static final Direction[] DIRECTIONS = new Direction[]{Direction.DOWN, Direction.UP, Direction.WEST, Direction.EAST, Direction.SOUTH, Direction.NORTH};

    public final TextureLayer BASE = new TextureLayer(0);
    public final TextureLayer ACTIVE = new TextureLayer(1);
    public final TextureLayer OVERLAY = new TextureLayer(2);

    public final TextureLayer[] LAYERS = new TextureLayer[]{BASE,ACTIVE,OVERLAY};

    public FullyRotatableBlockWithEntity(Identifier identifier, Material material) {
        super(identifier, material);
    }

    public static int getFacingForPlacement(World world, int x, int y, int z, PlayerEntity player) {
        if (MathHelper.abs((float)player.x - (float)x) < 2.0F && MathHelper.abs((float)player.z - (float)z) < 2.0F) {
            double var5 = player.y + 1.82 - (double)player.standingEyeHeight;
            if (var5 - (double)y > (double)2.0F) {
                return 1;
            }

            if ((double)y - var5 > (double)0.0F) {
                return 0;
            }
        }

        int var7 = MathHelper.floor((double)(player.yaw * 4.0F / 360.0F) + (double)0.5F) & 3;
        if (var7 == 0) {
            return 2;
        } else if (var7 == 1) {
            return 5;
        } else if (var7 == 2) {
            return 3;
        } else {
            return var7 == 3 ? 4 : 0;
        }
    }

    @Override
    public void onPlaced(World level, int x, int y, int z, LivingEntity living) {
        super.onPlaced(level, x, y, z, living);
        int facing = getFacingForPlacement(level, x, y, z, (PlayerEntity) living);
        level.setBlockState(x, y, z, getDefaultState().with(FACING, DIRECTIONS[facing]));
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING);
    }

    @Override
    public int getTexture(int side, int meta) {
        boolean isVertical = meta == 0 || meta == 1;
        int index = ORIENTATION_HORIZONTAL[6 * Math.min(meta, 5) + side];
        if(isVertical){
            index = ORIENTATION_VERTICAL[6 * meta + side];
        }
        if(BASE.get(index) != null){
            // cope
            //noinspection DataFlowIssue
            return BASE.get(index).index;
        }
        return super.getTexture(side, meta);
    }

    @Override
    public TextureLayer[] getTextureLayers() {
        return LAYERS;
    }

    @Override
    public boolean isLayerFullbright(int layer) {
        return layer == OVERLAY.getIndex();
    }

    @Override
    public Atlas.@Nullable Sprite getLayerTexture(BlockView view, BlockStateView blockStateView, int x, int y, int z, int meta, int side, int layer) {
        return switch (layer) {
            case 0 -> getBaseTexture(view, blockStateView, x, y, z, meta, side);
            case 1 -> getActiveTexture(view, blockStateView, x, y, z, meta, side);
            case 2 -> getOverlayTexture(view, blockStateView, x, y, z, meta, side);
            default -> null;
        };
    }

    public Atlas.Sprite getBaseTexture(BlockView view, BlockStateView blockStateView, int x, int y, int z, int meta, int side){
        boolean isVertical = meta == 0 || meta == 1;
        int facing = blockStateView.getBlockState(x, y, z).get(FACING).getId();
        int index = ORIENTATION_HORIZONTAL[6 * Math.min(facing, 5) + side];
        if(isVertical){
            index = ORIENTATION_VERTICAL[6 * meta + side];
        }
        return BASE.get(index);
    }

    public Atlas.Sprite getActiveTexture(BlockView view, BlockStateView blockStateView, int x, int y, int z, int meta, int side){
        boolean isVertical = meta == 0 || meta == 1;
        int facing = blockStateView.getBlockState(x, y, z).get(FACING).getId();
        int index = ORIENTATION_HORIZONTAL[6 * Math.min(facing, 5) + side];
        if(isVertical){
            index = ORIENTATION_VERTICAL[6 * meta + side];
        }
        return ACTIVE.get(index);
    }

    public Atlas.Sprite getOverlayTexture(BlockView view, BlockStateView blockStateView, int x, int y, int z, int meta, int side){
        boolean isVertical = meta == 0 || meta == 1;
        int facing = blockStateView.getBlockState(x, y, z).get(FACING).getId();
        int index = ORIENTATION_HORIZONTAL[6 * Math.min(facing, 5) + side];
        if(isVertical){
            index = ORIENTATION_VERTICAL[6 * meta + side];
        }
        return OVERLAY.get(index);
    }

    @Override
    public boolean renderLayer(BlockView view, BlockStateView blockStateView, int x, int y, int z, int meta, int layer) {
        return true;
    }
}
