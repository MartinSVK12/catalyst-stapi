package sunsetsatellite.catalyst.multiblocks;

import com.google.gson.*;
import net.glasslauncher.mods.alwaysmoreitems.recipe.multiblock.BlockPatternEntry;
import net.glasslauncher.mods.alwaysmoreitems.recipe.multiblock.MultiBlockRecipe;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.modificationstation.stationapi.api.registry.BlockRegistry;
import net.modificationstation.stationapi.api.util.Identifier;
import sunsetsatellite.catalyst.Catalyst;
import sunsetsatellite.catalyst.core.util.StringUtils;
import sunsetsatellite.catalyst.core.util.vector.Vec3i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StructureBuilder {

	private final char originSymbol;
	private final Block originBlock;
	private final int originMeta;
	private final List<String[]> layers = new ArrayList<>();
	private final Map<Character, ItemStack> symbolMap = new HashMap<>();

	public StructureBuilder(char originSymbol, Block originBlock, int originMeta) {
		this.originSymbol = originSymbol;
		this.originBlock = originBlock;
		this.originMeta = originMeta;
	}

	public StructureBuilder addLayer(String... layer){
		layers.add(layer);
		return this;
	}

	public StructureBuilder mapSymbol(char symbol, ItemStack stack){
		symbolMap.put(symbol, stack);
		return this;
	}

    public StructureBuilder loadJson(String path){
        String jsonString = StringUtils.readInputString(StructureBuilder.class.getResourceAsStream(path));
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        JsonArray jsonArray = JsonParser.parseString(jsonString).getAsJsonArray();
        for(JsonElement element : jsonArray){
            JsonArray layer = element.getAsJsonArray();
            layers.add(gson.fromJson(layer, String[].class));
        }
        return this;
    }

    public MultiBlockRecipe toRecipe(Identifier name, List<Object> description){
        List<BlockPatternEntry> blockPatterns = new ArrayList<>();
        symbolMap.forEach((K,V)->{
            blockPatterns.add(new BlockPatternEntry(K, Block.BLOCKS[V.itemId].getDefaultState(), V.getDamage(), V));
        });
        return new MultiBlockRecipe(name, description, layers, blockPatterns);
    }

	public NbtCompound build(){

		int x = 0;
		int y = 0;
		int z = 0;

		Vec3i originPos = null;
        NbtCompound structureData = new NbtCompound();
        NbtCompound originTag = new NbtCompound();
        NbtCompound blocksTag = new NbtCompound();
        NbtCompound tileEntitiesTag = new NbtCompound();
        NbtCompound substitutionsTag = new NbtCompound();

		top:
		for (String[] layer : layers) {
			z = 0;
			for (String section : layer) {
				x = 0;
				for (char symbol : section.toCharArray()) {
					if(symbol == originSymbol){
						originPos = new Vec3i(x,y,z);
						break top;
					}
					x++;
				}
				z++;
			}
			y++;
		}

		if(originPos == null){
			throw new IllegalStateException("No origin found in structure schematic!");
		}

		originTag.putString("id", Catalyst.getIdFromBlock(originBlock));
		originTag.putInt("meta",originMeta);
		originTag.putBoolean("tile", originBlock instanceof BlockWithEntity);
        NbtCompound originPosTag = new NbtCompound();
		new Vec3i().writeToNBT(originPosTag);
		originTag.put("pos",originPosTag);

		x = y = z = 0;

		int i = 0;

		for (String[] layer : layers) {
			z = 0;
			for (String section : layer) {
				x = 0;
				for (char symbol : section.toCharArray()) {
					if(symbol == ' '){
						x++;
						continue;
					}
					if(symbol == originSymbol){
						x++;
						continue;
					}
					ItemStack mappedStack = symbolMap.get(symbol);
					if(mappedStack == null){
						throw new NullPointerException("Unmapped symbol: '"+symbol+"'!");
					}
					NbtCompound blockTag = new NbtCompound();
					NbtCompound posTag = new NbtCompound();
					boolean isTile = ((BlockItem) mappedStack.getItem()).getBlock() instanceof BlockWithEntity;
					new Vec3i(x,y,z).subtract(originPos).writeToNBT(posTag);
					blockTag.putString("id", Catalyst.getIdFromStack(mappedStack));
					blockTag.putInt("meta", mappedStack.getDamage());
					blockTag.putBoolean("tile", isTile);
					blockTag.put("pos", posTag);
					blocksTag.put(String.valueOf(i), blockTag);
					if(isTile){
						tileEntitiesTag.put(String.valueOf(i), blockTag);
					}
					i++;
					x++;
				}
				z++;
			}
			y++;
		}

		structureData.put("Origin", originTag);
		structureData.put("Blocks", blocksTag);
		structureData.put("TileEntities", tileEntitiesTag);
		structureData.put("Substitutions", substitutionsTag);

		return structureData;
	}

}
