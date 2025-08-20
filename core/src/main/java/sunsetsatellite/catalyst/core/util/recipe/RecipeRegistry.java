package sunsetsatellite.catalyst.core.util.recipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecipeRegistry extends ActuallySimpleRegistry<RecipeNamespace>{

    @Override
    public void register(String key, RecipeNamespace item) {
        if(key.contains(":") || key.contains("/")) throw new IllegalArgumentException("Keys cannot contain ':' or '/'!");
        super.register(key, item);
    }

    public List<RecipeEntryBase<?,?,?>> getAllRecipes(){
        ArrayList<RecipeEntryBase<?,?,?>> recipes = new ArrayList<>();
        for (RecipeNamespace recipeNamespace : this) {
            for (RecipeGroup<? extends RecipeEntryBase<?,?,?>> recipeGroup : recipeNamespace) {
                for (RecipeEntryBase<?,?,?> recipeEntry : recipeGroup) {
                    recipes.add(recipeEntry);
                }
            }
        }
        return Collections.unmodifiableList(recipes);
    }

    public List<RecipeGroup<?>> getAllGroups(){
        ArrayList<RecipeGroup<?>> recipes = new ArrayList<>();
        for (RecipeNamespace recipeNamespace : this) {
            for (RecipeGroup<?> recipeGroup : recipeNamespace) {
                recipes.add(recipeGroup);
            }
        }
        return Collections.unmodifiableList(recipes);
    }

    public void addRecipe(String recipeKey, RecipeEntryBase<?,?,?> recipe){
        RecipeGroup<RecipeEntryBase<?, ?, ?>> group = getGroupFromKey(recipeKey);
        String key = deconstructKey(recipeKey)[2];
        group.register(key,recipe);
    }

    public <T extends RecipeEntryBase<?,?,?>> RecipeBranch<T> getRecipeFromKey(String key){
        if(!(key.contains(":") && key.contains("/"))) throw new IllegalArgumentException("Invalid or malformed key!");
        String[] keys = deconstructKey(key);
        String namespaceKey = keys[0];
        String groupKey = keys[1];
        String recipeKey = keys[2];
        RecipeNamespace namespace = getItem(namespaceKey);
        if(namespace == null){
            throw new IllegalArgumentException(String.format("Namespace '%s' doesn't exist!",namespaceKey));
        }
        RecipeGroup<? extends RecipeEntryBase<?, ?, ?>> group = namespace.getItem(groupKey);
        if(group == null){
            throw new IllegalArgumentException(String.format("Group '%s' in namespace '%s' doesn't exist!",groupKey,namespaceKey));
        }
        T recipe = (T) group.getItem(recipeKey); //unchecked cast? cope.
        if(recipe == null){
            throw new IllegalArgumentException(String.format("Recipe '%s' in group '%s' in namespace '%s' doesn't exist!",recipeKey,groupKey,namespaceKey));
        }
        return new RecipeBranch<>(namespace,group,recipe);
    }

    public String[] deconstructKey(String key){
        String namespaceKey = key.split(":")[0];
        String groupKey = key.split(":")[1].split("/")[0];
        String recipeKey = "";
        if(key.contains("/")){
            recipeKey = key.split(":")[1].split("/")[1];
        }
        return new String[]{namespaceKey,groupKey,recipeKey};
    }

    public <T extends RecipeEntryBase<?, ?, ?>> RecipeGroup<T> getGroupFromKey(String key){
        if(!(key.contains(":"))) throw new IllegalArgumentException("Invalid or malformed key!");
        String[] keys;
        try {
            keys = deconstructKey(key);
        } catch (ArrayIndexOutOfBoundsException e){
            throw new IllegalArgumentException("Invalid or malformed key!",e);
        }
        String namespaceKey = keys[0];
        String groupKey = keys[1];
        RecipeNamespace namespace = getItem(namespaceKey);
        if(namespace == null){
            throw new IllegalArgumentException(String.format("Namespace '%s' doesn't exist!",namespaceKey));
        }
        RecipeGroup<T> group = (RecipeGroup<T>) namespace.getItem(groupKey);
        if(group == null){
            throw new IllegalArgumentException(String.format("Group '%s' in namespace '%s' doesn't exist!",groupKey,namespaceKey));
        }
        return group;
    }

}
