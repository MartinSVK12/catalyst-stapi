package sunsetsatellite.catalyst.core.mixin;

import net.minecraft.block.entity.BlockEntity;
import net.modificationstation.stationapi.impl.world.chunk.FlattenedChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sunsetsatellite.catalyst.core.util.BlockEntityInit;

@Mixin(FlattenedChunk.class)
public class FlattenedChunkMixin {

    @Inject(method = "setBlockEntity", at = @At("TAIL"))
    public void setBlockEntity(int relX, int relY, int relZ, BlockEntity arg, CallbackInfo ci) {
        if (arg instanceof BlockEntityInit tile) {
            tile.init(((FlattenedChunk) (Object) this).getBlockState(relX, relY, relZ).getBlock());
        }
    }
}
