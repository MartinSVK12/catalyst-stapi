package sunsetsatellite.catalyst.core.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipeManager;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.ShapelessRecipe;
import net.modificationstation.stationapi.impl.recipe.StationShapedRecipe;
import net.modificationstation.stationapi.impl.recipe.StationShapelessRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import sunsetsatellite.catalyst.Catalyst;
import sunsetsatellite.catalyst.core.util.recipe.RecipeGroup;
import sunsetsatellite.catalyst.core.util.recipe.RecipeSymbol;
import sunsetsatellite.catalyst.core.util.recipe.crafting.RecipeEntryCraftingShaped;
import sunsetsatellite.catalyst.core.util.recipe.crafting.RecipeEntryCraftingShapeless;

import java.util.Arrays;
import java.util.List;

@Mixin(CraftingRecipeManager.class)
public class CraftingRecipeManagerMixin {

    @WrapOperation(method = "addShapedRecipe", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    private static boolean interceptShapedRecipe(List<?> instance, Object o, Operation<Boolean> original){
        if(Catalyst.CRAFTING_RECIPES == null) {
            Catalyst.CRAFTING_RECIPES = new RecipeGroup<>(List.of(new ItemStack(Block.CRAFTING_TABLE)));
            Catalyst.MINECRAFT_RECIPES.register("crafting",Catalyst.CRAFTING_RECIPES);
            Catalyst.RECIPES.register("minecraft",Catalyst.MINECRAFT_RECIPES);
        }
        ShapedRecipe recipe = (ShapedRecipe) o;
        RecipeSymbol[] array = Arrays.stream(recipe.input).map((S)->{
            if(S == null) return null;
            return new RecipeSymbol(S);
        }).toArray(RecipeSymbol[]::new);
        Catalyst.CRAFTING_RECIPES.register("shaped_"+instance.size(),new RecipeEntryCraftingShaped(recipe, array, recipe.getOutput()));
        return original.call(instance, o);
    }

    @SuppressWarnings("unchecked")
    @WrapOperation(method = "addShapelessRecipe", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 3))
    private static boolean interceptShapelessRecipe(List<?> instance, Object o, Operation<Boolean> original){
        if(Catalyst.CRAFTING_RECIPES == null) {
            Catalyst.CRAFTING_RECIPES = new RecipeGroup<>(List.of(new ItemStack(Block.CRAFTING_TABLE)));
            Catalyst.MINECRAFT_RECIPES.register("crafting",Catalyst.CRAFTING_RECIPES);
            Catalyst.RECIPES.register("minecraft",Catalyst.MINECRAFT_RECIPES);
        }
        ShapelessRecipe recipe = (ShapelessRecipe) o;
        List<RecipeSymbol> list = recipe.input.stream().map((S)->{
            if(S == null) return null;
            return new RecipeSymbol((ItemStack) S);
        }).toList();
        Catalyst.CRAFTING_RECIPES.register("shapeless_"+instance.size(),new RecipeEntryCraftingShapeless(recipe, list, recipe.getOutput()));
        return original.call(instance, o);
    }

}
