package sunsetsatellite.catalyst.multipart.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.client.StationRenderAPI;
import net.modificationstation.stationapi.api.client.render.RendererAccess;
import net.modificationstation.stationapi.api.client.render.model.BakedModel;
import net.modificationstation.stationapi.api.client.render.model.BakedModelRenderer;
import net.modificationstation.stationapi.api.client.render.model.json.*;
import net.modificationstation.stationapi.api.client.texture.SpriteAtlasTexture;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;
import net.modificationstation.stationapi.impl.client.arsenic.renderer.render.ArsenicItemRenderer;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static sunsetsatellite.catalyst.CatalystMultipartClient.getItemModel;

@Mixin(value = ArsenicItemRenderer.class)
public class ArsenicItemRendererMixin {

    @Shadow
    @Final
    private ItemRenderer itemRenderer;

    @Inject(cancellable = true, method = "renderItemOnGui(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/client/texture/TextureManager;Lnet/minecraft/item/ItemStack;IILorg/spongepowered/asm/mixin/injection/callback/CallbackInfo;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;", shift = At.Shift.AFTER))
    public void renderItemOnGui(TextRenderer textRenderer, TextureManager textureManager, ItemStack itemStack, int x, int y, CallbackInfo ci, CallbackInfo ci2) {
        BakedModel model = getItemModel(itemStack);
        if(model == null) return;
        renderGuiItemModel(itemStack, x, y, model);
        ci.cancel();
        ci2.cancel();
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/modificationstation/stationapi/api/client/render/model/BakedModelRenderer;getModel(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;I)Lnet/modificationstation/stationapi/api/client/render/model/BakedModel;"))
    private BakedModel replaceModel(BakedModelRenderer instance, ItemStack stack, World world, LivingEntity livingEntity, int i){
        return getItemModel(stack) != null ? getItemModel(stack) : instance.getModel(stack, world, livingEntity, i);
    }

    @Inject(method = "renderModel",at = @At(value = "INVOKE", target = "Lnet/modificationstation/stationapi/api/client/render/model/BakedModelRenderer;renderItem(Lnet/minecraft/item/ItemStack;Lnet/modificationstation/stationapi/api/client/render/model/json/ModelTransformation$Mode;FLnet/modificationstation/stationapi/api/client/render/model/BakedModel;)V", shift = At.Shift.BEFORE))
    private void renderModel(ItemEntity item, float x, float y, float z, float delta, ItemStack itemStack, float var11, float var12, byte renderedAmount, SpriteAtlasTexture atlas, BakedModel model, CallbackInfo ci, @Local(argsOnly = true) LocalRef<BakedModel> localModel) {
        localModel.set(getItemModel(itemStack));
    }

    @Unique
    private void renderGuiItemModel(ItemStack stack, int x, int y, BakedModel model) {
        StationRenderAPI.getBakedModelManager().getAtlas(Atlases.GAME_ATLAS_TEXTURE).setFilter(false, false);
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, 14.5F);
        GL11.glTranslatef(12.0F, 7.0F, 0.0F);
        GL11.glScalef(1.0F, -1.0F, 1.0F);
        GL11.glScalef(16.0F, 16.0F, 16.0F);

        Tessellator.INSTANCE.startQuads();
        RendererAccess.INSTANCE.getRenderer().bakedModelRenderer().renderItem(stack, ModelTransformation.Mode.GUI, 1.0F, model);
        Tessellator.INSTANCE.draw();

        GL11.glPopMatrix();
        GL11.glEnable(2884);
    }


}
