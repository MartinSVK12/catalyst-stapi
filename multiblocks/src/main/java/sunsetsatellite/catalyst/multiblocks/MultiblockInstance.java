package sunsetsatellite.catalyst.multiblocks;

import lombok.Getter;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.StationAPI;
import net.modificationstation.stationapi.api.event.world.BlockSetEvent;
import net.modificationstation.stationapi.api.state.property.Properties;
import sunsetsatellite.catalyst.core.util.BlockInstance;
import sunsetsatellite.catalyst.core.util.Direction;
import sunsetsatellite.catalyst.core.util.vector.Vec3i;

import static net.modificationstation.stationapi.api.state.property.Properties.HORIZONTAL_FACING;

public class MultiblockInstance {

	@Getter
    private boolean valid = false;
	public final BlockEntity origin;
	public final Multiblock data;

	public MultiblockInstance(BlockEntity origin, Multiblock data) {
		this.origin = origin;
		this.data = data;
        StationAPI.EVENT_BUS.register(this);
		valid = verifyIntegrity(null);
	}

    @EventListener
    public void blockChanged(BlockSetEvent event) {
        if(origin.world == null) {
            valid = false;
            return;
        }
        if(origin.world.getBlockId(origin.x, origin.y, origin.z) == 0) {
            valid = false;
            return;
        }
        BlockInstance blockInstance = new BlockInstance(
                event.blockState.getBlock(),
                new Vec3i(event.x, event.y, event.z),
                event.blockMeta,
                event.blockState,
                null
        );
        valid = verifyIntegrity(blockInstance);
    }

	public boolean verifyIntegrity(BlockInstance changedBlock){
		if(origin.world != null){
			if(origin.getBlock() != null){
				Direction direction = Direction.X_POS;
                if (origin.world.getBlockState(origin.x, origin.y, origin.z).contains(Properties.HORIZONTAL_FACING)) {
                    direction = Direction.getDirectionFromSide(origin.world.getBlockState(origin.x, origin.y, origin.z).get(HORIZONTAL_FACING).getId()).getOpposite();
                }
                BlockInstance originBlock = new BlockInstance(new Vec3i(origin.x, origin.y, origin.z), origin.world);
                if(changedBlock == null){
                    return data.isValidAt(
                            origin.world,
                            originBlock,
                            direction
                    );
                } else {
                    return data.isBlockValidAt(origin.world, changedBlock, originBlock, direction);
                }
			} else {
				return false;
			}
		} else {
			return false;
		}

	}

}
