package sunsetsatellite.catalyst.core.util.mp;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.entity.player.PlayerHelper;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import net.modificationstation.stationapi.api.util.SideUtil;
import org.jetbrains.annotations.NotNull;

import java.io.*;

public class BlockEntityUpdatePacket extends Packet implements ManagedPacket<BlockEntityUpdatePacket> {

    public NbtCompound data;
    public int dataSize = 0;
    public static final PacketType<BlockEntityUpdatePacket> TYPE = PacketType.builder(true, true, BlockEntityUpdatePacket::new).build();

    public BlockEntityUpdatePacket() {

    }

    public BlockEntityUpdatePacket(BlockEntity te) {
        data = new NbtCompound();
        te.writeNbt(data);
    }

    @Override
    public void read(DataInputStream stream) {
        data = readNbt(stream);
    }

    public NbtCompound readNbt(DataInputStream dis) {
        try {
            int length = Short.toUnsignedInt(dis.readShort());
            if (length == 0) {
                return null;
            } else {
                byte[] data = new byte[length];
                dis.readFully(data);
                return NbtIo.readCompressed(new ByteArrayInputStream(data));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeNbt(NbtCompound tag, DataOutputStream dos) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            NbtIo.writeCompressed(tag, baos);
            byte[] buffer = baos.toByteArray();
            dos.writeShort((short)buffer.length);
            dos.write(buffer);
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(DataOutputStream stream) {
        int before = stream.size();
        writeNbt(data, stream);
        dataSize = stream.size() - before;
    }

    @Override
    public void apply(NetworkHandler networkHandler) {
        SideUtil.run(() -> handleClient(networkHandler),()->{});
    }

    @Environment(EnvType.CLIENT)
    public void handleClient(NetworkHandler networkHandler) {
        try {
            Class<?> clazz = (Class<?>) BlockEntity.idToClass.get(data.getString("id"));
            if (clazz != null) {
                PlayerEntity player = PlayerHelper.getPlayerFromPacketHandler(networkHandler);
                World world = player.world;
                BlockEntity te = world.getBlockEntity(data.getInt("x"),data.getInt("y"),data.getInt("z"));
                if(te != null && te.getClass() == clazz){
                    te.readNbt(data);
                } else {
                    BlockEntity newTe = BlockEntity.createFromNbt(data);
                    if(newTe != null){
                        world.setBlockEntity(data.getInt("x"),data.getInt("y"),data.getInt("z"),newTe);
                    }
                }

                world.setBlockDirty(data.getInt("x"),data.getInt("y"),data.getInt("z"));
            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public int size() {
        return dataSize;
    }

    @Override
    public @NotNull PacketType<BlockEntityUpdatePacket> getType() {
        return TYPE;
    }
}
