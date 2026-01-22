package sunsetsatellite.catalyst.core.mixin;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import sunsetsatellite.catalyst.Catalyst;
import sunsetsatellite.catalyst.core.util.mp.gui.MpGui;
import sunsetsatellite.catalyst.core.util.mp.gui.MpGuiEntry;
import sunsetsatellite.catalyst.core.util.mp.gui.OpenGuiPacket;

import java.lang.reflect.InvocationTargetException;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements MpGui {

    private ServerPlayerEntityMixin(World world) {
        super(world);
    }

    @Shadow
    protected abstract void incrementScreenHandlerSyncId();

    @Shadow
    private int screenHandlerSyncId;
    @Shadow
    public ServerPlayNetworkHandler networkHandler;

    @Shadow
    public abstract void initScreenHandler();

    @Unique
    private final ServerPlayerEntity thisAs = (ServerPlayerEntity) (Object) this;


    @Override
    public void catalyst$displayCustomGUI(Inventory inventory, int slotIndex, boolean isArmor, String id) {
        incrementScreenHandlerSyncId();
        MpGuiEntry entry = Catalyst.GUIS.getItem(id);
        networkHandler.sendPacket(new OpenGuiPacket(screenHandlerSyncId, id, slotIndex, isArmor));
        if(entry.containerClass != null){
            try {
                currentScreenHandler = (ScreenHandler) entry.containerClass.getDeclaredConstructors()[0].newInstance(thisAs.inventory, slotIndex, isArmor);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            currentScreenHandler.syncId = screenHandlerSyncId;
            initScreenHandler();
        }
    }

    @Override
    public void catalyst$displayCustomGUI(BlockEntity be, String id) {
        incrementScreenHandlerSyncId();
        MpGuiEntry entry = Catalyst.GUIS.getItem(id);
        networkHandler.sendPacket(new OpenGuiPacket(screenHandlerSyncId, id, be.x, be.y, be.z));
        if(entry.containerClass != null){
            try {
                currentScreenHandler = (ScreenHandler) entry.containerClass.getDeclaredConstructors()[0].newInstance(thisAs.inventory, be);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            currentScreenHandler.syncId = screenHandlerSyncId;
            initScreenHandler();
        }
    }

    @Override
    public void catalyst$displayCustomGUI(BlockEntity be, String id, NbtCompound data) {
        incrementScreenHandlerSyncId();
        MpGuiEntry entry = Catalyst.GUIS.getItem(id);
        networkHandler.sendPacket(new OpenGuiPacket(screenHandlerSyncId, id, be.x, be.y, be.z, data));
        if(entry.containerClass != null){
            try {
                currentScreenHandler = (ScreenHandler) entry.containerClass.getDeclaredConstructors()[0].newInstance(thisAs.inventory, be, data);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            currentScreenHandler.syncId = screenHandlerSyncId;
            initScreenHandler();
        }
    }
}
