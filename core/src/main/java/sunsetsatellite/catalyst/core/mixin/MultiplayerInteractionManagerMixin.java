package sunsetsatellite.catalyst.core.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MultiplayerInteractionManager;
import net.minecraft.client.network.ClientNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sunsetsatellite.catalyst.Catalyst;
import sunsetsatellite.catalyst.core.util.EnhancedBlockInteraction;
import sunsetsatellite.catalyst.core.util.mp.PlayerEnhancedInteractBlockC2SPacket;

@Mixin(MultiplayerInteractionManager.class)
public abstract class MultiplayerInteractionManagerMixin {

    @Shadow
    private ClientNetworkHandler networkHandler;

    @Shadow
    protected abstract void updateSelectedSlot();

    @Inject(method = "interactBlock", at = @At("HEAD"), cancellable = true, order = 999)
    public void interactBlock(PlayerEntity player, World world, ItemStack stack, int x, int y, int z, int side, CallbackInfoReturnable<Boolean> cir) {
        if (stack != null && stack.getItem() instanceof EnhancedBlockInteraction) {
            this.updateSelectedSlot();
            this.networkHandler.sendPacket(new PlayerEnhancedInteractBlockC2SPacket(x, y, z, side, player.inventory.getSelectedItem(), Catalyst.getClickPosition()));
            cir.setReturnValue(((EnhancedBlockInteraction) stack.getItem()).useOnBlock(stack, player, world, x, y, z, side, Catalyst.getClickPosition()));
        }
    }

    @Inject(method = "attackBlock", at = @At("HEAD")/*, cancellable = true*/)
    public void attackBlock(int x, int y, int z, int dir, CallbackInfo ci) {
        /*if (Minecraft.INSTANCE.world.getBlockState(x, y, z).getBlock() instanceof EnhancedBlockInteraction) {
            this.updateSelectedSlot();
            ci.cancel();
        }*/
    }
}
