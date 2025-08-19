package sunsetsatellite.catalyst.multipart.block.entity;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;
import sunsetsatellite.catalyst.Catalyst;
import sunsetsatellite.catalyst.CatalystMultipart;
import sunsetsatellite.catalyst.core.util.ScreenActionListener;
import sunsetsatellite.catalyst.core.util.Side;
import sunsetsatellite.catalyst.multipart.api.MultipartType;

import java.util.ArrayList;
import java.util.List;

public class CarpenterWorkbenchBlockEntity extends BlockEntity implements Inventory, ScreenActionListener {

    public ItemStack[] contents = new ItemStack[2];
    public List<ItemStack> parts = new ArrayList<>();
    public int page = 1;
    public int maxPages = 1;
    public Side selectedSide = null;

    @Override
    public int size() {
        return contents.length;
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.contents[slot];
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        if (this.contents[slot] != null) {
            ItemStack itemstack1;
            if (this.contents[slot].count <= amount) {
                itemstack1 = this.contents[slot];
                this.contents[slot] = null;
                this.markDirty();
                return itemstack1;
            } else {
                itemstack1 = this.contents[slot].split(amount);
                if (this.contents[slot].count <= 0) {
                    this.contents[slot] = null;
                }

                this.markDirty();
                return itemstack1;
            }
        } else {
            return null;
        }
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.contents[slot] = stack;
        if (stack != null && stack.count > this.getMaxCountPerStack()) {
            stack.count = this.getMaxCountPerStack();
        }

        this.markDirty();
    }

    @Override
    public String getName() {
        return "container.catalyst-multipart.carpenterWorkbench";
    }

    @Override
    public int getMaxCountPerStack() {
        return 64;
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        if (this.world.getBlockEntity(this.x, this.y, this.z) != this) {
            return false;
        } else {
            return player.getSquaredDistance((double)this.x + 0.5, (double)this.y + 0.5, (double)this.z + 0.5) <= 64.0;
        }
    }

    @Override
    public void buttonClicked(int id, int button, int channel) {
        switch (id) {
            case 0 -> {
                if (page < maxPages) {
                    page++;
                }
            }
            case 1 -> {
                if (page > 1) {
                    page--;
                }
            }
            case 2 -> {
                int i = selectedSide.ordinal();
                i++;
                if (i >= Side.values().length) {
                    i = 0;
                }
                selectedSide = Side.values()[i];
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        parts.clear();
        if(contents[1] != null && contents[1].getItem() instanceof AxeItem && contents[1].count <= 0){
            contents[1] = null;
        }

        if(contents[0] != null && CatalystMultipart.validBlocks.contains(Block.BLOCKS[contents[0].itemId])){
            if(contents[1] != null && contents[1].getItem() instanceof AxeItem){
                for (MultipartType type : MultipartType.types.values()) {
                    ItemStack stack = new ItemStack(CatalystMultipart.multipartItem,16 / type.thickness, 0);
                    NbtCompound multipartTag = new NbtCompound();
                    multipartTag.putString("Type",type.name.toString());
                    multipartTag.putString("Block", Catalyst.getIdFromStack(contents[0]));
                    multipartTag.putInt("Meta", contents[0].getDamage());
                    for (Side side : Side.values()) {
                        String texture = Atlases.getTerrain().getTexture(Block.BLOCKS[contents[0].itemId].getTexture(side.ordinal(), contents[0].getDamage())).getId().toString();
                        multipartTag.putString("Texture_"+side.name().toLowerCase(), texture);
                    }
                    stack.getStationNbt().put("Multipart",multipartTag);
                    parts.add(stack);
                }
                maxPages = (int) Math.ceil(parts.size() / 9f);
            } else {
                page = 1;
                maxPages = 1;
            }
        } else {
            page = 1;
            maxPages = 1;
        }
    }
}
