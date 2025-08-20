package sunsetsatellite.catalyst.core.mixin;

import net.minecraft.block.Block;
import net.minecraft.client.InteractionManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MultiplayerInteractionManager;
import net.minecraft.client.network.ClientNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
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
import sunsetsatellite.catalyst.core.util.mp.PlayerEnhancedActionC2SPacket;
import sunsetsatellite.catalyst.core.util.mp.PlayerEnhancedInteractBlockC2SPacket;

@Mixin(MultiplayerInteractionManager.class)
public abstract class MultiplayerInteractionManagerMixin extends InteractionManager {

    @Shadow
    private ClientNetworkHandler networkHandler;

    private MultiplayerInteractionManagerMixin(Minecraft minecraft) {
        super(minecraft);
    }

    @Shadow
    protected abstract void updateSelectedSlot();

    @Shadow
    private boolean breakingBlock;

    @Shadow
    private int breakingPosX;

    @Shadow
    private int breakingPosY;

    @Shadow
    private int breakingPosZ;

    @Shadow
    private float blockBreakingProgress;

    @Shadow
    private float lastBlockBreakingProgress;

    @Shadow
    private float breakingSoundDelayTicks;

    @Inject(method = "interactBlock", at = @At("HEAD"), cancellable = true, order = 999)
    public void interactBlock(PlayerEntity player, World world, ItemStack stack, int x, int y, int z, int side, CallbackInfoReturnable<Boolean> cir) {
        if (stack != null && stack.getItem() instanceof EnhancedBlockInteraction) {
            this.updateSelectedSlot();
            this.networkHandler.sendPacket(new PlayerEnhancedInteractBlockC2SPacket(x, y, z, side, player.inventory.getSelectedItem(), Catalyst.getClickPosition()));
            cir.setReturnValue(((EnhancedBlockInteraction) stack.getItem()).useOnBlock(stack, player, world, x, y, z, side, Catalyst.getClickPosition()));
        }
    }

    @Inject(method = "attackBlock", at = @At("HEAD"), cancellable = true)
    public void attackBlock(int x, int y, int z, int dir, CallbackInfo ci) {
        if (Minecraft.INSTANCE.world.getBlockState(x, y, z).getBlock() instanceof EnhancedBlockInteraction) {
            if (!this.breakingBlock || x != this.breakingPosX || y != this.breakingPosY || z != this.breakingPosZ) {
                this.networkHandler.sendPacket(new PlayerEnhancedActionC2SPacket(0, x, y, z, dir, Catalyst.getClickPosition()));
                int blockId = this.minecraft.world.getBlockId(x, y, z);
                if (blockId > 0 && this.blockBreakingProgress == 0.0F) {
                    ((EnhancedBlockInteraction) Block.BLOCKS[blockId]).onBlockBreakStart(this.minecraft.world, x, y, z, dir, this.minecraft.player, Catalyst.getClickPosition());
                    Minecraft.INSTANCE.world.setBlockDirty(x,y,z);
                }

                if (blockId > 0 && Block.BLOCKS[blockId].getHardness(this.minecraft.player) >= 1.0F) {
                    this.breakBlock(x, y, z, dir);
                } else {
                    this.breakingBlock = true;
                    this.breakingPosX = x;
                    this.breakingPosY = y;
                    this.breakingPosZ = z;
                    this.blockBreakingProgress = 0.0F;
                    this.lastBlockBreakingProgress = 0.0F;
                    this.breakingSoundDelayTicks = 0.0F;
                }
            }
            ci.cancel();
        }
    }
}
