package sunsetsatellite.catalyst.multipart.api;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.modificationstation.stationapi.api.registry.BlockRegistry;
import net.modificationstation.stationapi.api.util.Identifier;
import sunsetsatellite.catalyst.Catalyst;
import sunsetsatellite.catalyst.CatalystMultipart;
import sunsetsatellite.catalyst.core.util.Side;

import java.util.HashMap;

public class Multipart {

	public final MultipartType type;
	public final HashMap<Side, Identifier> textures = (HashMap<Side, Identifier>) Catalyst.mapOf(Side.values(), Catalyst.arrayFill(new Identifier[Side.values().length], Identifier.of("minecraft:block/stone")));
	public final Block block;
	public final int meta;
	public final Side side; //side of the texture that will be used, not the actual side this multipart is attached to
	public final boolean specifiedSideOnly;

	public Multipart(MultipartType type, Block block, Identifier texture, Side side, int meta) {
		this.type = type;
		this.block = block;
		this.meta = meta;
		this.side = side;
		this.specifiedSideOnly = true;
		for (Side _side : Side.values()) {
            this.textures.put(_side, texture);
		}
	}

	public Multipart(MultipartType type, Block block, Identifier[] texture, int meta) {
		this.type = type;
		this.block = block;
		this.meta = meta;
		this.side = null;
		this.specifiedSideOnly = false;
        Side[] values = Side.values();
        for (int i = 0; i < values.length; i++) {
            Side _side = values[i];
            this.textures.put(_side, texture[i]);
        }
	}

	public Multipart(NbtCompound partNbt){
		boolean sideOnly;
		this.type = MultipartType.types.get(Identifier.of(partNbt.getString("Type")));
		this.block = BlockRegistry.INSTANCE.get(Identifier.of(partNbt.getString("Block")));
		this.meta = partNbt.getInt("Meta");
		if(partNbt.contains("Side")){
			this.side = Side.values()[(partNbt.getInt("Side"))];
			sideOnly = true;
		} else {
			this.side = null;
			sideOnly = false;
		}
		this.specifiedSideOnly = sideOnly;
		if(specifiedSideOnly) {
			for (Side _side : Side.values()) {
                Identifier id = Identifier.of(partNbt.getString("Texture"));
				this.textures.put(_side,id);
			}
		} else {
			for (Side _side : Side.values()) {
                Identifier id = Identifier.of(partNbt.getString("Texture_"+_side.name().toLowerCase()));
				this.textures.put(_side,id);
			}
		}
	}

	public void writeToNbt(NbtCompound partNbt) {
		partNbt.putString("Type", type.name.toString());
		partNbt.putInt("Meta", meta);
		partNbt.putString("Block", Catalyst.getIdFromBlock(block));
		if (specifiedSideOnly) {
			partNbt.putInt("Side", side.ordinal());
            partNbt.putString("Texture", textures.get(Side.NORTH).toString());
		} else {
            textures.forEach((side, texture) -> partNbt.putString("Texture_"+side.name().toLowerCase(), texture.toString()));
        }
	}

    public ItemStack getStack(int amount){
        ItemStack stack = new ItemStack(CatalystMultipart.multipartItem, amount, 0);
        NbtCompound multipartTag = new NbtCompound();
        writeToNbt(multipartTag);
        stack.getStationNbt().put("Multipart",multipartTag);
        return stack;
    }
}
