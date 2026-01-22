package sunsetsatellite.catalyst.core.util.recipe.crafting;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.ShapedRecipe;
import sunsetsatellite.catalyst.core.util.recipe.RecipeSymbol;

import java.util.Arrays;

public class RecipeEntryCraftingShaped extends RecipeEntryCrafting<RecipeSymbol[], ItemStack> {

    public CraftingRecipe recipe;

    public RecipeEntryCraftingShaped(CraftingRecipe recipe, RecipeSymbol[] input, ItemStack output) {
        super(input, output);
        this.recipe = recipe;
    }

    @Override
    public int getRecipeSize() {
        return recipe.getSize();
    }

    @Override
    public boolean matches(CraftingInventory containerCrafting) {
        return recipe.matches(containerCrafting);
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory containerCrafting) {
        return recipe.craft(containerCrafting);
    }
}
