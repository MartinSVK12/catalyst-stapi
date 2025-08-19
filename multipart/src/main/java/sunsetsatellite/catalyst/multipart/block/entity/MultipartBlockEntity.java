package sunsetsatellite.catalyst.multipart.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.modificationstation.stationapi.mixin.registry.server.MinecraftServerMixin;
import sunsetsatellite.catalyst.Catalyst;
import sunsetsatellite.catalyst.core.util.Direction;
import sunsetsatellite.catalyst.core.util.mp.BlockEntityUpdatePacket;
import sunsetsatellite.catalyst.multipart.api.Multipart;
import sunsetsatellite.catalyst.multipart.api.SupportsMultiparts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultipartBlockEntity extends BlockEntity implements SupportsMultiparts {

    public final HashMap<Direction, Multipart> parts = (HashMap<Direction, Multipart>) Catalyst.mapOf(Direction.values(),new Multipart[Direction.values().length]);

    public MultipartBlockEntity() {

    }

    @Override
    public HashMap<Direction, Multipart> getParts() {
        return parts;
    }

    @Override
    public void tick() {
        super.tick();
        if(FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER){
            MinecraftServer server = (MinecraftServer) FabricLoader.getInstance().getGameInstance();
            List<ServerPlayNetworkHandler> list = server.connections.connections;
            list.forEach(handler -> handler.sendPacket(new BlockEntityUpdatePacket(this)));
        }
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        NbtCompound coversNbt = new NbtCompound();

        for (Map.Entry<Direction, Multipart> entry : parts.entrySet()) {
            if(entry.getValue() == null) continue;
            NbtCompound partNbt = new NbtCompound();
            entry.getValue().writeToNbt(partNbt);
            coversNbt.put(String.valueOf(entry.getKey().ordinal()),partNbt);
        }

        tag.put("Parts",coversNbt);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        NbtCompound coversNbt = tag.getCompound("Parts");

        for (Object o : coversNbt.entries.entrySet()) {
            Map.Entry<String, NbtElement> entry = (Map.Entry<String, NbtElement>) o;
            Direction dir = Direction.values()[Integer.parseInt(entry.getKey())];
            NbtCompound partTag = (NbtCompound) entry.getValue();
            parts.put(dir,new Multipart(partTag));
        }
    }

    @Override
    public Packet createUpdatePacket() {
        return super.createUpdatePacket();
    }
}
