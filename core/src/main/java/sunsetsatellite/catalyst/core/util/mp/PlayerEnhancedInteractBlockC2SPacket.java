package sunsetsatellite.catalyst.core.util.mp;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.ServerWorld;
import net.modificationstation.stationapi.api.entity.player.PlayerHelper;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import net.modificationstation.stationapi.api.util.SideUtil;
import org.jetbrains.annotations.NotNull;
import sunsetsatellite.catalyst.core.util.EnhancedBlockInteraction;
import sunsetsatellite.catalyst.core.util.ScreenActionListener;
import sunsetsatellite.catalyst.core.util.vector.Vec2f;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PlayerEnhancedInteractBlockC2SPacket extends Packet implements ManagedPacket<PlayerEnhancedInteractBlockC2SPacket> {

    public static final PacketType<PlayerEnhancedInteractBlockC2SPacket> TYPE = PacketType.builder(false, true, PlayerEnhancedInteractBlockC2SPacket::new).build();

    public int x;
    public int y;
    public int z;
    public int side;
    public ItemStack stack;
    public Vec2f clickPosition;

    public PlayerEnhancedInteractBlockC2SPacket() {}

    @Environment(value=EnvType.CLIENT)
    public PlayerEnhancedInteractBlockC2SPacket(int x, int y, int z, int side, ItemStack stack, Vec2f clickPosition) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.side = side;
        this.stack = stack;
        this.clickPosition = clickPosition;
    }

    @Override
    public void read(DataInputStream stream) {
        try {
            double vx = stream.readDouble();
            double vy = stream.readDouble();
            this.clickPosition = new Vec2f(vx, vy);
            this.x = stream.readInt();
            this.y = stream.read();
            this.z = stream.readInt();
            this.side = stream.read();
            short s = stream.readShort();
            if (s >= 0) {
                byte by = stream.readByte();
                short s2 = stream.readShort();
                this.stack = new ItemStack(s, by, s2);
            } else {
                this.stack = null;
            }
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(DataOutputStream stream) {
        try {
            stream.writeDouble(this.clickPosition.x);
            stream.writeDouble(this.clickPosition.y);
            stream.writeInt(this.x);
            stream.write(this.y);
            stream.writeInt(this.z);
            stream.write(this.side);
            if (this.stack == null) {
                stream.writeShort(-1);
            } else {
                stream.writeShort(this.stack.itemId);
                stream.writeByte(this.stack.count);
                stream.writeShort(this.stack.getDamage());
            }
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
        ServerPlayNetworkHandler handler = (ServerPlayNetworkHandler) networkHandler;
        ServerWorld serverWorld = handler.server.getWorld(handler.player.dimensionId);
        ItemStack itemStack = handler.player.inventory.getSelectedItem();
        boolean elevatedPermission = serverWorld.bypassSpawnProtection = serverWorld.dimension.id != 0 || handler.server.playerManager.isOperator(handler.player.name);
        Vec3i spawnPos = serverWorld.getSpawnPos();
        int xDiff = (int) MathHelper.abs((float)(x - spawnPos.x));
        int zDiff = (int)MathHelper.abs((float)(z - spawnPos.z));
        if (xDiff > zDiff) {
            zDiff = xDiff;
        }
        if (handler.teleported && handler.player.getSquaredDistance((double)x + (double)0.5F, (double)y + (double)0.5F, (double)z + (double)0.5F) < (double)64.0F && (zDiff > 16 || elevatedPermission)) {
            Block b = serverWorld.getBlockState(x, y, z).getBlock();
            if(itemStack.getItem() instanceof EnhancedBlockInteraction item){
                item.useOnBlock(itemStack, handler.player, serverWorld, x, y, z, side, clickPosition);
            }
        }

        //handler.player.networkHandler.sendPacket(new BlockUpdateS2CPacket(x, y, z, serverWorld));
        if (side == 0) {
            --y;
        }

        if (side == 1) {
            ++y;
        }

        if (side == 2) {
            --z;
        }

        if (side == 3) {
            ++z;
        }

        if (side == 4) {
            --x;
        }

        if (side == 5) {
            ++x;
        }

        //handler.player.networkHandler.sendPacket(new BlockUpdateS2CPacket(x, y, z, serverWorld));
    }

    @Override
    public int size() {
        return 15 + 16;
    }

    @Override
    public @NotNull PacketType<PlayerEnhancedInteractBlockC2SPacket> getType() {
        return TYPE;
    }
}
