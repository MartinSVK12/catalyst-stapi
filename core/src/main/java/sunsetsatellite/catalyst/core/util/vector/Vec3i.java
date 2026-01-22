package sunsetsatellite.catalyst.core.util.vector;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.modificationstation.stationapi.api.world.BlockStateView;
import sunsetsatellite.catalyst.core.util.Axis;
import sunsetsatellite.catalyst.core.util.Direction;


public class Vec3i {
    public int x;
    public int y;
    public int z;

    public Vec3i(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

	public Vec3i(){
        this.x = this.y = this.z = 0;
    }

    public Vec3i(net.minecraft.util.math.Vec3i vec3i){
        this.x = vec3i.x;
        this.y = vec3i.y;
        this.z = vec3i.z;
    }

    public Vec3i(net.modificationstation.stationapi.api.util.math.Vec3i vec3i){
        this.x = vec3i.getX();
        this.y = vec3i.getY();
        this.z = vec3i.getZ();
    }

    public Vec3i(int size){
        this.x = this.y = this.z = size;
    }

    public Vec3i(NbtCompound tag){
        readFromNBT(tag);
    }

    public Vec3i(BlockPos pos) {
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
    }


    @Override
    public String toString() {
       return String.format("(%d, %d, %d)",x, y, z);
    }

    public double distanceTo(Vec3f vec3f) {
        double d = vec3f.x - this.x;
        double d1 = vec3f.y - this.y;
        double d2 = vec3f.z - this.z;
        return MathHelper.sqrt(d * d + d1 * d1 + d2 * d2);
    }

	public double distanceTo(Vec3i vec3i) {
		double d = vec3i.x - this.x;
		double d1 = vec3i.y - this.y;
		double d2 = vec3i.z - this.z;
		return MathHelper.sqrt(d * d + d1 * d1 + d2 * d2);
	}

	public void set(int x, int y, int z){
		this.x = x;
		this.y = y;
		this.z = z;
	}

    public Vec3i add(int value){
        this.x += value;
        this.y += value;
        this.z += value;
        return this;
    }

    public Vec3i subtract(int value){
        this.x -= value;
        this.y -= value;
        this.z -= value;
        return this;
    }

    public Vec3i divide(int value){
        this.x /= value;
        this.y /= value;
        this.z /= value;
        return this;
    }

    public Vec3i multiply(int value){
        this.x *= value;
        this.y *= value;
        this.z *= value;
        return this;
    }

    public Vec3i add(Vec3i value){
        this.x += value.x;
        this.y += value.y;
        this.z += value.z;
        return this;
    }

    public Vec3i subtract(Vec3i value){
        this.x -= value.x;
        this.y -= value.y;
        this.z -= value.z;
        return this;
    }

    public Vec3i divide(Vec3i value){
        this.x /= value.x;
        this.y /= value.y;
        this.z /= value.z;
        return this;
    }

    public Vec3i multiply(Vec3i value){
        this.x *= value.x;
        this.y *= value.y;
        this.z *= value.z;
        return this;
    }

    public Vec3i rotate(Vec3i origin, Direction direction){
        Vec3i pos = this;
        pos = switch (direction) {
            case Z_POS -> new Vec3i(this.z + origin.x, this.y + origin.y, this.x + origin.z);
            case Z_NEG -> new Vec3i(-this.z + origin.x, this.y + origin.y, -this.x + origin.z);
            case X_NEG -> new Vec3i(-this.x + origin.x, this.y + origin.y, -this.z + origin.z);
            case X_POS -> new Vec3i(this.x + origin.x, this.y + origin.y, this.z + origin.z);
            default -> pos;
        };
        return pos;
    }

    public Vec3i rotate(Direction direction){
        Vec3i pos = this;
        pos = switch (direction) {
            case Z_POS -> new Vec3i(this.z, this.y, this.x);
            case Z_NEG -> new Vec3i(-this.z, this.y, -this.x);
            case X_NEG -> new Vec3i(-this.x, this.y, -this.z);
            case X_POS -> new Vec3i(this.x, this.y, this.z);
            default -> pos;
        };
        return pos;
    }

	public Vec3i rotateX(double angle){
		float cosine = MathHelper.cos((float) angle);
		float sine = MathHelper.sin((float) angle);
		y = (int) Math.round(y * (double)cosine + z * (double)sine);
		z = (int) Math.round(z * (double)cosine - y * (double)sine);
		return this;
	}

	public Vec3i rotateY(double angle){
		float cosine = MathHelper.cos((float) angle);
		float sine = MathHelper.sin((float) angle);
		x = (int) Math.round(x * (double)cosine + z * (double)sine);
		z = (int) Math.round(z * (double)cosine - x * (double)sine);
		return this;
	}

	public Vec3i rotateX(Vec3i origin, double angle){
		this.add(origin);
		float cosine = MathHelper.cos((float) angle);
		float sine = MathHelper.sin((float) angle);
		y = (int) Math.round(y * (double)cosine + z * (double)sine);
		z = (int) Math.round(z * (double)cosine - y * (double)sine);
		return this;
	}

	public Vec3i rotateY(Vec3i origin, double angle){
		float cosine = MathHelper.cos((float) angle);
		float sine = MathHelper.sin((float) angle);
		x = (int) Math.round(x * (double)cosine + z * (double)sine);
		z = (int) Math.round(z * (double)cosine - x * (double)sine);
		this.add(origin);
		return this;
	}

	public Vec3i set(Axis axis, int value){
        return switch (axis) {
            case X -> {
                this.x = value;
                yield this;
            }
            case Y -> {
                this.y = value;
                yield this;
            }
            case Z -> {
                this.z = value;
                yield this;
            }
        };
	}

	public int get(Axis axis){
        return switch (axis) {
            case X -> x;
            case Y -> y;
            case Z -> z;
        };
	}

	public void writeToNBT(NbtCompound tag){
        tag.putInt("x",this.x);
        tag.putInt("y",this.y);
        tag.putInt("z",this.z);
    }

    public void readFromNBT(NbtCompound tag){
        this.x = tag.getInt("x");
        this.y = tag.getInt("y");
        this.z = tag.getInt("z");
    }

    public Vec3i copy(){
        return new Vec3i(this.x,this.y,this.z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vec3i vec3I = (Vec3i) o;

        if (x != vec3I.x) return false;
        if (y != vec3I.y) return false;
        return z == vec3I.z;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + z;
        return result;
    }

	public BlockEntity getTileEntity(BlockView worldSource){
		return worldSource.getBlockEntity(this.x, this.y, this.z);
	}

	public Block getBlock(BlockStateView worldSource){
		return worldSource.getBlockState(this.x, this.y, this.z).getBlock();
	}

	public int getBlockMetadata(BlockView worldSource){
		return worldSource.getBlockMeta(this.x, this.y, this.z);
	}

	public double getSqDistanceTo(int x, int y, int z) {
		int dx = this.x - x;
		int dy = this.y - y;
		int dz = this.z - z;
		return Math.sqrt(dx * dx + dy * dy + dz * dz);
	}
}
