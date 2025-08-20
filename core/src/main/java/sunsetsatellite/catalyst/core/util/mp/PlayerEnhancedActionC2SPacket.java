package sunsetsatellite.catalyst.core.util.mp;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.ServerWorld;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import net.modificationstation.stationapi.api.util.SideUtil;
import org.jetbrains.annotations.NotNull;
import sunsetsatellite.catalyst.core.util.EnhancedBlockInteraction;
import sunsetsatellite.catalyst.core.util.mixin.interfaces.ServerPlayerInteractionManagerMixinInterface;
import sunsetsatellite.catalyst.core.util.vector.Vec2f;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PlayerEnhancedActionC2SPacket extends Packet implements ManagedPacket<PlayerEnhancedActionC2SPacket> {

    public static final PacketType<PlayerEnhancedActionC2SPacket> TYPE = PacketType.builder(false, true, PlayerEnhancedActionC2SPacket::new).build();

    public int x;
    public int y;
    public int z;
    public int side;
    public int action;
    public Vec2f clickPosition;

    public PlayerEnhancedActionC2SPacket() {}

    @Environment(value=EnvType.CLIENT)
    public PlayerEnhancedActionC2SPacket(int action, int x, int y, int z, int side, Vec2f clickPosition) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.side = side;
        this.action = action;
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
            this.action = stream.readInt();
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
            stream.writeInt(this.action);
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
        int yDiff;
        double calcZ;
        double calcY;
        double calcX;
        serverWorld.bypassSpawnProtection = serverWorld.dimension.id != 0 || handler.server.playerManager.isOperator(handler.player.name);
        boolean bypass = serverWorld.bypassSpawnProtection;
        boolean flag = false;
        if (action == 0) {
            flag = true;
        }
        if (flag && ((calcX = handler.player.x - ((double)x + 0.5)) * calcX + (calcY = handler.player.y - ((double)y + 0.5)) * calcY + (calcZ = handler.player.z - ((double)z + 0.5)) * calcZ) > 36.0) {
            return;
        }
        Vec3i vec3i = serverWorld.getSpawnPos();
        int xDiff = (int)MathHelper.abs(x - vec3i.x);
        if (xDiff > (yDiff = (int)MathHelper.abs(z - vec3i.z))) {
            yDiff = xDiff;
        }
        if (action == 0) {
            if (yDiff > 16 || bypass) {
                ((ServerPlayerInteractionManagerMixinInterface) handler.player.interactionManager).catalyst$onBlockBreakingAction(x, y, z, side, clickPosition);
            } else {
                handler.player.networkHandler.sendPacket(new BlockUpdateS2CPacket(x, y, z, serverWorld));
            }
        }
        serverWorld.bypassSpawnProtection = false;
    }

    @Override
    public int size() {
        return 30;
    }

    @Override
    public @NotNull PacketType<PlayerEnhancedActionC2SPacket> getType() {
        return TYPE;
    }
}
