package sunsetsatellite.catalyst.core.mixin;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import sunsetsatellite.catalyst.core.util.EnhancedBlockInteraction;
import sunsetsatellite.catalyst.core.util.mixin.interfaces.ServerPlayerInteractionManagerMixinInterface;
import sunsetsatellite.catalyst.core.util.vector.Vec2f;

@Mixin(ServerPlayerInteractionManager.class)
public abstract class ServerPlayerInteractionManagerMixin implements ServerPlayerInteractionManagerMixinInterface {

    @Shadow
    private ServerWorld world;

    @Shadow
    private int failedMiningStartTime;

    @Shadow
    private int tickCounter;

    @Shadow
    public PlayerEntity player;

    @Shadow
    public abstract boolean tryBreakBlock(int x, int y, int z);

    @Shadow
    private int failedMiningX;

    @Shadow
    private int failedMiningY;

    @Shadow
    private int failedMiningZ;

    @Shadow
    public abstract void onBlockBreakingAction(int x, int y, int z, int direction);

    @Unique
    @Override
    public void catalyst$onBlockBreakingAction(int x, int y, int z, int direction, Vec2f clickPosition) {
        Block b = this.world.getBlockState(x, y, z).getBlock();
        if(b instanceof EnhancedBlockInteraction block){
            this.world.extinguishFire(null, x, y, z, direction);
            this.failedMiningStartTime = this.tickCounter;
            block.onBlockBreakStart(this.world, x, y, z, direction, this.player, clickPosition);
            this.world.setBlockDirty(x,y,z);
            if (b.getHardness(this.player) >= 1.0F) {
                this.tryBreakBlock(x, y, z);
            } else {
                this.failedMiningX = x;
                this.failedMiningY = y;
                this.failedMiningZ = z;
            }
        } else {
            this.onBlockBreakingAction(x, y, z, direction);
        }
    }

}
