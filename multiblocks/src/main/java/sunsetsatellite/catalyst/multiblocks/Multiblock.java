package sunsetsatellite.catalyst.multiblocks;

import com.mojang.datafixers.util.Pair;
import net.glasslauncher.mods.alwaysmoreitems.recipe.multiblock.BlockPatternEntry;
import net.glasslauncher.mods.alwaysmoreitems.recipe.multiblock.MultiBlockRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.Namespace;
import sunsetsatellite.catalyst.core.util.BlockInstance;
import sunsetsatellite.catalyst.core.util.Direction;
import sunsetsatellite.catalyst.core.util.vector.Vec3i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Multiblock extends Structure {

    public static final HashMap<String,Multiblock> multiblocks = new HashMap<>();

    public Multiblock(String modId, Class<?>[] modClasses, String translateKey, NbtCompound data, boolean includeAir) {
        super(modId, modClasses, translateKey, data, includeAir, false);
        this.translateKey = "multiblock."+modId+"."+translateKey;
    }

    public Multiblock(String modId, Class<?>[] modClasses, String translateKey, String filePath, boolean includeAir) {
        super(modId, modClasses, translateKey, filePath, includeAir, false);
        this.translateKey = "multiblock."+modId+"."+translateKey;
    }

    /*public boolean isValidAt(World world, BlockInstance origin, Direction dir){
        ArrayList<BlockInstance> blocks = getBlocks(origin.pos,dir);
        ArrayList<BlockInstance> substitutions = getSubstitutions(origin.pos,dir);
        for (BlockInstance block : blocks) {
            if (!block.exists(world)) {
                boolean foundSub = substitutions.stream().anyMatch((BI) -> BI.pos.equals(block.pos) && BI.exists(world));
                if (!foundSub) {
					//Minecraft.getMinecraft().hudIngame.addChatMessage("Invalid at "+block.pos);
                    return false;
                }
            }
        }
        return true;
    }*/

	public boolean isBlockValidAt(World world, BlockInstance block, BlockInstance origin, Direction dir){
		//fixme: broken
		ArrayList<BlockInstance> blocks = getBlocks(origin.pos,dir);
		ArrayList<BlockInstance> substitutions = getSubstitutions(origin.pos,dir);
        BlockInstance part = null;
		boolean found = false;
        for (BlockInstance blockInstance : blocks) {
            if (blockInstance.pos.equals(block.pos)) {
				if(blockInstance.equals(block)) {
					return true;
				}
				break;
            }
        }
        if(!found){
            return substitutions.stream().anyMatch((BI) -> BI.pos.equals(block.pos) && BI.equals(block));
		}
		return false;
	}

	public boolean isValidAt(World world, BlockInstance origin, Direction dir){
		ArrayList<BlockInstance> blocks = getBlocks(origin.pos,dir);
		ArrayList<BlockInstance> substitutions = getSubstitutions(origin.pos,dir);
		for (BlockInstance block : blocks) {
			if (!block.exists(world)) {
				boolean foundSub = substitutions.stream().anyMatch((BI) -> BI.pos.equals(block.pos) && BI.exists(world));
				if (!foundSub) {
					return false;
				}
			}
		}
		return true;
	}

	public MultiBlockRecipe toRecipe() {

		Map<BlockInstance, Character> pattern = new HashMap<>();
		List<BlockInstance> uniqueBlocks = getUniqueBlocks();

		char c = 33;
		for (BlockInstance block : uniqueBlocks) {
			if(c > 126){
				throw new IllegalStateException("Can't have more than 93 patterns!");
			}
			pattern.put(block, c);
			c++;
		}

		ArrayList<BlockInstance> blocks = getBlocks();

		Pair<Vec3i, Vec3i> sizes = getSizes();
		Vec3i min = getSizes().getFirst();
		Vec3i max = getSizes().getSecond();

		int width = getSize().x;
		int height = getSize().y;
		int depth = getSize().z;

		char[][][] chars = new char[height][depth][width];

		for (int y = 0; y < height; y++) {
			for (int z = 0; z < depth; z++) {
				for (int x = 0; x < width; x++) {
					chars[y][x][z] = ' ';
				}
			}
		}

		for (BlockInstance block : blocks) {
			Map.Entry<BlockInstance, Character> entry = pattern.entrySet().stream().filter((E) -> E.getKey().equalsIgnorePosition(block)).findFirst().orElse(null);
			Character character = entry == null ? null : entry.getValue();

			if (character == null) {
				continue;
			}

			int x = block.pos.x - min.x;
			int y = block.pos.y - min.y;
			int z = block.pos.z - min.z;

			chars[y][x][z] = character;
		}

		String[][] layers = new String[height][depth];

		for (int y = 0; y < height; y++) {
			for (int z = 0; z < depth; z++) {
				layers[y][z] = new String(chars[y][z]);
			}
		}

		ArrayList<BlockPatternEntry> blockPatterns = new ArrayList<>();
		pattern.forEach((k,v)->{
			blockPatterns.add(new BlockPatternEntry(v, k.state, k.meta, new ItemStack(k.block)));
		});
		return new MultiBlockRecipe(Identifier.of(Namespace.of(modId), name), new ArrayList<>(), layers, blockPatterns);
	}

}
