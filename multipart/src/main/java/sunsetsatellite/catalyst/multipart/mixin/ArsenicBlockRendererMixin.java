package sunsetsatellite.catalyst.multipart.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.client.render.RendererAccess;
import net.modificationstation.stationapi.api.client.render.model.*;
import net.modificationstation.stationapi.api.client.render.model.json.JsonUnbakedModel;
import net.modificationstation.stationapi.api.client.render.model.json.ModelElement;
import net.modificationstation.stationapi.api.client.render.model.json.ModelElementFace;
import net.modificationstation.stationapi.api.client.render.model.json.ModelOverrideList;
import net.modificationstation.stationapi.api.client.texture.Sprite;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.MutableBlockPos;
import net.modificationstation.stationapi.impl.client.arsenic.renderer.render.ArsenicBlockRenderer;
import net.modificationstation.stationapi.mixin.arsenic.client.BlockRenderManagerAccessor;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sunsetsatellite.catalyst.CatalystMultipart;
import sunsetsatellite.catalyst.core.util.Direction;
import sunsetsatellite.catalyst.core.util.Side;
import sunsetsatellite.catalyst.multipart.api.Multipart;
import sunsetsatellite.catalyst.multipart.api.SupportsMultiparts;
import sunsetsatellite.catalyst.multipart.block.MultipartRender;
import sunsetsatellite.catalyst.multipart.mixin.accessor.JsonUnbakedModelAccessor;

import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

@Mixin(value = ArsenicBlockRenderer.class, remap = false)
public class ArsenicBlockRendererMixin {

    @Shadow
    @Final
    private BlockRenderManagerAccessor blockRendererAccessor;

    @Shadow
    @Final
    private MutableBlockPos blockPos;

    @Shadow
    @Final
    private Random random;

    @Inject(method = "renderWorld", cancellable = true, at = @At(value = "INVOKE", target = "Lnet/modificationstation/stationapi/api/client/render/model/BakedModel;isBuiltin()Z", shift = At.Shift.BEFORE))
    public void renderWorld(Block block, int x, int y, int z, CallbackInfoReturnable<Boolean> cir, CallbackInfo ci, @Local BakedModel model, @Local BlockState state) {
        if(block instanceof MultipartRender){
            BlockEntity blockEntity = this.blockRendererAccessor.getBlockView().getBlockEntity(x, y, z);
            if(blockEntity instanceof SupportsMultiparts multipartEntity) {
                for (Direction dir : Direction.values()) {
                    Side side = Side.values()[dir.getSideNumber()];
                    Multipart multipart = multipartEntity.getParts().get(dir);
                    if (multipart == null) continue;
                    JsonUnbakedModel unbakedModel = (JsonUnbakedModel) CatalystMultipart.UNBAKED_MODELS.get(multipart.type.name);
                    BasicBakedModel.Builder builder = new BasicBakedModel.Builder(unbakedModel, ModelOverrideList.EMPTY, true);
                    builder.setParticle(Atlases.getTerrain().getTexture(multipart.textures.get(Side.NORTH)).getSprite());
                    for (ModelElement modelElement : unbakedModel.getElements()) {
                        String textureId = "missing";
                        for (ModelElementFace elementFace : modelElement.faces.values()) {
                            textureId = elementFace.textureId.replace("#","");
                            break;
                        }
                        if(textureId.equalsIgnoreCase(side.name())){
                            for (net.modificationstation.stationapi.api.util.math.Direction direction : modelElement.faces.keySet()) {
                                Side faceSide = Side.values()[direction.getId()];
                                ModelElementFace modelElementFace = modelElement.faces.get(direction);
                                Sprite sprite = Atlases.getTerrain().getTexture(multipart.textures.get(faceSide)).getSprite();
                                Identifier id = Identifier.of("catalyst-multipart:multipart");
                                if (modelElementFace.cullFace == null)
                                    builder.addQuad(JsonUnbakedModelAccessor.callCreateQuad(modelElement, modelElementFace, sprite, direction, ModelBakeRotation.X0_Y0, id));
                                else
                                    builder.addQuad(net.modificationstation.stationapi.api.util.math.Direction.transform(ModelBakeRotation.X0_Y0.getRotation().getMatrix(), modelElementFace.cullFace), JsonUnbakedModelAccessor.callCreateQuad(modelElement, modelElementFace, sprite, direction, ModelBakeRotation.X0_Y0, id));
                            }
                        }
                    }
                    cir.setReturnValue(RendererAccess.INSTANCE.getRenderer().bakedModelRenderer().render(
                            this.blockRendererAccessor.getBlockView(),
                            builder.build(),
                            state,
                            this.blockPos.set(x, y, z),
                            !this.blockRendererAccessor.getSkipFaceCulling(),
                            this.random,
                            state.getRenderingSeed(this.blockPos))
                    );
                }
                ci.cancel();
            }
        }
    }
}
