package sunsetsatellite.catalyst;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.fabricmc.loader.impl.launch.FabricLauncher;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.event.mod.InitEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.registry.BlockRegistry;
import net.modificationstation.stationapi.api.registry.ItemRegistry;
import net.modificationstation.stationapi.api.util.Namespace;
import net.modificationstation.stationapi.api.util.SideUtil;
import net.modificationstation.stationapi.api.world.StationFlatteningWorld;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.UnmodifiableView;
import sunsetsatellite.catalyst.core.util.Direction;
import sunsetsatellite.catalyst.core.util.Side;
import sunsetsatellite.catalyst.core.util.section.BlockSection;
import sunsetsatellite.catalyst.core.util.vector.Vec2f;
import sunsetsatellite.catalyst.core.util.vector.Vec3f;
import sunsetsatellite.catalyst.core.util.vector.Vec3i;

import java.util.*;

public class Catalyst {

    @Entrypoint.Namespace
    public static Namespace NAMESPACE;

    @Entrypoint.Logger
    public static Logger LOGGER;

    @EventListener
    public void onInit(InitEvent event) {
        LOGGER.info("Catalyst initialized!");
    }

    public static BlockEntity getBlockEntity(Direction dir, BlockView world, BlockEntity origin) {
        return world.getBlockEntity(origin.x + dir.getVec().x, origin.y + dir.getVec().y, origin.z + dir.getVec().z);
    }

    public static Block getBlock(Direction dir, StationFlatteningWorld world, BlockEntity origin) {
        return world.getBlockState(origin.x + dir.getVec().x, origin.y + dir.getVec().y, origin.z + dir.getVec().z).getBlock();
    }

    public static BlockEntity getBlockEntity(Direction dir, BlockView world, BlockPos origin) {
        return world.getBlockEntity(origin.x + dir.getVec().x, origin.y + dir.getVec().y, origin.z + dir.getVec().z);
    }

    public static BlockEntity getBlockEntity(Direction dir, BlockView world, Vec3i origin) {
        return world.getBlockEntity(origin.x + dir.getVec().x, origin.y + dir.getVec().y, origin.z + dir.getVec().z);
    }

    public static BlockEntity getBlockEntity(BlockView world, Vec3i position) {
        return world.getBlockEntity(position.x, position.y, position.z);
    }

    public static Block getBlock(Direction dir, StationFlatteningWorld world, BlockPos origin) {
        return world.getBlockState(origin.x + dir.getVec().x, origin.y + dir.getVec().y, origin.z + dir.getVec().z).getBlock();
    }

    public static ArrayList<ItemStack> condenseItemList(List<ItemStack> list) {
        ArrayList<ItemStack> stacks = new ArrayList<>();
        for (ItemStack stack : list) {
            if (stack != null) {
                boolean found = false;
                for (ItemStack S : stacks) {
                    if (S.isItemEqual(stack) && (S.getStationNbt().equals(stack.getStationNbt()))) {
                        S.count += stack.count;
                        found = true;
                    }
                }
                if (!found) stacks.add(stack.copy());
            }
        }
        return stacks;
    }

    public static @UnmodifiableView List<ItemStack> collectStacks(Inventory inv) {
        if (inv == null) return Collections.emptyList();
        ArrayList<ItemStack> stacks = new ArrayList<>();

        for (int i = 0; i < inv.size(); i++) {
            stacks.add(i, inv.getStack(i));
        }

        return Collections.unmodifiableList(stacks);
    }

    public static @UnmodifiableView List<ItemStack> collectAndCondenseStacks(Inventory inv) {
        return condenseItemList(collectStacks(inv));
    }

    public static ArrayList<ItemStack> condenseList(List<ItemStack> list){
        ArrayList<ItemStack> stacks = new ArrayList<>();
        for (ItemStack stack : list) {
            if(stack != null){
                Optional<ItemStack> existing = stacks.stream().filter((S) -> S.isItemEqual(stack)).findAny();
                if (existing.isPresent()) {
                    existing.get().count += stack.count;
                } else {
                    stacks.add(stack.copy());
                }
            }
        }
        return stacks;
    }

    /**
     * Maps a value from one range to another.
     *
     * @return The resuling value after being mapped from one range to another
     */
    public static double map(double value,
                             double fromMin, double fromMax,
                             double toMin, double toMax) {

        final double EPSILON = 1e-12;
        if (Math.abs(fromMax - fromMin) < EPSILON) {
            throw new ArithmeticException("Division by 0");
        }

        double ratio = (toMax - toMin) / (fromMax - fromMin);
        return ratio * (value - fromMin) + toMin;
    }

    public static String getIdFromStack(ItemStack stack){
        if(stack.getItem() instanceof BlockItem){
            return BlockRegistry.INSTANCE.getId(((BlockItem) stack.getItem()).getBlock()).toString();
        } else {
            return ItemRegistry.INSTANCE.getId(stack.getItem()).toString();
        }
    }

    public static String getIdFromBlock(Block block){
        return BlockRegistry.INSTANCE.getId(block).toString();
    }

    public static String getIdFromItem(Item item){
        return ItemRegistry.INSTANCE.getId(item).toString();
    }

    public static <K,V> Map<K,V> mapOf(K[] keys, V[] values){
        if(keys.length != values.length){
            throw new IllegalArgumentException("Arrays differ in size!");
        }
        HashMap<K,V> map = new HashMap<>();
        for (int i = 0; i < keys.length; i++) {
            map.put(keys[i],values[i]);
        }
        return map;
    }

    public static <T,V> T[] arrayFill(T[] array,V value){
        Arrays.fill(array,value);
        return array;
    }

    public static Pair<Direction, BlockSection> getBlockSurfaceClickPosition(World world, PlayerEntity player, Side side, Vec2f clickPosition){
        Direction dir = Direction.getDirectionFromSide(side.ordinal());
        return Pair.of(dir,BlockSection.getClosestBlockSection(clickPosition));
    }

    public static Side calculatePlayerFacing(float rotation) {
        return Side.values()[(2 + ((MathHelper.floor((double) ((rotation * 4F) / 360F) + 0.5D) + 2) & 3))];
    }

    public static Vec2f getClickPosition(){
        if(FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER){
            return new Vec2f();
        }
        HitResult hit = Minecraft.INSTANCE.crosshairTarget;
        if (hit.type == HitResultType.BLOCK) {
            Vec3f vec3f = new Vec3f(hit.pos.x, hit.pos.y, hit.pos.z);
            Vec2f clickPosition = vec3f.subtract(vec3f.copy().floor()).abs().set(Direction.getDirectionFromSide(hit.side).getAxis(), 0).toVec2f();
            switch (Side.values()[hit.side]) {
                case NORTH:
                    clickPosition.x = 1 - clickPosition.x;
                    break;
                case EAST: {
                    double temp1 = clickPosition.y;
                    double temp2 = clickPosition.x;
                    clickPosition.x = 1 - temp1;
                    clickPosition.y = temp2;
                    break;
                }
                case SOUTH:
                    //no change needed
                    break;
                case WEST: {
                    double temp1 = clickPosition.y;
                    double temp2 = clickPosition.x;
                    clickPosition.x = temp1;
                    clickPosition.y = temp2;
                    break;
                }
            }
            return clickPosition;
        }
        return new Vec2f();
    }
}
