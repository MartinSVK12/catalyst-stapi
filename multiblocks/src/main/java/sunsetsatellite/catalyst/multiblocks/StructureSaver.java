package sunsetsatellite.catalyst.multiblocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sunsetsatellite.catalyst.Catalyst;
import sunsetsatellite.catalyst.CatalystMultiblocks;
import sunsetsatellite.catalyst.core.util.BlockInstance;
import sunsetsatellite.catalyst.core.util.vector.Vec3i;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

public class StructureSaver {

	public static NbtCompound serialize(@NotNull String name, @NotNull List<BlockInstance> blocks, boolean saveTileEntityData, @Nullable BlockInstance origin) {
		NbtCompound structureData = new NbtCompound();
		NbtCompound blocksTag = new NbtCompound();
		NbtCompound tileEntitiesTag = new NbtCompound();
		NbtCompound substitutionsTag = new NbtCompound();
		NbtCompound originTag = new NbtCompound();

		for (int i = 0; i < blocks.size(); i++) {
			BlockInstance block = blocks.get(i);
			NbtCompound blockTag = new NbtCompound();
			NbtCompound posTag = new NbtCompound();
			boolean isTile = Block.BLOCKS_WITH_ENTITY[block.block.id];
			block.pos.writeToNBT(posTag);
			String namespaceId = Catalyst.getIdFromBlock(block.block);
			blockTag.putString("id", namespaceId);
			blockTag.putInt("meta", block.meta);
			blockTag.putBoolean("tile", isTile);
			blockTag.put("pos", posTag);
			blockTag.put("state", Catalyst.writeBlockState(block.state));
			blocksTag.put(String.valueOf(i), blockTag);
			if (isTile) {
				if (block.tile != null && saveTileEntityData) {
					NbtCompound data = new NbtCompound();
					block.tile.writeNbt(data);
					blockTag.put("data", data);
				}
				tileEntitiesTag.put(String.valueOf(i), blockTag);
			}
		}

		structureData.putString("Name", name);
		structureData.put("Blocks", blocksTag);
		structureData.put("TileEntities", tileEntitiesTag);
		structureData.put("Substitutions", substitutionsTag);
		if(origin != null) {
			NbtCompound posTag = new NbtCompound();
			new Vec3i().writeToNBT(posTag);
			boolean isTile = Block.BLOCKS_WITH_ENTITY[origin.block.id];
			String namespaceId = Catalyst.getIdFromBlock(origin.block);
			originTag.putString("id", namespaceId);
			originTag.putInt("meta", origin.meta);
			originTag.putBoolean("tile", isTile);
			originTag.put("pos", posTag);
			originTag.put("state", Catalyst.writeBlockState(origin.state));
			structureData.put("Origin", originTag);
		}

		return structureData;
	}

	public static UUID save(NbtCompound data, World world) {
		try {
			UUID uuid = UUID.randomUUID();
			File file = world.storage.getWorldPropertiesFile("struct_" + uuid);
			if (file == null) return null;
			NbtIo.writeCompressed(data, Files.newOutputStream(file.toPath()));
			return uuid;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
