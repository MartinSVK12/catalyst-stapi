package sunsetsatellite.catalyst.multiblocks;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import sunsetsatellite.catalyst.core.util.BlockInstance;
import sunsetsatellite.catalyst.core.util.Direction;

import java.util.ArrayList;
import java.util.HashMap;

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
		if(block == null) return true;
		ArrayList<BlockInstance> blocks = getBlocks(origin.pos,dir);
		ArrayList<BlockInstance> substitutions = getSubstitutions(origin.pos,dir);
        BlockInstance part = null;
		boolean found = false;
        for (BlockInstance blockInstance : blocks) {
            if (blockInstance.pos.equals(block.pos)) {
				part = blockInstance;
				if(blockInstance.equals(block)) {
					found = true;
				}
				break;
            }
        }
        if(!found){
			boolean foundSub = substitutions.stream().anyMatch((BI) -> BI.pos.equals(block.pos) && BI.equals(block));
			if (!foundSub) {
				return part == null;
			}
		}
		return found || part == null;
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

}
