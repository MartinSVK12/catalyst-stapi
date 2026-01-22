package sunsetsatellite.catalyst.core.util.mp.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.modificationstation.stationapi.api.entity.player.PlayerHelper;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import net.modificationstation.stationapi.api.util.SideUtil;
import org.jetbrains.annotations.NotNull;
import sunsetsatellite.catalyst.Catalyst;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.lang.reflect.InvocationTargetException;

public class OpenGuiPacket extends Packet implements ManagedPacket<OpenGuiPacket> {

    public int syncId;
    public String guiId;
    public GuiType type;
    public int blockX;
    public int blockY;
    public int blockZ;
    public int stackIndex;
    public boolean isArmor;
    public NbtCompound data = null;

    public static final PacketType<OpenGuiPacket> TYPE = PacketType.builder(true, false, OpenGuiPacket::new).build();

    public enum GuiType {
        ITEM, BLOCK
    }

    public OpenGuiPacket() {
    }

    public OpenGuiPacket(int syncId, String guiId, int x, int y, int z) {
        this.syncId = syncId;
        this.guiId = guiId;
        this.type = GuiType.BLOCK;
        this.blockX = x;
        this.blockY = y;
        this.blockZ = z;
    }

    public OpenGuiPacket(int syncId, String guiId, int x, int y, int z, NbtCompound data) {
        this.syncId = syncId;
        this.guiId = guiId;
        this.type = GuiType.BLOCK;
        this.blockX = x;
        this.blockY = y;
        this.blockZ = z;
        this.data = data;
    }

    public OpenGuiPacket(int syncId, String guiId, int stackIndex, boolean isArmor) {
        this.syncId = syncId;
        this.guiId = guiId;
        this.type = GuiType.ITEM;
        this.stackIndex = stackIndex;
        this.isArmor = isArmor;
    }

    @Override
    public void read(DataInputStream stream) {
        try {
            this.syncId = stream.readByte();
            this.guiId = stream.readUTF();
            this.type = GuiType.values()[stream.readInt()];
            this.blockX = stream.readInt();
            this.blockY = stream.readInt();
            this.blockZ = stream.readInt();
            this.stackIndex = stream.readInt();
            this.isArmor = stream.readBoolean();
            boolean hasData = stream.readBoolean();
            if (hasData) {
                this.data = Catalyst.readNbtFromStream(stream);
            } else {
                this.data = null;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void write(DataOutputStream stream) {
        try {
            stream.writeByte(this.syncId);
            stream.writeUTF(this.guiId);
            stream.writeInt(this.type.ordinal());
            stream.writeInt(this.blockX);
            stream.writeInt(this.blockY);
            stream.writeInt(this.blockZ);
            stream.writeInt(this.stackIndex);
            stream.writeBoolean(this.isArmor);
            stream.writeBoolean(data != null);
            if (data != null) {
                Catalyst.writeNbtToStream(data, stream);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void apply(NetworkHandler networkHandler) {
        SideUtil.run(() -> handleClient(networkHandler),()->{});
    }

    @Environment(EnvType.CLIENT)
    private void handleClient(NetworkHandler networkHandler) {
        PlayerEntity player = PlayerHelper.getPlayerFromPacketHandler(networkHandler);
        switch (type) {
            case BLOCK -> {
                BlockEntity be = null;
                if(player != null){
                    be = player.world.getBlockEntity(blockX, blockY, blockZ);
                }
                if(be != null){
                    if(data != null) {
                        try {
                            Minecraft.INSTANCE.setScreen((Screen) ((MpGuiEntryClient) Catalyst.GUIS.getItem(guiId)).guiClass.getDeclaredConstructors()[0].newInstance(player.inventory, be, data));
                        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        try {
                            Minecraft.INSTANCE.setScreen((Screen) ((MpGuiEntryClient) Catalyst.GUIS.getItem(guiId)).guiClass.getDeclaredConstructors()[0].newInstance(player.inventory, be));
                        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    player.currentScreenHandler.syncId = syncId;
                }
            }
            case ITEM -> {
                if(player != null){
                    try {
                        Minecraft.INSTANCE.setScreen((Screen) ((MpGuiEntryClient) Catalyst.GUIS.getItem(guiId)).guiClass.getDeclaredConstructors()[0].newInstance(player.inventory, stackIndex, isArmor));
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                    player.currentScreenHandler.syncId = syncId;
                }
            }
        }
    }

    @Override
    public int size() {
        return 6 * 4 + 1 + guiId.length();
    }

    @Override
    public @NotNull PacketType<OpenGuiPacket> getType() {
        return TYPE;
    }
}
