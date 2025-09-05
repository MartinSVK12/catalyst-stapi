package sunsetsatellite.catalyst.core.mixin;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sunsetsatellite.catalyst.core.util.ExtendedScreenDraw;

@Mixin(HandledScreen.class)
public class HandledScreenMixin implements ExtendedScreenDraw {

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;drawForeground()V"))
    public void hook(int mouseX, int mouseY, float delta, CallbackInfo ci){
        drawAfterSlotAndButtonRendering(mouseX, mouseY, delta);
    }

    @Override
    public void drawAfterSlotAndButtonRendering(int mouseX, int mouseY, float delta) {

    }
}
