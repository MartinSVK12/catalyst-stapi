package sunsetsatellite.catalyst.core.util.recipe;

import lombok.Getter;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.registry.Registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class RecipeGroup<T extends RecipeEntryBase<?,?,?>> extends ActuallySimpleRegistry<T> {

    private final ItemStack machine;

    public RecipeGroup(ItemStack machine) {
        this.machine = machine;
    }

    @Override
    public void register(String key, T item) {
        super.register(key, item);
        item.parent = this;
    }

    public List<T> getAllRecipes(){
        ArrayList<T> recipes = new ArrayList<>();
        for (T recipeEntry : this) {
            recipes.add(recipeEntry);
        }
        return Collections.unmodifiableList(recipes);
    }
}
