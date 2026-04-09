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
            2, 3, 0, 1, 4, 5,
            3, 2, 0, 1, 4, 5
    };

    private static final Direction[] DIRECTIONS = new Direction[] { Direction.WEST, Direction.NORTH, Direction.EAST, Direction.SOUTH };

    public final TextureLayer BASE = new TextureLayer(0);
    public final TextureLayer ACTIVE = new TextureLayer(1);
    public final TextureLayer OVERLAY = new TextureLayer(2);

    public final TextureLayer[] LAYERS = new TextureLayer[]{BASE,ACTIVE,OVERLAY};

    public FullyRotatableBlockWithEntity(Identifier identifier, Material material) {
        super(identifier, material);
    }

    @Override
    public void onPlaced(World level, int x, int y, int z, LivingEntity living) {
        super.onPlaced(level, x, y, z, living);
        Direction dir = DIRECTIONS[MathHelper.floor((double) (living.yaw * 4.0F / 360.0F) + 0.5D) & 3].getOpposite();
        if(living.pitch > 70f){
            dir = Direction.UP;
        } else if (living.pitch < -70f) {
            dir = Direction.DOWN;
        }
        level.setBlockState(x, y, z, getDefaultState().with(FACING,dir));
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING);
    }

    @Override
    public int getTexture(int side, int meta) {
        int index = ORIENTATION_HORIZONTAL[6 * Math.min(meta, 5) + side];
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
        int facing = blockStateView.getBlockState(x, y, z).get(FACING).getId();
        boolean isVertical = facing == 0 || facing == 1;
        int index = ORIENTATION_HORIZONTAL[6 * Math.min(facing, 5) + side];
        if(isVertical){
            index = ORIENTATION_VERTICAL[6 * facing + side];
        }
        return BASE.get(index);
    }

    public Atlas.Sprite getActiveTexture(BlockView view, BlockStateView blockStateView, int x, int y, int z, int meta, int side){
        int facing = blockStateView.getBlockState(x, y, z).get(FACING).getId();
        boolean isVertical = facing == 0 || facing == 1;
        int index = ORIENTATION_HORIZONTAL[6 * Math.min(facing, 5) + side];
        if(isVertical){
            index = ORIENTATION_VERTICAL[6 * facing + side];
        }
        return ACTIVE.get(index);
    }

    public Atlas.Sprite getOverlayTexture(BlockView view, BlockStateView blockStateView, int x, int y, int z, int meta, int side){
        int facing = blockStateView.getBlockState(x, y, z).get(FACING).getId();
        boolean isVertical = facing == 0 || facing == 1;
        int index = ORIENTATION_HORIZONTAL[6 * Math.min(facing, 5) + side];
        if(isVertical){
            index = ORIENTATION_VERTICAL[6 * facing + side];
        }
        return OVERLAY.get(index);
    }

    @Override
    public boolean renderLayer(BlockView view, BlockStateView blockStateView, int x, int y, int z, int meta, int layer) {
        return true;
    }
}
