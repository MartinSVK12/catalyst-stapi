package sunsetsatellite.catalyst.core.util.mp;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.modificationstation.stationapi.api.entity.player.PlayerHelper;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import net.modificationstation.stationapi.api.util.SideUtil;
import org.jetbrains.annotations.NotNull;
import sunsetsatellite.catalyst.core.util.ScreenActionListener;
import sunsetsatellite.catalyst.core.util.vector.Vec3i;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ScreenActionPacket extends Packet implements ManagedPacket<ScreenActionPacket> {

    public static final PacketType<ScreenActionPacket> TYPE = PacketType.builder(false, true, ScreenActionPacket::new).build();

    public int id;
    public int button;
    public int channel;
    public Vec3i pos;

    public ScreenActionPacket() {}

    public ScreenActionPacket(int id, int button, int channel, Vec3i pos) {
        this.id = id;
        this.button = button;
        this.channel = channel;
        this.pos = pos;
    }

    @Override
    public void read(DataInputStream stream) {
        try {
            int x = stream.readInt();
            int y = stream.readInt();
            int z = stream.readInt();
            pos = new Vec3i(x, y, z);
            id = stream.readInt();
            button = stream.readInt();
            channel = stream.readInt();
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(DataOutputStream stream) {
        try {
            stream.writeInt(pos.x);
            stream.writeInt(pos.y);
            stream.writeInt(pos.z);
            stream.writeInt(id);
            stream.writeInt(button);
            stream.writeInt(channel);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void apply(NetworkHandler networkHandler) {
        SideUtil.run(() -> {},() -> handleServer(networkHandler));
    }

    @Environment(EnvType.SERVER)
    public void handleServer(NetworkHandler networkHandler) {
        PlayerEntity player = PlayerHelper.getPlayerFromPacketHandler(networkHandler);
        BlockEntity e = player.world.getBlockEntity(pos.x, pos.y, pos.z);
        if(e instanceof ScreenActionListener){
            ((ScreenActionListener) e).buttonClicked(id, button, channel);
        }
    }

    @Override
    public int size() {
        return 4 * 6;
    }

    @Override
    public @NotNull PacketType<ScreenActionPacket> getType() {
        return TYPE;
    }
}
