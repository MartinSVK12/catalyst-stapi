package sunsetsatellite.catalyst.core.util.recipe.crafting;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.ShapelessRecipe;
import sunsetsatellite.catalyst.core.util.recipe.RecipeSymbol;

import java.util.List;

public class RecipeEntryCraftingShapeless extends RecipeEntryCrafting<List<RecipeSymbol>, ItemStack>{

    public CraftingRecipe recipe;

    public RecipeEntryCraftingShapeless(CraftingRecipe recipe, List<RecipeSymbol> input, ItemStack output) {
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
