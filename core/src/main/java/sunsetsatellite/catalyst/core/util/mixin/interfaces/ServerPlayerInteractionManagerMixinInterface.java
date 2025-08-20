package sunsetsatellite.catalyst.core.util.mixin.interfaces;

import org.spongepowered.asm.mixin.Unique;
import sunsetsatellite.catalyst.core.util.vector.Vec2f;

public interface ServerPlayerInteractionManagerMixinInterface {
    @Unique
    void catalyst$onBlockBreakingAction(int x, int y, int z, int direction, Vec2f clickPosition);
}
