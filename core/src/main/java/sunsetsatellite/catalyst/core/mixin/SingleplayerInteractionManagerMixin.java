package sunsetsatellite.catalyst.core.mixin;

import net.minecraft.block.Block;
import net.minecraft.client.InteractionManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.SingleplayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sunsetsatellite.catalyst.Catalyst;
import sunsetsatellite.catalyst.core.util.EnhancedBlockInteraction;

@Mixin(SingleplayerInteractionManager.class)
public abstract class SingleplayerInteractionManagerMixin extends InteractionManager {

    @Shadow
    private float blockBreakingProgress;

    private SingleplayerInteractionManagerMixin(Minecraft minecraft) {
        super(minecraft);
    }

    @Inject(method = "attackBlock", at = @At("HEAD"), cancellable = true)
    public void attackBlock(int x, int y, int z, int direction, CallbackInfo ci) {
        Block b = this.minecraft.world.getBlockState(x, y, z).getBlock();
        if(b instanceof EnhancedBlockInteraction block) {
            this.minecraft.world.extinguishFire(this.minecraft.player, x, y, z, direction);
            if (this.blockBreakingProgress == 0.0F) {
                block.onBlockBreakStart(this.minecraft.world, x, y, z, direction, this.minecraft.player, Catalyst.getClickPosition());
            }

            if (b.getHardness(this.minecraft.player) >= 1.0F) {
                this.breakBlock(x, y, z, direction);
            }
            ci.cancel();
        }
    }

    @Override
    public boolean interactBlock(PlayerEntity player, World world, ItemStack i, int x, int y, int z, int side) {
        int blockId = world.getBlockId(x, y, z);
        if (blockId > 0 && Block.BLOCKS[blockId].onUse(world, x, y, z, player)) {
            return true;
        } else {
            if (i != null && i.getItem() instanceof EnhancedBlockInteraction item) {
               return item.useOnBlock(i, player, world, x, y, z, side, Catalyst.getClickPosition());
            } else if (i != null) {
                return super.interactBlock(player, world, i, x, y, z, side);
            }
            return false;
        }
    }

}
