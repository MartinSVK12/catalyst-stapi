package sunsetsatellite.catalyst;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.client.StationRenderAPI;
import net.modificationstation.stationapi.api.client.event.render.model.LoadUnbakedModelEvent;
import net.modificationstation.stationapi.api.client.event.texture.TextureRegisterEvent;
import net.modificationstation.stationapi.api.client.gui.screen.GuiHandler;
import net.modificationstation.stationapi.api.client.registry.GuiHandlerRegistry;
import net.modificationstation.stationapi.api.client.render.model.BakedModel;
import net.modificationstation.stationapi.api.client.render.model.BasicBakedModel;
import net.modificationstation.stationapi.api.client.render.model.ModelBakeRotation;
import net.modificationstation.stationapi.api.client.render.model.UnbakedModel;
import net.modificationstation.stationapi.api.client.render.model.json.JsonUnbakedModel;
import net.modificationstation.stationapi.api.client.render.model.json.ModelElement;
import net.modificationstation.stationapi.api.client.render.model.json.ModelElementFace;
import net.modificationstation.stationapi.api.client.render.model.json.ModelOverrideList;
import net.modificationstation.stationapi.api.client.texture.Sprite;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;
import net.modificationstation.stationapi.api.event.block.entity.BlockEntityRegisterEvent;
import net.modificationstation.stationapi.api.event.mod.InitEvent;
import net.modificationstation.stationapi.api.event.recipe.RecipeRegisterEvent;
import net.modificationstation.stationapi.api.event.registry.AfterBlockAndItemRegisterEvent;
import net.modificationstation.stationapi.api.event.registry.BlockRegistryEvent;
import net.modificationstation.stationapi.api.event.registry.GuiHandlerRegistryEvent;
import net.modificationstation.stationapi.api.event.registry.ItemRegistryEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.recipe.CraftingRegistry;
import net.modificationstation.stationapi.api.registry.Registry;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.Namespace;
import net.modificationstation.stationapi.api.util.math.Direction;
import org.apache.logging.log4j.Logger;
import sunsetsatellite.catalyst.core.util.Side;
import sunsetsatellite.catalyst.multipart.api.Multipart;
import sunsetsatellite.catalyst.multipart.api.MultipartType;
import sunsetsatellite.catalyst.multipart.block.CarpenterWorkbenchBlock;
import sunsetsatellite.catalyst.multipart.block.MultipartBlock;
import sunsetsatellite.catalyst.multipart.block.MultipartRender;
import sunsetsatellite.catalyst.multipart.block.entity.CarpenterWorkbenchBlockEntity;
import sunsetsatellite.catalyst.multipart.block.entity.MultipartBlockEntity;
import sunsetsatellite.catalyst.multipart.item.MultipartItem;
import sunsetsatellite.catalyst.multipart.mixin.accessor.JsonUnbakedModelAccessor;
import sunsetsatellite.catalyst.multipart.screen.CarpenterWorkbenchScreen;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CatalystMultipart {

    @Entrypoint.Namespace
    public static Namespace NAMESPACE;

    @Entrypoint.Logger
    public static Logger LOGGER;

    public static final Map<Identifier, UnbakedModel> UNBAKED_MODELS = new HashMap<>();

    @EventListener
    public void onInit(InitEvent event) {
        LOGGER.info("Catalyst: Multipart initialized!");
    }

    @EventListener
    public void loadModelEvent(LoadUnbakedModelEvent event) {
        LOGGER.info(event.identifier);
        UNBAKED_MODELS.put(event.identifier,event.model);
    }

    public static Block multipart;
    public static Item multipartItem;

    public static Block carpenterWorkbench;

    public static List<Block> multipartTypeBlocks = new ArrayList<>();

    public static List<Block> validBlocks = new ArrayList<>();

    @EventListener
    public static void registerBlocks(BlockRegistryEvent event){
        multipart = makeBlock(NAMESPACE.id("multipart"), MultipartBlock.class);
        carpenterWorkbench = new CarpenterWorkbenchBlock(NAMESPACE.id("carpenter_workbench"), Material.STONE).setTranslationKey(NAMESPACE, "carpenterWorkbench").setHardness(3f).setResistance(1);;

        MultipartType.types.keySet().forEach(id -> multipartTypeBlocks.add(makeBlock(Identifier.of(id.toString().replace("block/", "")), MultipartBlock.class)));
    }

    @EventListener
    public static void registerItems(ItemRegistryEvent event){
        multipartItem = new MultipartItem(NAMESPACE.id("multipart_item")).setTranslationKey(NAMESPACE, "multipart_item");
    }

    @EventListener
    public static void registerBlockEntities(BlockEntityRegisterEvent event){
        event.register(MultipartBlockEntity.class, NAMESPACE.id("multipart").toString());
        event.register(CarpenterWorkbenchBlockEntity.class, NAMESPACE.id("carpenter_workbench").toString());
    }

    @EventListener
    public static void afterBlockItemRegistry(AfterBlockAndItemRegisterEvent event){
        for (Block block : Block.BLOCKS) {
            if(block != null && block.isFullCube()){
                validBlocks.add(block);
            }
        }
    }

    @EventListener
    public void registerRecipes(RecipeRegisterEvent event) {
        // Getting the Event type
        RecipeRegisterEvent.Vanilla type = RecipeRegisterEvent.Vanilla.fromType(event.recipeId);

        if (type == RecipeRegisterEvent.Vanilla.CRAFTING_SHAPED) {
            // This will only fire for Shaped Crafting Recipes
            CraftingRegistry.addShapedRecipe(new ItemStack(carpenterWorkbench,1),
            "CCC", "CTC", "CCC",
                    'C', new ItemStack(Block.COBBLESTONE),
                    'T', new ItemStack(Block.CRAFTING_TABLE)
            );
        }
    }

    private static Block makeBlock(Identifier id, Class<? extends Block> clazz){
        try {
            Constructor<? extends Block> c = clazz.getDeclaredConstructor(Identifier.class, Material.class);

            return c.newInstance(id, Material.STONE).setTranslationKey(NAMESPACE, "multipart").setHardness(0.2f).setResistance(1);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
