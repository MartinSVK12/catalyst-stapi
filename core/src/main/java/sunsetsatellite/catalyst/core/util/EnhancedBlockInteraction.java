package sunsetsatellite.catalyst.core.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import sunsetsatellite.catalyst.core.util.vector.Vec2f;

public interface EnhancedBlockInteraction {
    default boolean useOnBlock(ItemStack stack, PlayerEntity user, World world, int x, int y, int z, int side, Vec2f clickPosition){
        return false;
    }

    default void onBlockBreakStart(World world, int x, int y, int z, int side, PlayerEntity player, Vec2f clickPosition){}
}
