package sunsetsatellite.catalyst.core.util.recipe;

import net.modificationstation.stationapi.api.registry.Registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecipeNamespace extends ActuallySimpleRegistry<RecipeGroup<? extends RecipeEntryBase<?,?,?>>> {
    public List<RecipeEntryBase<?,?,?>> getAllRecipes(){
        ArrayList<RecipeEntryBase<?,?,?>> recipes = new ArrayList<>();
        for (RecipeGroup<? extends RecipeEntryBase<?,?,?>> recipeGroup : this) {
            for (RecipeEntryBase<?,?,?> recipeEntry : recipeGroup) {
                recipes.add(recipeEntry);
            }
        }
        return Collections.unmodifiableList(recipes);
    }

}
