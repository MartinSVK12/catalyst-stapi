package sunsetsatellite.catalyst.core.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.client.render.Tessellator;
import net.modificationstation.stationapi.api.client.render.model.BakedQuad;
import net.modificationstation.stationapi.impl.client.render.StationTessellatorImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sunsetsatellite.catalyst.core.util.mixin.interfaces.GlobalAlphaSwitch;

@Mixin(StationTessellatorImpl.class)
public class StationTesselatorImplMixin {

    @Shadow
    @Final
    private Tessellator self;

    @Inject(method = "quad", at = @At("HEAD"))
    public void quad(BakedQuad quad, float x, float y, float z, int colour0, int colour1, int colour2, int colour3, float normalX, float normalY, float normalZ, boolean spreadUV, CallbackInfo ci,
                     @Local(argsOnly = true, index = 5) LocalIntRef c0,
                     @Local(argsOnly = true, index = 6) LocalIntRef c1,
                     @Local(argsOnly = true, index = 7) LocalIntRef c2,
                     @Local(argsOnly = true, index = 8) LocalIntRef c3
                     ) {
        int alpha = Math.round(((GlobalAlphaSwitch) self).getGlobalAlpha() * 255);
        int value = (alpha << 24 & colour0) | (0x00FFFFFF & colour0);
        c0.set(value);
        c1.set(value);
        c2.set(value);
        c3.set(value);
    }
}
