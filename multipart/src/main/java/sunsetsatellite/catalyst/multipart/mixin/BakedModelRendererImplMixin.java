package sunsetsatellite.catalyst.multipart.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.client.render.model.BakedModel;
import net.modificationstation.stationapi.api.client.render.model.json.ModelTransformation;
import net.modificationstation.stationapi.impl.client.arsenic.renderer.render.BakedModelRendererImpl;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import sunsetsatellite.catalyst.CatalystMultipart;
import sunsetsatellite.catalyst.multipart.block.MultipartRender;

@Mixin(value = BakedModelRendererImpl.class,remap = false)
public class BakedModelRendererImplMixin {

    @Redirect(method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/modificationstation/stationapi/api/client/render/model/json/ModelTransformation$Mode;Lnet/minecraft/world/World;FI)V",
        at = @At(value = "INVOKE", target = "Lnet/modificationstation/stationapi/impl/client/arsenic/renderer/render/BakedModelRendererImpl;getModel(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;I)Lnet/modificationstation/stationapi/api/client/render/model/BakedModel;")
    )
    private BakedModel getModel(BakedModelRendererImpl instance, ItemStack stack, World world, LivingEntity entity, int seed){
        Item item = stack.getItem();
        if(item instanceof MultipartRender) {
            return CatalystMultipart.getItemModel(stack);
        }
        return instance.getModel(stack, world, entity, seed);
    }

}
