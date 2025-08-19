package sunsetsatellite.catalyst.multipart.item;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.glasslauncher.mods.alwaysmoreitems.api.SubItemProvider;
import net.minecraft.block.Block;
import net.minecraft.block.LiquidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.client.item.CustomTooltipProvider;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;
import net.modificationstation.stationapi.api.registry.BlockRegistry;
import net.modificationstation.stationapi.api.template.item.TemplateItem;
import net.modificationstation.stationapi.api.util.Identifier;
import org.jetbrains.annotations.NotNull;
import sunsetsatellite.catalyst.Catalyst;
import sunsetsatellite.catalyst.CatalystMultipart;
import sunsetsatellite.catalyst.core.util.Direction;
import sunsetsatellite.catalyst.core.util.Side;
import sunsetsatellite.catalyst.core.util.section.BlockSection;
import sunsetsatellite.catalyst.core.util.section.SideInteractable;
import sunsetsatellite.catalyst.multipart.api.Multipart;
import sunsetsatellite.catalyst.multipart.api.MultipartType;
import sunsetsatellite.catalyst.multipart.api.SupportsMultiparts;
import sunsetsatellite.catalyst.multipart.block.MultipartRender;

import java.util.ArrayList;
import java.util.List;

public class MultipartItem extends TemplateItem implements MultipartRender, SideInteractable, CustomTooltipProvider {
    public MultipartItem(Identifier identifier) {
        super(identifier);
    }

    @Override
    public boolean useOnBlock(ItemStack stack, PlayerEntity user, World world, int x, int y, int z, int side) {

        if(world.isRemote){
            return true;
        }

        Multipart multipart = getMultipart(stack);
        if(multipart == null) return false;
        if (stack.count <= 0) {
            return false;
        }

        Pair<Direction, BlockSection> pair = Catalyst.getBlockSurfaceClickPosition(world, user, Side.values()[side], Catalyst.getClickPosition());
        Side playerFacing = Catalyst.calculatePlayerFacing(user.yaw);
        if (pairIsInvalid(pair)) return false;
        Direction dir = pair.getSecond().toDirection(pair.getFirst(), playerFacing);
        BlockEntity tile = world.getBlockEntity(x, y, z);

        if(tile instanceof SupportsMultiparts) {
            return addMultipart((SupportsMultiparts) tile, dir, stack, user, world, x, y, z, multipart, side);
        } else {
            return placeMultipart(tile, dir, stack, user, world, x, y, z, multipart, side);
        }
    }

    public boolean addMultipart(SupportsMultiparts tile, Direction dir, ItemStack stack, PlayerEntity user, World world, int x, int y, int z, Multipart multipart, int side) {
        if(tile.getParts().get(dir) != null){
            placeMultipart((BlockEntity) tile,dir,stack,user,world,x,y,z,multipart,side);
        }
        if(stack.count > 0){
            stack.count--;
            tile.getParts().putIfAbsent(dir, multipart);
            world.setBlockDirty(x,y,z);
            return true;
        }
        return false;
    }

    public boolean placeMultipart(BlockEntity tile, Direction dir, ItemStack stack, PlayerEntity user, World world, int x, int y, int z, Multipart multipart, int side) {
        if (!world.canPlace(CatalystMultipart.multipart.id,x,y,z,false,side)) {
            x += Direction.getDirectionFromSide(side).getVec().x;
            y += Direction.getDirectionFromSide(side).getVec().y;
            z += Direction.getDirectionFromSide(side).getVec().z;
        }

        Block currentBlock = world.getBlockState(x, y, z).getBlock();
        boolean placedInside = currentBlock != null && !(currentBlock instanceof LiquidBlock);
        if (y >= 0 && y < world.getHeight()) {
            if (world.canPlace(CatalystMultipart.multipart.id, x, y, z, false, side) && stack.count > 0) {
                stack.count--;
                Block block = CatalystMultipart.multipart;

                if (world.setBlock(x,y,z,block.id,0)) {
                    block.onPlaced(world, x, y, z, side);
                }

                tile = world.getBlockEntity(x, y, z);
                if(tile instanceof SupportsMultiparts multipartTile) {
                    multipartTile.getParts().putIfAbsent(dir, multipart);
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public Multipart getMultipart(ItemStack itemstack) {
        if(itemstack.getStationNbt() == null || !itemstack.getStationNbt().contains("Multipart")) return null;
        String type = itemstack.getStationNbt().getCompound("Multipart").getString("Type");

        if(type.isEmpty()) return null;
        Identifier id = Identifier.of(type);

        Block block = BlockRegistry.INSTANCE.get(Identifier.of(itemstack.getStationNbt().getCompound("Multipart").getString("Block")));
        int meta = itemstack.getStationNbt().getCompound("Multipart").getInt("Meta");
        boolean specifiedSideOnly = itemstack.getStationNbt().getCompound("Multipart").contains("Side");
        if (specifiedSideOnly) {
            String texture = itemstack.getStationNbt().getCompound("Multipart").getString("Texture");
            if(texture.isEmpty()) return null;
            Identifier textureId = Identifier.of(texture);
            Side side = Side.values()[itemstack.getStationNbt().getCompound("Multipart").getInt("Side")];
            if(block != null && MultipartType.types.containsKey(id)) {
                return new Multipart(MultipartType.types.get(id),block,textureId,side,meta);
            }
        }
        if(block != null && MultipartType.types.containsKey(id)) {
            List<Identifier> textures = new ArrayList<>();
            for (Side side : Side.values()) {
                String texture = itemstack.getStationNbt().getCompound("Multipart").getString("Texture_"+side.name().toLowerCase());
                if(texture.isEmpty()) return null;
                Identifier textureId = Identifier.of(texture);
                textures.add(textureId);
            }
            return new Multipart(MultipartType.types.get(id),block,textures.toArray(new Identifier[0]),meta);
        }
        return null;
    }

    private boolean pairIsInvalid(Pair<Direction, BlockSection> pair) {
        return pair == null || pair.getFirst() == null || pair.getSecond() == null;
    }

    /*@Environment(EnvType.CLIENT)
    @SubItemProvider
    public ArrayList<ItemStack> getMultiparts(){
        ArrayList<ItemStack> list = new ArrayList<>();
        CatalystMultipart.LOGGER.info(CatalystMultipart.validBlocks.size()+" valid blocks");
        for (MultipartType type : MultipartType.types.values()) {
            for (Block block : CatalystMultipart.validBlocks) {
                ItemStack stack = new ItemStack(CatalystMultipart.multipartItem,1, 0);
                NbtCompound multipartTag = new NbtCompound();
                multipartTag.putString("Type",type.name.toString());
                multipartTag.putString("Block", Catalyst.getIdFromBlock(block));
                multipartTag.putInt("Meta", 0);
                for (Side side : Side.values()) {
                    String texture = Atlases.getTerrain().getTexture(block.getTexture(side.ordinal(), 0)).getId().toString();
                    multipartTag.putString("Texture_"+side.name().toLowerCase(), texture);
                }
                stack.getStationNbt().put("Multipart",multipartTag);
                list.add(stack);
            }
        }
        return list;
    }*/

    @Override
    public @NotNull String[] getTooltip(ItemStack stack, String s) {
        Multipart multipart = getMultipart(stack);
        if(multipart == null) return new String[]{s};
        return new String[]{
                I18n.getTranslation(multipart.block.getTranslationKey()+".name") + " " + I18n.getTranslation("multipart."+multipart.type.model+".name")
        };
    }

}
