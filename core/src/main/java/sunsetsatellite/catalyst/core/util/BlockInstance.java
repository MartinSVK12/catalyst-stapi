package sunsetsatellite.catalyst.core.util;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.state.property.Property;
import org.jetbrains.annotations.NotNull;
import sunsetsatellite.catalyst.core.util.vector.Vec3i;

import java.util.Objects;

public class BlockInstance {
    @NotNull
    public Block block;
    @NotNull
    public Vec3i pos;
    public int meta = 0;
    public BlockEntity tile;
    @NotNull
    public BlockState state;
	public Vec3i offset;

    public BlockInstance(@NotNull Vec3i pos, @NotNull World world){
        this.block = Objects.requireNonNull(world.getBlockState(pos.x, pos.y, pos.z).getBlock());
        this.pos = pos;
        this.tile = world.getBlockEntity(pos.x, pos.y, pos.z);
        this.meta = world.getBlockMeta(pos.x, pos.y, pos.z);
        this.state = world.getBlockState(pos.x, pos.y, pos.z);
    }

    public BlockInstance(@NotNull Block block, @NotNull Vec3i pos, int meta, @NotNull BlockState state, BlockEntity tile){
        this.block = block;
        this.pos = pos;
        this.tile = tile;
        this.state = state;
        this.meta = meta;
    }

    public BlockInstance(@NotNull Block block, @NotNull Vec3i pos, @NotNull BlockState state, BlockEntity tile){
        this.block = block;
        this.pos = pos;
        this.tile = tile;
        this.state = state;
    }

    public boolean exists(World world){
        Block block = world.getBlockState(pos.x, pos.y, pos.z).getBlock();
        int meta = world.getBlockMeta(pos.x, pos.y, pos.z);
        BlockState state = world.getBlockState(pos.x, pos.y, pos.z);
        boolean stateMatches = state.getEntries().entrySet().stream().allMatch((E) -> {
            Property<?> K = E.getKey();
            Comparable<?> V = E.getValue();
            return this.state.contains(K) && this.state.get(K).equals(V);
        });
        return block == this.block && ((meta == this.meta && stateMatches) || this.meta == -1);
    }

    public boolean existsWithTile(World world){
        Block block = world.getBlockState(pos.x, pos.y, pos.z).getBlock();
        int meta = world.getBlockMeta(pos.x, pos.y, pos.z);
        BlockEntity tile = world.getBlockEntity(pos.x, pos.y, pos.z);
        BlockState state = world.getBlockState(pos.x, pos.y, pos.z);
        boolean stateMatches = state.getEntries().entrySet().stream().allMatch((E) -> {
            Property<?> K = E.getKey();
            Comparable<?> V = E.getValue();
            return this.state.contains(K) && this.state.get(K).equals(V);
        });
        return block == this.block && ((meta == this.meta && stateMatches) || this.meta == -1) && tile == this.tile;
    }

	public boolean place(World world){
		if(world.getBlockId(pos.x, pos.y, pos.z) == 0){
			world.setBlock(pos.x, pos.y, pos.z, block.id, meta);
            world.setBlockState(pos.x, pos.y, pos.z, state);
			return true;
		}
		return false;
	}

    @Override
    public String toString() {
        return "BlockInstance{" +
                "block=" + block +
                ", pos=" + pos +
                ", meta=" + meta +
                ", tile=" + tile +
                '}';
    }

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof BlockInstance that)) return false;

        boolean stateMatches = that.state.getEntries().entrySet().stream().allMatch((E) -> {
            Property<?> K = E.getKey();
            Comparable<?> V = E.getValue();
            return this.state.contains(K) && this.state.get(K).equals(V);
        });
		return ((that.meta == this.meta && stateMatches) || this.meta == -1) && Objects.equals(block, that.block) && Objects.equals(pos, that.pos) && Objects.equals(tile, that.tile);
	}

	@Override
    public int hashCode() {
        int result = block.hashCode();
        result = 31 * result + pos.hashCode();
        result = 31 * result + meta;
        result = 31 * result + (tile != null ? tile.hashCode() : 0);
        result = 31 * result + state.hashCode();
        return result;
    }
}
