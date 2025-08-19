package sunsetsatellite.catalyst.multipart.api.impl.ami;

import net.glasslauncher.mods.alwaysmoreitems.api.*;
import net.glasslauncher.mods.alwaysmoreitems.config.ConfigChangedListener;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.modificationstation.stationapi.api.util.Identifier;
import sunsetsatellite.catalyst.Catalyst;
import sunsetsatellite.catalyst.CatalystMultipart;

import java.util.ArrayList;

public class AMIPlugin implements ModPluginProvider {
    @Override
    public String getName() {
        return "Catalyst: Multipart";
    }

    @Override
    public Identifier getId() {
        return Catalyst.NAMESPACE.id("multipart");
    }

    @Override
    public void onAMIHelpersAvailable(AMIHelpers amiHelpers) {

    }

    @Override
    public void onItemRegistryAvailable(ItemRegistry itemRegistry) {

    }

    @Override
    public void register(ModRegistry modRegistry) {

    }

    @Override
    public void onRecipeRegistryAvailable(RecipeRegistry recipeRegistry) {

    }

    @Override
    public SyncableRecipe deserializeRecipe(NbtCompound nbtCompound) {
        return null;
    }

    @Override
    public void updateBlacklist(AMIHelpers amiHelpers) {
        for (Block block : CatalystMultipart.multipartTypeBlocks) {
            amiHelpers.getItemBlacklist().addItemToBlacklist(new ItemStack(block));
        }
        amiHelpers.getItemBlacklist().addItemToBlacklist(new ItemStack(CatalystMultipart.multipartItem));
        amiHelpers.getItemBlacklist().addItemToBlacklist(new ItemStack(CatalystMultipart.multipart));
    }
}
