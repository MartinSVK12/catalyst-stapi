package sunsetsatellite.catalyst.core.event;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.event.network.packet.PacketRegisterEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.registry.PacketTypeRegistry;
import net.modificationstation.stationapi.api.registry.Registry;
import net.modificationstation.stationapi.api.util.Namespace;
import sunsetsatellite.catalyst.Catalyst;
import sunsetsatellite.catalyst.core.util.mp.BlockEntityUpdatePacket;
import sunsetsatellite.catalyst.core.util.mp.PlayerEnhancedInteractBlockC2SPacket;
import sunsetsatellite.catalyst.core.util.mp.ScreenActionPacket;

public class PacketRegister {

    @Entrypoint.Namespace
    public static Namespace NAMESPACE;

    @EventListener
    public void registerPackets(PacketRegisterEvent event) {
        Registry.register(PacketTypeRegistry.INSTANCE, NAMESPACE.id("screen_action"), ScreenActionPacket.TYPE);
        Registry.register(PacketTypeRegistry.INSTANCE, NAMESPACE.id("block_entity_update"), BlockEntityUpdatePacket.TYPE);
        Registry.register(PacketTypeRegistry.INSTANCE, NAMESPACE.id("enhanced_player_interact"), PlayerEnhancedInteractBlockC2SPacket.TYPE);
        Catalyst.LOGGER.info("Registered packets");
    }

}
