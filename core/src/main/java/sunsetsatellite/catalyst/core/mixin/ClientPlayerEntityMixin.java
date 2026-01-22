package sunsetsatellite.catalyst.core.mixin;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import sunsetsatellite.catalyst.Catalyst;
import sunsetsatellite.catalyst.core.util.mp.gui.MpGui;
import sunsetsatellite.catalyst.core.util.mp.gui.MpGuiEntryClient;

import java.lang.reflect.InvocationTargetException;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin implements MpGui {
    @Shadow
    protected Minecraft minecraft;

    @Override
    public void catalyst$displayCustomGUI(Inventory inventory, int slotIndex, boolean isArmor, String id) {
        MpGuiEntryClient entry = (MpGuiEntryClient) Catalyst.GUIS.getItem(id);
        try {
            minecraft.setScreen((Screen) entry.guiClass.getDeclaredConstructors()[0].newInstance(this.minecraft.player.inventory, slotIndex, isArmor));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void catalyst$displayCustomGUI(BlockEntity tileEntity, String id) {
        MpGuiEntryClient entry = (MpGuiEntryClient) Catalyst.GUIS.getItem(id);
        try {
            minecraft.setScreen((Screen) entry.guiClass.getDeclaredConstructors()[0].newInstance(this.minecraft.player.inventory, tileEntity));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void catalyst$displayCustomGUI(BlockEntity tileEntity, String id, NbtCompound data) {
        MpGuiEntryClient entry = (MpGuiEntryClient) Catalyst.GUIS.getItem(id);
        try {
            minecraft.setScreen((Screen) entry.guiClass.getDeclaredConstructors()[0].newInstance(this.minecraft.player.inventory, tileEntity, data));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
