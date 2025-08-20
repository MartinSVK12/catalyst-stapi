package sunsetsatellite.catalyst.core.util.model;

import lombok.Getter;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlas;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;
import net.modificationstation.stationapi.api.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sunsetsatellite.catalyst.core.util.Side;

import java.util.Arrays;

public class TextureLayer {
    public final Atlas.Sprite[] coordinates = new Atlas.Sprite[6];
    @Getter
    private final int index;
    @Getter
    private boolean hasTexture = false;

    public TextureLayer(int index) {
        this.index = index;
    }

    public TextureLayer set(@Nullable Atlas.Sprite coordinate, Side... sides) {
        for(Side s : sides) {

            this.coordinates[s.ordinal()] = coordinate;
        }

        this.hasTexture = false;
        int i = 0;

        for(int coordinatesLength = this.coordinates.length; i < coordinatesLength; ++i) {
            Atlas.Sprite iconCoordinate = this.coordinates[i];
            this.hasTexture |= iconCoordinate != null;
        }

        return this;
    }

    public TextureLayer set(@Nullable Identifier texture, Side... sides) {
        return this.set(texture == null ? null : Atlases.getTerrain().addTexture(texture), sides);
    }

    public TextureLayer setAll(@Nullable Atlas.Sprite coordinate) {
        Arrays.fill(this.coordinates, coordinate);
        this.hasTexture = coordinate != null;
        return this;
    }

    public TextureLayer setAll(@Nullable Identifier texture) {
        return this.setAll(texture == null ? null : Atlases.getTerrain().addTexture(texture));
    }

    public TextureLayer copy(@NotNull TextureLayer layer) {
        System.arraycopy(layer.coordinates, 0, this.coordinates, 0, 6);
        this.hasTexture = layer.hasTexture;
        return this;
    }

    public @Nullable Atlas.Sprite get(@NotNull Side side) {

        return this.coordinates[side.ordinal()];
    }

    public @Nullable Atlas.Sprite get(int index) {
        assert index >= 0 : "Index must be greater then or equal to 0!";

        assert index < this.coordinates.length : "Index must be less then " + this.coordinates.length + "!";

        return this.coordinates[index];
    }
    
}
