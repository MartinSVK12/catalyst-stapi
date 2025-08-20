package sunsetsatellite.catalyst.core.util.recipe;

import java.util.Objects;

public abstract class RecipeEntryBase<I,O,D> {
    private final I input;
    private final O output;
    private final D data;
    public RecipeGroup parent;

    public RecipeEntryBase(I input, O output, D data) {
        this.input = input;
        this.output = output;
        this.data = data;
    }

    public RecipeEntryBase(){
        this.input = null;
        this.output = null;
        this.data = null;
    }

    public I getInput() {
        return input;
    }

    public O getOutput() {
        return output;
    }

    public D getData() {
        return data;
    }

    public boolean containsInput(I input){
        return input.equals(this.input);
    }

    public boolean containsOutput(O output){
        return output.equals(this.output);
    }

    public boolean containsData(D data){
        return data == this.data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RecipeEntryBase<?, ?, ?> that = (RecipeEntryBase<?, ?, ?>) o;

        if (getInput() != null ? !getInput().equals(that.getInput()) : that.getInput() != null) return false;
        if (getOutput() != null ? !getOutput().equals(that.getOutput()) : that.getOutput() != null) return false;
        if (getData() != null ? !getData().equals(that.getData()) : that.getData() != null) return false;
        return Objects.equals(parent, that.parent);
    }

    @Override
    public String toString() {
        RecipeNamespace namespace = (RecipeNamespace) parent.getParent();
        String recipeKey = parent.getKey(this);
        String groupKey = namespace.getKey(parent);
        String namespaceKey = ((RecipeRegistry)namespace.getParent()).getKey(namespace);
        return String.format("%s:%s/%s", namespaceKey, groupKey, recipeKey);
    }
}