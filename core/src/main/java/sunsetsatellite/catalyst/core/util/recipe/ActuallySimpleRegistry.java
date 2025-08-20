package sunsetsatellite.catalyst.core.util.recipe;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Stores an association between an object and a string key.
 * @param <T> The type of item to store.
 */
public class ActuallySimpleRegistry<T>
        implements Iterable<T>
{
    private final List<T> items = new ArrayList<>();
    private final Map<String, T> keyItemMap = new HashMap<>();
    private final Map<T, String> itemKeyMap = new HashMap<>();
    private final List<ItemAddedCallback<T>> callbacks = new ArrayList<>();
    private ActuallySimpleRegistry<?> parent = null;

    /**
     * Adds a new item to the registry.
     * @param key The key to associate with the item.
     * @param item The item to be associated with the key.
     */
    public void register(String key, T item)
    {
        Objects.requireNonNull(key);
        Objects.requireNonNull(item);

        items.add(item);
        keyItemMap.put(key, item);
        itemKeyMap.put(item, key);
        items.sort((t1, t2) -> String.valueOf(itemKeyMap.get(t1)).compareTo(String.valueOf(itemKeyMap.get(t2))));

        if(item instanceof ActuallySimpleRegistry<?>){
            ((ActuallySimpleRegistry<?>) item).parent = this;
        }

        for (ItemAddedCallback<T> callback : callbacks)
        {
            callback.onItemAdded(this, item);
        }
    }

    /**
     * Removes an item from the registry.
     * @param key The key associated with an item to be removed.
     */
    public void unregister(String key){
        Objects.requireNonNull(key);

        T item = keyItemMap.get(key);
        items.remove(item);
        keyItemMap.remove(key);
        itemKeyMap.remove(item);
        items.sort((t1, t2) -> String.valueOf(itemKeyMap.get(t1)).compareTo(String.valueOf(itemKeyMap.get(t2))));

        if(item instanceof ActuallySimpleRegistry<?>){
            ((ActuallySimpleRegistry<?>) item).parent = null;
        }
    }

    /**
     * Adds a callback to be called whenever an item is added to the registry.
     * @param callback The callback to be called.
     */
    public void addCallback(ItemAddedCallback<T> callback)
    {
        callbacks.add(callback);
    }

    /**
     * Fetches an item by its key.
     * @param key The key of the item.
     * @return The item associated with the given key, or null if no such key-item association is present.
     */
    public T getItem(String key)
    {
        Objects.requireNonNull(key);

        return keyItemMap.get(key);
    }

    /**
     * Fetches the key associated with a given item.
     * @param item The item whose corresponding key should be found.
     * @return The key associated with the given item, or null if no such key-item association is present.
     */
    public String getKey(T item)
    {
        Objects.requireNonNull(item);

        return itemKeyMap.get(item);
    }

    /**
     * Returns this registries parent if it has one.
     * @return The parent registry or null if this registry does not have a parent or if it is the root registry.
     */
    @SuppressWarnings("LombokGetterMayBeUsed")
    public ActuallySimpleRegistry<?> getParent(){
        return parent;
    }

    /**
     * Fetches the numeric ID of an item. Use with caution as this may change for a given item depending on what's loaded into the registry and when.
     * @param item The item whose corresponding numeric ID should be found.
     * @return The numeric ID of the item, or -1 if the item is not present in the registry.
     */
    public int getNumericIdOfItem(T item)
    {
        Objects.requireNonNull(item);

        return items.indexOf(item);
    }

    /**
     * Fetches the numeric ID of the item corresponding to a key. Use with caution as this may change for a given item depending on what's loaded into the registry and when.
     * @param key The key whose associated item's corresponding numeric ID should be found.
     * @return The numeric ID of the item associated to the key, or -1 if the item is not present in the registry.
     */
    public int getNumericIdOfKey(String key)
    {
        Objects.requireNonNull(key);

        T item = getItem(key);

        if (item != null)
        {
            return getNumericIdOfItem(item);
        }

        return -1;
    }

    /**
     * Fetches the item corresponding to a numeric ID. Use with caution as this may change for a given item depending on what's loaded into the registry and when.
     * @param id The ID whose corresponding item should be found.
     * @return The item corresponding to the given ID, or null if it does not match an item.
     */
    public T getItemByNumericId(int id)
    {
        if (id < 0 || id >= items.size()) return null;
        return items.get(id);
    }

    /** @return The number of items in the registry. */
    public int size() {
        return items.size();
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        return items.iterator();
    }

    public interface ItemAddedCallback<T>
    {
        void onItemAdded(ActuallySimpleRegistry<T> registry, T item);
    }
}