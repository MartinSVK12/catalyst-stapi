package sunsetsatellite.catalyst.core.util.model;

import net.minecraft.world.BlockView;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlas;
import net.modificationstation.stationapi.api.world.BlockStateView;
import org.jetbrains.annotations.Nullable;

public interface LayeredCubeModel {
    TextureLayer[] getTextureLayers();

    boolean isLayerFullbright(int layer);

    Atlas.@Nullable Sprite getLayerTexture(BlockView view, BlockStateView blockStateView, int x, int y, int z, int meta, int side, int layer);

    boolean renderLayer(BlockView view, int x, int y, int z, int meta, int layer);
}
