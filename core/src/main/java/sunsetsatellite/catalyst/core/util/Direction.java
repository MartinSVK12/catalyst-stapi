package sunsetsatellite.catalyst.core.util;


import lombok.Getter;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;
import net.modificationstation.stationapi.api.world.BlockStateView;
import sunsetsatellite.catalyst.core.util.vector.Vec3f;
import sunsetsatellite.catalyst.core.util.vector.Vec3i;

import java.util.List;

public enum Direction {
	/**EAST, 5, X*/
    X_POS (new Vec3i(1,0,0),5,"EAST", Axis.X, (3*Math.PI)/2),
	/**WEST, 4, X*/
	X_NEG (new Vec3i(-1,0,0),4,"WEST", Axis.X, Math.PI/2),
	/**UP, 1, Y*/
	Y_POS (new Vec3i(0,1,0),1,"UP", Axis.Y, 0.0f),
	/**DOWN, 0, Y*/
	Y_NEG (new Vec3i(0,-1,0),0,"DOWN", Axis.Y, 0.0f),
	/**SOUTH, 3, Z*/
	Z_POS (new Vec3i(0,0,1),3,"SOUTH", Axis.Z, Math.PI),
	/**NORTH, 2, Z*/
	Z_NEG (new Vec3i(0,0,-1),2,"NORTH", Axis.Z, 0.0f);


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

}
