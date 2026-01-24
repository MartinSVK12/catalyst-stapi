package sunsetsatellite.catalyst.core.mixin;

import net.minecraft.client.render.Tessellator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sunsetsatellite.catalyst.core.util.mixin.interfaces.GlobalAlphaSwitch;

@Mixin(value = Tessellator.class, remap = false)
public abstract class TessellatorMixin implements GlobalAlphaSwitch {

    @Shadow
    public abstract void color(int r, int g, int b, int a);

    @Shadow
    public abstract void color(float r, float g, float b, float a);

    @Unique
    public float globalAlpha = 1.0F;

    @Inject(method = "color(I)V", at = @At("HEAD"), cancellable = true)
    public void fixAlpha(int rgb, CallbackInfo ci) {
        int r = rgb >> 16 & 255;
        int g = rgb >> 8 & 255;
        int b = rgb & 255;
        this.color(r, g, b, (int) (globalAlpha * 255));
        ci.cancel();
    }

    @Inject(method = "color(FFF)V", at = @At("HEAD"), cancellable = true)
    public void fixAlpha(float r, float g, float b, CallbackInfo ci) {
        this.color(r, g, b, globalAlpha);
        ci.cancel();
    }

    @Override
    public float getGlobalAlpha() {
        return globalAlpha;
    }

    @Override
    public void setGlobalAlpha(float alpha) {
        this.globalAlpha = alpha;
    }
}
