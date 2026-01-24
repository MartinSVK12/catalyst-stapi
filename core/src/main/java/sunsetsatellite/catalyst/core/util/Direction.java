package sunsetsatellite.catalyst.core.util;


import lombok.Getter;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.world.BlockStateView;
import org.lwjgl.util.vector.Vector3f;
import sunsetsatellite.catalyst.core.util.vector.Vec3f;
import sunsetsatellite.catalyst.core.util.vector.Vec3i;

import java.util.List;

// b1.7.3 does directions differently apparently??
public enum Direction {
	/**NORTH, 5, X*/
    X_POS (new Vec3i(1,0,0),5,"NORTH", Axis.X, (3*Math.PI)/2),
	/**SOUTH, 4, X*/
	X_NEG (new Vec3i(-1,0,0),4,"SOUTH", Axis.X, Math.PI/2),
	/**UP, 1, Y*/
	Y_POS (new Vec3i(0,1,0),1,"UP", Axis.Y, 0.0f),
	/**DOWN, 0, Y*/
	Y_NEG (new Vec3i(0,-1,0),0,"DOWN", Axis.Y, 0.0f),
	/**WEST, 3, Z*/
	Z_POS (new Vec3i(0,0,1),3,"WEST", Axis.Z, Math.PI),
	/**EAST, 2, Z*/
	Z_NEG (new Vec3i(0,0,-1),2,"EAST", Axis.Z, 0.0f);


	private final Vec3i vec;
    @Getter
    private Direction opposite;
    private final int side;
    @Getter
    private final String name;
	@Getter
    private final Axis axis;
    /**
     * Angle in radians from North for horizontal directions, vertical directions return 0
     */
    @Getter
    private final double angle;

    Direction(Vec3i vec3I, int side, String name, Axis axis, double angle) {
        this.vec = vec3I;
        this.side = side;
        this.name = name;
        this.axis = axis;
		this.angle = angle;
	}

    public static Direction get(net.modificationstation.stationapi.api.util.math.Direction direction){
        return switch (direction) {
            case EAST -> Direction.Z_NEG;
            case WEST -> Direction.Z_POS;
            case NORTH -> Direction.X_NEG;
            case SOUTH -> Direction.X_POS;
            case UP -> Direction.Y_POS;
            case DOWN -> Direction.Y_NEG;
        };
    }

    public net.modificationstation.stationapi.api.util.math.Direction to(){
        return switch (this) {
            case Z_NEG -> net.modificationstation.stationapi.api.util.math.Direction.EAST;
            case Z_POS -> net.modificationstation.stationapi.api.util.math.Direction.WEST;
            case Y_POS -> net.modificationstation.stationapi.api.util.math.Direction.UP;
            case Y_NEG -> net.modificationstation.stationapi.api.util.math.Direction.DOWN;
            case X_POS -> net.modificationstation.stationapi.api.util.math.Direction.SOUTH;
            case X_NEG -> net.modificationstation.stationapi.api.util.math.Direction.NORTH;
        };
    }

    public BlockEntity getTileEntity(BlockView world, BlockEntity tile){
        Vec3i pos = new Vec3i(tile.x + vec.x, tile.y + vec.y, tile.z + vec.z);
        return world.getBlockEntity(pos.x,pos.y,pos.z);
    }

	public Block getBlock(BlockStateView world, BlockEntity tile){
		Vec3i pos = new Vec3i(tile.x + vec.x, tile.y + vec.y, tile.z + vec.z);
		return world.getBlockState(pos.x,pos.y,pos.z).getBlock();
	}

	public Block getBlock(BlockStateView world, Vec3i baseVec){
		Vec3i pos = new Vec3i(baseVec.x + vec.x, baseVec.y + vec.y, baseVec.z + vec.z);
		return world.getBlockState(pos.x,pos.y,pos.z).getBlock();
	}

    public BlockState getBlockState(BlockStateView world, BlockEntity tile){
        Vec3i pos = new Vec3i(tile.x + vec.x, tile.y + vec.y, tile.z + vec.z);
        return world.getBlockState(pos.x,pos.y,pos.z);
    }

    public BlockState getBlockState(BlockStateView world, Vec3i baseVec){
        Vec3i pos = new Vec3i(baseVec.x + vec.x, baseVec.y + vec.y, baseVec.z + vec.z);
        return world.getBlockState(pos.x,pos.y,pos.z);
    }

