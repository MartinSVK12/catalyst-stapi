package sunsetsatellite.catalyst.core.util.recipe.crafting;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import sunsetsatellite.catalyst.core.util.recipe.RecipeEntryBase;

public abstract class RecipeEntryCrafting<I, O> extends RecipeEntryBase<I, O, Void> {
    public RecipeEntryCrafting(I input, O output) {
        super(input, output, null);
    }

    public RecipeEntryCrafting() {
    }

    public abstract int getRecipeSize();
    public abstract boolean matches(CraftingInventory containerCrafting);
    public abstract ItemStack getCraftingResult(CraftingInventory containerCrafting);
}
