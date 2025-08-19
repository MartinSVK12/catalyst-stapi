package sunsetsatellite.catalyst.multiblocks;

import lombok.Getter;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.Minecraft;
import net.modificationstation.stationapi.api.StationAPI;
import net.modificationstation.stationapi.api.event.world.BlockSetEvent;
import sun.misc.Signal;
import sunsetsatellite.catalyst.Catalyst;
import sunsetsatellite.catalyst.core.util.BlockInstance;
import sunsetsatellite.catalyst.core.util.Direction;
import sunsetsatellite.catalyst.core.util.vector.Vec3i;

public class MultiblockInstance {

	@Getter
    private boolean valid = false;
	public final BlockEntity origin;
	public final Multiblock data;

	public MultiblockInstance(BlockEntity origin, Multiblock data) {
		this.origin = origin;
		this.data = data;
		valid = verifyIntegrity();
        StationAPI.EVENT_BUS.register(this);
	}

    @EventListener
    public void blockChanged(BlockSetEvent event) {
        if(origin.world == null) {
            valid = false;
            return;
        }
        if(origin.world.getBlockEntity(origin.x, origin.y, origin.z) != origin || origin.world.getBlockId(origin.x, origin.y, origin.z) == 0) {
            valid = false;
            return;
        }
        valid = verifyIntegrity();
    }

	public boolean verifyIntegrity(){
		if(origin.world != null){
			Block block = origin.getBlock();
			if(block != null){
				return data.isValidAtSilent(origin.world,new BlockInstance(block,new Vec3i(origin.x,origin.y,origin.z),origin), Direction.getDirectionFromSide(origin.world.getBlockMeta(origin.x,origin.y,origin.z)));
			} else {
				return false;
			}
		} else {
			return false;
		}

	}

}
