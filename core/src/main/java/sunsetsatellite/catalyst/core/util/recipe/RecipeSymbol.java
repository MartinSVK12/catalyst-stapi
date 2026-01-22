package sunsetsatellite.catalyst.core.util.recipe;

import net.danygames2014.nyalib.fluid.FluidStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.registry.ItemRegistry;
import net.modificationstation.stationapi.api.tag.TagKey;
import net.modificationstation.stationapi.api.util.Identifier;

import java.util.*;
import java.util.stream.Collectors;

public class RecipeSymbol {
    private char symbol;
    private ItemStack stack;
    private TagKey<Item> itemGroup;
    private FluidStack fluidStack;

    private List<ItemStack> override;

    private RecipeSymbol(char symbol, ItemStack stack, FluidStack fluidStack, TagKey<Item> itemGroup) {
        if(stack == null && fluidStack == null && (itemGroup == null)){
            throw new NullPointerException("Null symbol!");
        }
        this.symbol = symbol;
        this.stack = stack;
        this.itemGroup = itemGroup;
        this.fluidStack = fluidStack;
    }

    public RecipeSymbol(char symbol, ItemStack stack, TagKey<Item> itemGroup) {
        if(stack == null && (itemGroup == null)){
            throw new NullPointerException("Null symbol!");
        }
        this.symbol = symbol;
        this.stack = stack;
        this.itemGroup = itemGroup;
    }

    public RecipeSymbol(ItemStack stack, TagKey<Item> itemGroup) {
        if(stack == null && (itemGroup == null)){
            throw new NullPointerException("Null symbol!");
        }
        this.stack = stack;
        this.itemGroup = itemGroup;
    }

    public RecipeSymbol(ItemStack stack) {
        if(stack == null){
            throw new NullPointerException("Null symbol!");
        }
        this.stack = stack;
    }

    public RecipeSymbol(TagKey<Item> itemGroup) {
        if(itemGroup == null){
            throw new NullPointerException("Null symbol!");
        }
        this.itemGroup = itemGroup;
    }

    public RecipeSymbol(FluidStack fluidStack) {
        if(fluidStack == null){
            throw new NullPointerException("Null symbol!");
        }
        this.fluidStack = fluidStack;
    }

    public RecipeSymbol(char symbol, FluidStack fluidStack) {
        if(fluidStack == null){
            throw new NullPointerException("Null symbol!");
        }
        this.symbol = symbol;
        this.fluidStack = fluidStack;
    }

    public RecipeSymbol(List<ItemStack> override){
        if(override == null || override.isEmpty()) {
            throw new NullPointerException("Null symbol!");
        }
        this.override = override;
        this.stack = override.get(0);
    }

    public List<ItemStack> resolve(){
        if(override != null) return override;
        if(stack != null) {
            return Collections.singletonList(stack);
        } /*else if (itemGroup != null && stack == null) {
            return Registries.ITEM_GROUPS.getItem(itemGroup);
        } else if (itemGroup != null) {
            ArrayList<ItemStack> list = new ArrayList<>(Registries.ITEM_GROUPS.getItem(itemGroup));
            list.add(stack);
            return list;
        }*/
        return new ArrayList<>();
    }

    public List<FluidStack> resolveFluids(){
        if(fluidStack != null && itemGroup == null){
            return Collections.singletonList(fluidStack);
        }
        return new ArrayList<>();
    }

    public boolean matches(ItemStack stack){
        if(stack == null) return false;
        if(itemGroup != null && stack.isIn(itemGroup)) return true;
        List<ItemStack> stacks = resolve();
        boolean foundId = false;
        boolean foundMeta = false;
        for (ItemStack resolvedStack : stacks) {
            if(resolvedStack.itemId == stack.itemId){
                foundId = true;
            }
            if(resolvedStack.getDamage() == -1 || resolvedStack.getDamage() == stack.getDamage()){
                foundMeta = true;
            }
        }
        return foundId && foundMeta;
    }

    public boolean matches(RecipeSymbol symbol){
        if(symbol == null) return false;
        if(equals(symbol)) return true;
        List<RecipeSymbol> symbols = resolve().stream().map(RecipeSymbol::new).toList();
        List<ItemStack> checkedStacks = symbol.resolve();
        return symbols.stream().anyMatch((S)->checkedStacks.stream().anyMatch(S::matches));
    }

    public boolean matchesFluid(FluidStack fluidStack){
        if(fluidStack == null) return false;
        List<FluidStack> fluidStacks = resolveFluids();
        boolean found = false;
        for (FluidStack fluid : fluidStacks) {
            if(fluid.isFluidEqual(fluidStack)){
                found = true;
                break;
            }
        }
        return found;
    }

    public char getSymbol() {
        return symbol;
    }

    public ItemStack getStack() {
        return stack;
    }

    public TagKey<Item> getItemGroup() {
        return itemGroup;
    }

    public FluidStack getFluidStack() { return fluidStack; }

    public boolean hasFluid() { return fluidStack != null; }

    public RecipeSymbol copy(){
        return new RecipeSymbol(symbol,stack,fluidStack,itemGroup);
    }

    public static RecipeSymbol[] arrayOf(Object... objs){
        //java varargs bruh moment
        if(objs.length == 1 && objs[0] instanceof List){
            return arrayOf((List<Object>) objs[0]);
        }

        return Arrays.stream(objs).map((O) -> {
            if (O instanceof ItemStack) {
                return new RecipeSymbol((ItemStack) O);
            } else if (O instanceof FluidStack) {
                return new RecipeSymbol((FluidStack) O);
            } else {
                return null;
            }
        }).filter(Objects::nonNull).toArray(RecipeSymbol[]::new);
    }

    public static RecipeSymbol[] arrayOf(List<Object> list){
        return list.stream().map((O) -> {
            if (O instanceof ItemStack) {
                return new RecipeSymbol((ItemStack) O);
            } else if (O instanceof FluidStack) {
                return new RecipeSymbol((FluidStack) O);
            } else {
                return null;
            }
        }).filter(Objects::nonNull).toArray(RecipeSymbol[]::new);
    }

    public static List<RecipeSymbol> listOf(Object... objs){
        return Arrays.stream(objs).map((O) -> {
            if (O instanceof ItemStack) {
                return new RecipeSymbol((ItemStack) O);
            } else if (O instanceof FluidStack) {
                return new RecipeSymbol((FluidStack) O);
            } else {
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public RecipeSymbol toItemSymbol(){
        if(fluidStack != null){
            return new RecipeSymbol(new ItemStack(fluidStack.fluid.getFlowingBlock(),fluidStack.amount));
        } else {
            return this;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RecipeSymbol that = (RecipeSymbol) o;

        if (getSymbol() != that.getSymbol()) return false;
        if (getStack() != null && that.getStack() != null ? !getStack().isItemEqual(that.getStack()) : that.getStack() != null) {
            return false;
        }
        if (getItemGroup() != null ? !getItemGroup().equals(that.getItemGroup()) : that.getItemGroup() != null)
            return false;
        return getFluidStack() != null ? getFluidStack().isFluidEqual(that.getFluidStack()) : that.getFluidStack() == null;
    }



    @Override
    public String toString() {
        if(stack != null && itemGroup == null){
            return "symbol: "+stack;
        } else if (itemGroup != null && stack == null) {
            return "symbol: "+itemGroup;
        } else if (itemGroup != null) {
            return "symbol: "+stack+" && "+itemGroup;
        } else if (fluidStack != null) {
            return "symbol: "+fluidStack;
        }
        return "null symbol";
    }
}