    public int getBlockMeta(BlockView world, BlockEntity tile){
        Vec3i pos = new Vec3i(tile.x + vec.x, tile.y + vec.y, tile.z + vec.z);
        return world.getBlockMeta(pos.x,pos.y,pos.z);
    }

    public int getBlockMeta(BlockView world, Vec3i baseVec){
        Vec3i pos = new Vec3i(baseVec.x + vec.x, baseVec.y + vec.y, baseVec.z + vec.z);
        return world.getBlockMeta(pos.x,pos.y,pos.z);
    }

    public BlockEntity getTileEntity(BlockView world, Vec3i baseVec){
        Vec3i pos = new Vec3i(baseVec.x + vec.x, baseVec.y + vec.y, baseVec.z + vec.z);
        return world.getBlockEntity(pos.x,pos.y,pos.z);
    }

    public Vec3i getVec() {
        return vec.copy();
    }

	public static Vec3i[] getVecs(){
		Vec3i[] vecs = new Vec3i[Direction.values().length];
		for (int i = 0; i < Direction.values().length; i++) {
			vecs[i] = Direction.values()[i].getVec();
		}
		return vecs;
	}

    public static Direction getDirectionFromSide(int side){
        for (Direction dir : values()) {
            if(dir.side == side){
                return dir;
            }
        }
        return Direction.X_NEG;
    }

    public static Direction getFromName(String name) {
        for (Direction dir : values()) {
            if(dir.name.equalsIgnoreCase(name)){
                return dir;
            }
        }
        return null;
    }

    public Direction rotate(int amount){
        if(this == Y_POS || this == Y_NEG) return this;
        List<Direction> horizontalDirs = List.of(Z_NEG,X_POS,Z_POS,X_NEG);
        return horizontalDirs.get(horizontalDirs.indexOf(this) + amount & 3);
    }

    /**
     * Gets minecraft's side number, NOTE: this and .ordinal() aren't the same!
     * @return Minecraft's side number.
     */
    public int getSideNumber() {
        return side;
    }

    public Vec3f getVecF(){
        return new Vec3f(vec.x, vec.y, vec.z);
    }

    /**
	 * @return Z direction if provided a X direction or X direction if provided Z direction
	 */
	public Direction shiftAxis() {
        return switch (this) {
            case X_POS -> Direction.Z_POS;
            case X_NEG -> Direction.Z_NEG;
            case Z_POS -> Direction.X_POS;
            case Z_NEG -> Direction.X_NEG;
            default -> this;
        };
    }

    static {
        X_POS.opposite = X_NEG;
        X_NEG.opposite = X_POS;
        Y_NEG.opposite = Y_POS;
        Y_POS.opposite = Y_NEG;
        Z_NEG.opposite = Z_POS;
        Z_POS.opposite = Z_NEG;
    }

    public static Vector3f[] getVerticesForSide(Direction dir) {
        float min = -0.5f;
        float max = 0.5f;

        return switch (dir) {
            case Y_POS -> // TOP
                    new Vector3f[]{
                            new Vector3f(min, max, min),
                            new Vector3f(min, max, max),
                            new Vector3f(max, max, max),
                            new Vector3f(max, max, min)
                    };
            case Y_NEG -> // BOTTOM
                    new Vector3f[]{
                            new Vector3f(min, min, min),
                            new Vector3f(max, min, min),
                            new Vector3f(max, min, max),
                            new Vector3f(min, min, max)
                    };
            case Z_NEG -> // NORTH
                    new Vector3f[]{
                            new Vector3f(min, min, min),
                            new Vector3f(min, max, min),
                            new Vector3f(max, max, min),
                            new Vector3f(max, min, min)
                    };
            case Z_POS -> // SOUTH
                    new Vector3f[]{
                            new Vector3f(min, min, max),
                            new Vector3f(max, min, max),
                            new Vector3f(max, max, max),
                            new Vector3f(min, max, max)
                    };
            case X_NEG -> // WEST
                    new Vector3f[]{
                            new Vector3f(min, min, min),
                            new Vector3f(min, min, max),
                            new Vector3f(min, max, max),
                            new Vector3f(min, max, min)
                    };
            case X_POS -> // EAST
                    new Vector3f[]{
                            new Vector3f(max, min, min),
                            new Vector3f(max, max, min),
                            new Vector3f(max, max, max),
                            new Vector3f(max, min, max)
                    };
            default -> new Vector3f[0];
        };
    }
}
