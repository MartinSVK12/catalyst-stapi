package sunsetsatellite.catalyst.multiblocks;

import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtString;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.registry.BlockRegistry;
import net.modificationstation.stationapi.api.util.Identifier;
import sunsetsatellite.catalyst.CatalystMultiblocks;
import sunsetsatellite.catalyst.core.util.BlockInstance;
import sunsetsatellite.catalyst.core.util.Direction;
import sunsetsatellite.catalyst.core.util.vector.Vec3i;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class Structure {
    public String modId;
    public Class<?>[] modClasses;
    public String translateKey;
    public String filePath;
    public NbtCompound data;
    public boolean placeAir;
    public boolean replaceBlocks;

    public static HashMap<String,Structure> internalStructures = new HashMap<>();

    public Structure(String modId, Class<?>[] modClasses, String translateKey, NbtCompound data, boolean placeAir, boolean replaceBlocks){
        this.modId = modId;
        this.modClasses = modClasses;
        this.translateKey = "structure."+modId+"."+translateKey+".name";
        this.data = data;
        this.filePath = null;
        this.placeAir = placeAir;
        this.replaceBlocks = replaceBlocks;
		CatalystMultiblocks.LOGGER.info(String.format("Structure '%s' contains %d blocks.",translateKey,this.data.getCompound("Blocks").values().size()));
    }

    public Structure(String modId, Class<?>[] modClasses, String translateKey, String filePath, boolean placeAir, boolean replaceBlocks){
        this.modId = modId;
        this.modClasses = modClasses;
        this.translateKey = "structure."+modId+"."+translateKey+".name";
        this.placeAir = placeAir;
        this.replaceBlocks = replaceBlocks;
        loadFromNBT(filePath);
    }

    public String getTranslatedName(){
        return I18n.getTranslation(this.translateKey);
    }

    public String getFullFilePath(){
        if(filePath != null){
            return "/assets/"+modId+"/structures/"+filePath+".nbt";
        } else {
            return null;
        }
    }

    public boolean placeStructure(World world, int originX, int originY, int originZ){
        Vec3i origin = new Vec3i(originX,originY,originZ);
        ArrayList<BlockInstance> blocks = getBlocks(origin);
        blocks.add(getOrigin(origin));
        for (BlockInstance block : blocks) {
            if (!replaceBlocks && world.getBlockId(block.pos.x, block.pos.y, block.pos.z) != 0) {
                return false;
            }
        }
        for (BlockInstance block : blocks) {
            world.setBlock(block.pos.x,block.pos.y,block.pos.z,block.block.id,block.meta);
        }
        return true;
    }

    public boolean placeStructure(World world, int originX, int originY, int originZ, String direction){
        Direction dir = Direction.getFromName(direction);
        if(dir == null) return false;
        Vec3i origin = new Vec3i(originX,originY,originZ);
        ArrayList<BlockInstance> blocks = getBlocks(origin,dir);
        blocks.add(getOrigin(origin));
        for (BlockInstance block : blocks) {
            if (!replaceBlocks && world.getBlockId(block.pos.x, block.pos.y, block.pos.z) != 0) {
                return false;
            }
        }
        for (BlockInstance block : blocks) {
            world.setBlock(block.pos.x,block.pos.y,block.pos.z,block.block.id,block.meta == -1 ? 0 : block.meta);
        }
        return true;
    }

    public BlockInstance getOrigin(){
        NbtCompound blockTag = data.getCompound("Origin");
        int meta = blockTag.getInt("meta");
        Block block = getBlock(blockTag);
        return new BlockInstance(block, new Vec3i(),meta,null);
    }

    public BlockInstance getOrigin(Vec3i origin){
        NbtCompound blockTag = data.getCompound("Origin");
        int meta = blockTag.getInt("meta");
        Block block = getBlock(blockTag);
        return new BlockInstance(block, origin,meta,null);
    }

	public BlockInstance getOrigin(Vec3i origin, Direction dir){
		NbtCompound blockTag = data.getCompound("Origin");
		int meta = blockTag.getInt("meta");
		if (meta != -1 && meta != 0 && meta != 1) {
			if (dir.shiftAxis() == Direction.Z_NEG) {
				meta = Direction.getDirectionFromSide(meta).getOpposite().getSideNumber();
			} else if (dir.shiftAxis() == Direction.X_NEG || dir.shiftAxis() == Direction.X_POS) {
				Direction blockDir = Direction.getDirectionFromSide(meta);
				blockDir = blockDir != Direction.X_NEG && blockDir != Direction.X_POS ? blockDir.rotate(1) : blockDir.rotate(1).getOpposite();
				meta = dir.shiftAxis() == Direction.X_NEG ? blockDir.getSideNumber() : blockDir.getOpposite().getSideNumber();
			}
		}
        Block block = getBlock(blockTag);
		return new BlockInstance(block, origin,meta,null);
	}

    public BlockInstance getOrigin(World world, Vec3i origin){
        NbtCompound blockTag = data.getCompound("Origin");
        Vec3i pos = new Vec3i(blockTag.getCompound("pos"));
        int meta = blockTag.getInt("meta");
        Block block = getBlock(blockTag);
        return new BlockInstance(block,pos,meta,world.getBlockEntity(pos.x, pos.y, pos.z));
    }

    public ArrayList<BlockInstance> getTileEntities(){
        ArrayList<BlockInstance> tiles = new ArrayList<>();
        for (Object tag : data.getCompound("TileEntities").values()) {
            NbtCompound tileEntity = (NbtCompound) tag;
            Vec3i pos = new Vec3i(tileEntity.getCompound("pos"));
            int meta = tileEntity.getInt("meta");
            Block block = getBlock(tileEntity);
            BlockInstance blockInstance = new BlockInstance(block,pos,meta,null);
            tiles.add(blockInstance);
        }
        return tiles;
    }

    public ArrayList<BlockInstance> getTileEntities(Vec3i origin){
        ArrayList<BlockInstance> tiles = new ArrayList<>();
        for (Object tag : data.getCompound("TileEntities").values()) {
            NbtCompound tileEntity = (NbtCompound) tag;
            Vec3i pos = new Vec3i(tileEntity.getCompound("pos")).add(origin);
            int meta = tileEntity.getInt("meta");
            Block block = getBlock(tileEntity);
            BlockInstance blockInstance = new BlockInstance(block,pos,meta,null);
            tiles.add(blockInstance);
        }
        return tiles;
    }

    public ArrayList<BlockInstance> getTileEntities(World world, Vec3i origin){
        ArrayList<BlockInstance> tiles = new ArrayList<>();
        for (Object tag : data.getCompound("TileEntities").values()) {
            NbtCompound tileEntity = (NbtCompound) tag;
            Vec3i pos = new Vec3i(tileEntity.getCompound("pos")).add(origin);
            int meta = tileEntity.getInt("meta");
            Block block = getBlock(tileEntity);
            BlockInstance blockInstance = new BlockInstance(block,pos,meta,world.getBlockEntity(pos.x, pos.y, pos.z));
            tiles.add(blockInstance);
        }
        return tiles;
    }

    public ArrayList<BlockInstance> getTileEntities(World world, Vec3i origin, Direction dir){
		ArrayList<BlockInstance> tiles = new ArrayList<>();
		for (Object tag : data.getCompound("TileEntities").values()) {
			NbtCompound blockTag = (NbtCompound) tag;
			Vec3i pos = new Vec3i(blockTag.getCompound("pos")).rotate(origin,dir);
			int meta = blockTag.getInt("meta");
			if (meta != -1 && meta != 0 && meta != 1) {
				if (dir.shiftAxis() == Direction.Z_NEG) {
					meta = Direction.getDirectionFromSide(meta).getOpposite().getSideNumber();
				} else if (dir.shiftAxis() == Direction.X_NEG || dir.shiftAxis() == Direction.X_POS) {
					Direction blockDir = Direction.getDirectionFromSide(meta);
					blockDir = blockDir != Direction.X_NEG && blockDir != Direction.X_POS ? blockDir.rotate(1) : blockDir.rotate(1).getOpposite();
					meta = dir.shiftAxis() == Direction.X_NEG ? blockDir.getSideNumber() : blockDir.getOpposite().getSideNumber();
				}
			}
            Block block = getBlock(blockTag);
			BlockInstance blockInstance = new BlockInstance(block,pos,meta,world.getBlockEntity(pos.x, pos.y, pos.z));
			tiles.add(blockInstance);
		}
		return tiles;
    }

	public Pair<Vec3i,Vec3i> getSizes(){
		ArrayList<BlockInstance> blocks = getBlocks();
		int xMin = 1;
		int yMin = 1;
		int zMin = 1;

		int xMax = 0;
		int yMax = 0;
		int zMax = 0;

		for (BlockInstance block : blocks) {
			if (block.pos.x < xMin) xMin = block.pos.x;
			if (block.pos.y < yMin) yMin = block.pos.y;
			if (block.pos.z < zMin) zMin = block.pos.z;

			if (block.pos.x > xMax) xMax = block.pos.x;
			if (block.pos.y > yMax) yMax = block.pos.y;
			if (block.pos.z > zMax) zMax = block.pos.z;
		}

		return Pair.of(new Vec3i(xMin,yMin,zMin),new Vec3i(xMax,yMax,zMax));
	}

	public Vec3i getSize(){
		Pair<Vec3i,Vec3i> sizes = getSizes();
		sizes.getFirst().set(Math.abs(sizes.getFirst().x),Math.abs(sizes.getFirst().y),Math.abs(sizes.getFirst().z));
		return sizes.getFirst().add(sizes.getSecond()).add(1);
	}

    public ArrayList<BlockInstance> getBlocks(){
        ArrayList<BlockInstance> tiles = new ArrayList<>();
        for (Object tag : data.getCompound("Blocks").values()) {
            NbtCompound blockTag = (NbtCompound) tag;
            Vec3i pos = new Vec3i(blockTag.getCompound("pos"));
            int meta = blockTag.getInt("meta");
            Block block = getBlock(blockTag);
            BlockInstance blockInstance = new BlockInstance(block,pos,meta,null);
            tiles.add(blockInstance);
        }
        return tiles;
    }

    public ArrayList<BlockInstance> getBlocks(Vec3i origin){
        ArrayList<BlockInstance> tiles = new ArrayList<>();
        for (Object tag : data.getCompound("Blocks").values()) {
            NbtCompound blockTag = (NbtCompound) tag;
            Vec3i pos = new Vec3i(blockTag.getCompound("pos")).add(origin);
            int meta = blockTag.getInt("meta");
            Block block = getBlock(blockTag);
            BlockInstance blockInstance = new BlockInstance(block,pos,meta,null);
            tiles.add(blockInstance);
        }
        return tiles;
    }

    public ArrayList<BlockInstance> getBlocks(Vec3i origin, Direction dir){
        ArrayList<BlockInstance> tiles = new ArrayList<>();
        for (Object tag : data.getCompound("Blocks").values()) {
            NbtCompound blockTag = (NbtCompound) tag;
            Vec3i pos = new Vec3i(blockTag.getCompound("pos")).rotate(origin,dir);
            int meta = blockTag.getInt("meta");
			if (meta != -1 && meta != 0 && meta != 1) {
				if (dir.shiftAxis() == Direction.Z_NEG) {
					meta = Direction.getDirectionFromSide(meta).getOpposite().getSideNumber();
				} else if (dir.shiftAxis() == Direction.X_NEG || dir.shiftAxis() == Direction.X_POS) {
					Direction blockDir = Direction.getDirectionFromSide(meta);
					blockDir = blockDir != Direction.X_NEG && blockDir != Direction.X_POS ? blockDir.rotate(1) : blockDir.rotate(1).getOpposite();
					meta = dir.shiftAxis() == Direction.X_NEG ? blockDir.getSideNumber() : blockDir.getOpposite().getSideNumber();
				}
			}
            Block block = getBlock(blockTag);
            BlockInstance blockInstance = new BlockInstance(block,pos,meta,null);
            tiles.add(blockInstance);
        }
        return tiles;
    }

    public ArrayList<BlockInstance> getSubstitutions(){
        ArrayList<BlockInstance> tiles = new ArrayList<>();
        for (Object tag : data.getCompound("Substitutions").values()) {
            NbtCompound sub = (NbtCompound) tag;
            Vec3i pos = new Vec3i(sub.getCompound("pos"));
            int meta = sub.getInt("meta");
            Block block = getBlock(sub);
            BlockInstance blockInstance = new BlockInstance(block,pos,meta,null);
            tiles.add(blockInstance);
        }
        return tiles;
    }

    public ArrayList<BlockInstance> getSubstitutions(Vec3i origin){
        ArrayList<BlockInstance> tiles = new ArrayList<>();
        for (Object tag : data.getCompound("Substitutions").values()) {
            NbtCompound sub = (NbtCompound) tag;
            Vec3i pos = new Vec3i(sub.getCompound("pos")).add(origin);
            int meta = sub.getInt("meta");
            Block block = getBlock(sub);
            BlockInstance blockInstance = new BlockInstance(block,pos,meta,null);
            tiles.add(blockInstance);
        }
        return tiles;
    }

    public ArrayList<BlockInstance> getSubstitutions(Vec3i origin, Direction dir){
        ArrayList<BlockInstance> tiles = new ArrayList<>();
        for (Object tag : data.getCompound("Substitutions").values()) {
            NbtCompound tileEntity = (NbtCompound) tag;
            Vec3i pos = new Vec3i(tileEntity.getCompound("pos")).rotate(origin, dir);
			int meta = tileEntity.getInt("meta");
			if (meta != -1 && meta != 0 && meta != 1) {
				if (dir.shiftAxis() == Direction.Z_NEG) {
					meta = Direction.getDirectionFromSide(meta).getOpposite().getSideNumber();
				} else if (dir.shiftAxis() == Direction.X_NEG || dir.shiftAxis() == Direction.X_POS) {
					Direction blockDir = Direction.getDirectionFromSide(meta);
					blockDir = blockDir != Direction.X_NEG && blockDir != Direction.X_POS ? blockDir.rotate(1) : blockDir.rotate(1).getOpposite();
					meta = dir.shiftAxis() == Direction.X_NEG ? blockDir.getSideNumber() : blockDir.getOpposite().getSideNumber();
				}
			}
            Block block = getBlock(tileEntity);
            BlockInstance blockInstance = new BlockInstance(block,pos,meta,null);
            tiles.add(blockInstance);
        }
        return tiles;
    }

    public static Block getBlock(NbtCompound block){
        return BlockRegistry.INSTANCE.get(Identifier.of(block.getString("id")));
    }

    protected void loadFromNBT(String name) {
        try (InputStream resource = this.getClass().getResourceAsStream("/assets/" + modId + "/structures/" + name + ".nbt")) {
            if (resource != null) {
                this.data = NbtIo.readCompressed(resource);
                CatalystMultiblocks.LOGGER.info(String.format("Structure '%s' contains %d blocks.",name,this.data.getCompound("Blocks").values().size()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*public String getBlockFieldName(Block item){
        try{
            ArrayList<Field> fields = new ArrayList<>(Arrays.asList(Blocks.class.getDeclaredFields()));
            for (Field field : fields) {
                if(field.getType().isAssignableFrom(Block.class) && Modifier.isStatic(field.getModifiers())){
                    field.setAccessible(true);
                    Block fieldItem = (Block) field.get(null);
                    if(fieldItem.equals(item)){
                        return "Block."+field.getName();
                    }
                }
            }
            for (Class<?> aClass : modClasses) {
                fields = new ArrayList<>(Arrays.asList(aClass.getDeclaredFields()));
                for (Field field : fields) {
                    if (field.getType().isAssignableFrom(Block.class) && Modifier.isStatic(field.getModifiers())) {
                        field.setAccessible(true);
                        Block fieldItem = (Block) field.get(null);
                        if (fieldItem.equals(item)) {
                            return aClass.getName()+"." + field.getName();
                        }
                    }
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return "";
    }*/

}
