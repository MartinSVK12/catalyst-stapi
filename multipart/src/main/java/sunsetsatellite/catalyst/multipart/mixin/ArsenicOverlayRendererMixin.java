package sunsetsatellite.catalyst.multipart.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.Tessellator;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.client.render.model.BakedModel;
import net.modificationstation.stationapi.api.client.render.model.BakedModelRenderer;
import net.modificationstation.stationapi.api.client.render.model.json.ModelTransformation;
import net.modificationstation.stationapi.impl.client.arsenic.renderer.render.ArsenicOverlayRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sunsetsatellite.catalyst.CatalystMultipart;
import sunsetsatellite.catalyst.multipart.block.MultipartRender;

import static org.lwjgl.opengl.GL11.*;

@Mixin(value = ArsenicOverlayRenderer.class,remap = false)
public abstract class ArsenicOverlayRendererMixin {

    @Shadow
    public abstract void renderItem(LivingEntity entity, ItemStack item, ModelTransformation.Mode renderMode);

    @ModifyExpressionValue(method = "renderItem(F)V", at = @At(value = "INVOKE", target = "Lnet/modificationstation/stationapi/api/client/render/item/ItemModels;getModel(Lnet/minecraft/item/ItemStack;)Lnet/modificationstation/stationapi/api/client/render/model/BakedModel;"))
    public BakedModel modifyModel(BakedModel original, @Local ItemStack itemStack){
        Item item = itemStack.getItem();
        if(item instanceof MultipartRender) {
            return CatalystMultipart.getItemModel(itemStack);
        }
        return original;
    }

    @Redirect(method = "renderItem3D", at = @At(value = "INVOKE", target = "Lnet/modificationstation/stationapi/api/client/render/model/BakedModelRenderer;getModel(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;I)Lnet/modificationstation/stationapi/api/client/render/model/BakedModel;"))
    public BakedModel getModel(BakedModelRenderer instance, ItemStack stack, World world, LivingEntity livingEntity, int i) {
        Item item = stack.getItem();
        if(item instanceof MultipartRender) {
            return CatalystMultipart.getItemModel(stack);
        }
        return instance.getModel(stack, world, livingEntity, i);
    }

    @Inject(method = "renderModel(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;)V", at = @At("HEAD"), cancellable = true)
    private void renderModel(LivingEntity entity, ItemStack itemStack, CallbackInfo ci) {
        Item item = itemStack.getItem();
        if(item instanceof MultipartRender) {
            glTranslated(-0.3, 0.8, 0.3);
            glRotatef(20, 1, 0, 0);
            glRotatef(45, 0, 1, 0);
            glScalef(-2, -2, 2);
            Tessellator.INSTANCE.startQuads();
            renderItem(entity, itemStack, ModelTransformation.Mode.THIRD_PERSON_RIGHT_HAND);
            Tessellator.INSTANCE.draw();
            ci.cancel();
        }
    }
}
