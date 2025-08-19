package sunsetsatellite.catalyst;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.client.StationRenderAPI;
import net.modificationstation.stationapi.api.client.event.texture.TextureRegisterEvent;
import net.modificationstation.stationapi.api.client.gui.screen.GuiHandler;
import net.modificationstation.stationapi.api.client.render.model.BakedModel;
import net.modificationstation.stationapi.api.client.render.model.BasicBakedModel;
import net.modificationstation.stationapi.api.client.render.model.ModelBakeRotation;
import net.modificationstation.stationapi.api.client.render.model.json.JsonUnbakedModel;
import net.modificationstation.stationapi.api.client.render.model.json.ModelElement;
import net.modificationstation.stationapi.api.client.render.model.json.ModelElementFace;
import net.modificationstation.stationapi.api.client.render.model.json.ModelOverrideList;
import net.modificationstation.stationapi.api.client.texture.Sprite;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;
import net.modificationstation.stationapi.api.event.registry.GuiHandlerRegistryEvent;
import net.modificationstation.stationapi.api.registry.Registry;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;
import sunsetsatellite.catalyst.core.util.Side;
import sunsetsatellite.catalyst.multipart.api.Multipart;
import sunsetsatellite.catalyst.multipart.block.MultipartRender;
import sunsetsatellite.catalyst.multipart.block.entity.CarpenterWorkbenchBlockEntity;
import sunsetsatellite.catalyst.multipart.mixin.accessor.JsonUnbakedModelAccessor;
import sunsetsatellite.catalyst.multipart.screen.CarpenterWorkbenchScreen;

import java.util.HashMap;

import static sunsetsatellite.catalyst.CatalystMultipart.NAMESPACE;
import static sunsetsatellite.catalyst.CatalystMultipart.multipartItem;

@Environment(EnvType.CLIENT)
public class CatalystMultipartClient {

    public static int carpenterWorkbenchFront;
    public static int carpenterWorkbenchTop;
    public static int carpenterWorkbenchBottom;
    public static int carpenterWorkbenchSide;

    @EventListener
    public void registerScreenHandlers(GuiHandlerRegistryEvent event) {
        Registry.register(event.registry, NAMESPACE.id("open_carpenter_workbench"), new GuiHandler((GuiHandler.ScreenFactoryNoMessage) (p, i)-> new CarpenterWorkbenchScreen(p.inventory, ((CarpenterWorkbenchBlockEntity) i)), CarpenterWorkbenchBlockEntity::new));
    }

    public static BakedModel getItemModel(ItemStack itemStack) {
        Item item = itemStack.getItem();
        if(item instanceof MultipartRender multipartRender) {
            StationRenderAPI.getBakedModelManager().getAtlas(Atlases.GAME_ATLAS_TEXTURE).bindTexture();
            Multipart multipart = multipartRender.getMultipart(itemStack);
            if (multipart == null) return null;
            JsonUnbakedModel unbakedModel = (JsonUnbakedModel) CatalystMultipart.UNBAKED_MODELS.get(multipart.type.name);
            BasicBakedModel.Builder builder = new BasicBakedModel.Builder(unbakedModel, ModelOverrideList.EMPTY, true);
            HashMap<Side, Identifier> textures = (HashMap<Side, Identifier>) Catalyst.mapOf(Side.values(), Catalyst.arrayFill(new Identifier[Side.values().length], Identifier.of("minecraft:block/stone")));
            for (Side side : Side.values()) {
                textures.put(side,Atlases.getTerrain().getTexture(multipart.block.getTexture(side.ordinal(), multipart.meta)).getId());
            }
            builder.setParticle(Atlases.getTerrain().getTexture(textures.get(Side.NORTH)).getSprite());
            for (ModelElement modelElement : unbakedModel.getElements()) {
                String textureId = "missing";
                for (ModelElementFace elementFace : modelElement.faces.values()) {
                    textureId = elementFace.textureId.replace("#", "");
                    break;
                }
                if (textureId.equalsIgnoreCase(Side.EAST.name())) {
                    for (Direction direction : modelElement.faces.keySet()) {
                        Side faceSide = Side.values()[direction.getId()];
                        ModelElementFace modelElementFace = modelElement.faces.get(direction);
                        Sprite sprite = Atlases.getTerrain().getTexture(textures.get(faceSide)).getSprite();
                        Identifier id = Identifier.of("catalyst-multipart:multipart");
                        if (modelElementFace.cullFace == null)
                            builder.addQuad(JsonUnbakedModelAccessor.callCreateQuad(modelElement, modelElementFace, sprite, direction, ModelBakeRotation.X0_Y0, id));
                        else
                            builder.addQuad(Direction.transform(ModelBakeRotation.X0_Y0.getRotation().getMatrix(), modelElementFace.cullFace), JsonUnbakedModelAccessor.callCreateQuad(modelElement, modelElementFace, sprite, direction, ModelBakeRotation.X0_Y0, id));
                    }
                }
            }
            return builder.build();
        }
        return null;
    }

    @EventListener
    public static void registerTextures(TextureRegisterEvent event){
        multipartItem.setTextureId(0);

        carpenterWorkbenchBottom = Atlases.getTerrain().addTexture(NAMESPACE.id("block/carpenter_workbench_bottom")).index;
        carpenterWorkbenchFront = Atlases.getTerrain().addTexture(NAMESPACE.id("block/carpenter_workbench_front")).index;
        carpenterWorkbenchSide = Atlases.getTerrain().addTexture(NAMESPACE.id("block/carpenter_workbench_side")).index;
        carpenterWorkbenchTop = Atlases.getTerrain().addTexture(NAMESPACE.id("block/carpenter_workbench_top")).index;
    }
}
