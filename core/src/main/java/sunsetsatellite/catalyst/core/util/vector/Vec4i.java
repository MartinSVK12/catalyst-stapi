package sunsetsatellite.catalyst.core.util.vector;


import net.minecraft.nbt.NbtCompound;

public class Vec4i {
    public int x;
    public int y;
    public int z;
    public int w;

    public Vec4i(int x, int y, int z, int w){
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vec4i(){
        this.x = this.y = this.z = this.w = 0;
    }

    public Vec4i(int size){
        this.x = this.y = this.z = this.w = size;
    }

    public Vec4i(NbtCompound tag){
        readFromNBT(tag);
    }


    public double distanceTo(Vec4i vec3f) {
        int d = vec3f.x - this.x;
        int d1 = vec3f.y - this.y;
        int d2 = vec3f.z - this.z;
        int d3 = vec3f.w - this.w;
        return Math.sqrt(d * d + d1 * d1 + d2 * d2 + d3 * d3);
    }

    public Vec4i add(int value){
        this.x += value;
        this.y += value;
        this.z += value;
        this.w += value;
        return this;
    }

    public Vec4i subtract(int value){
        this.x -= value;
        this.y -= value;
        this.z -= value;
        this.w -= value;
        return this;
    }

    public Vec4i divide(int value){
        this.x /= value;
        this.y /= value;
        this.z /= value;
        this.w /= value;
        return this;
    }

    public Vec4i multiply(int value){
        this.x *= value;
        this.y *= value;
        this.z *= value;
        this.w *= value;
        return this;
    }

    public Vec4i add(Vec4i value){
        this.x += value.x;
        this.y += value.y;
        this.z += value.z;
        this.w += value.w;
        return this;
    }

    public Vec4i subtract(Vec4i value){
        this.x -= value.x;
        this.y -= value.y;
        this.z -= value.z;
        this.w -= value.w;
        return this;
    }

    public Vec4i divide(Vec4i value){
        this.x /= value.x;
        this.y /= value.y;
        this.z /= value.z;
        this.w /= value.w;
        return this;
    }

    public Vec4i multiply(Vec4i value){
        this.x *= value.x;
        this.y *= value.y;
        this.z *= value.z;
        this.w *= value.w;
        return this;
    }

    public NbtCompound writeToNBT(NbtCompound tag){
        tag.putInt("x",this.x);
        tag.putInt("y",this.y);
        tag.putInt("z",this.z);
        tag.putInt("w",this.w);
        return tag;
    }

    public void readFromNBT(NbtCompound tag){
        this.x = tag.getInt("x");
        this.y = tag.getInt("y");
        this.z = tag.getInt("z");
        this.w = tag.getInt("w");
    }

    public Vec4i copy(){
        return new Vec4i(this.x,this.y,this.z,this.w);
    }

	@Override
	public String toString() {
		return String.format("(%d, %d, %d, %d)",x, y, z, w);
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vec4i vec = (Vec4i) o;

        if (x != vec.x) return false;
        if (y != vec.y) return false;
        if (z != vec.w) return false;
        return w == vec.w;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + z;
        result = 31 * result + w;
        return result;
    }
}
