package sunsetsatellite.catalyst.multipart.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;
import sunsetsatellite.catalyst.Catalyst;
import sunsetsatellite.catalyst.CatalystMultipart;
import sunsetsatellite.catalyst.core.util.ScreenActionListener;
import sunsetsatellite.catalyst.core.util.Side;
import sunsetsatellite.catalyst.core.util.mp.BlockEntityUpdatePacket;
import sunsetsatellite.catalyst.multipart.api.MultipartType;

import java.util.ArrayList;
import java.util.List;

public class CarpenterWorkbenchBlockEntity extends BlockEntity implements Inventory, ScreenActionListener {

    public ItemStack[] contents = new ItemStack[2];
    public List<ItemStack> parts = new ArrayList<>();
    public int page = 1;
    public int maxPages = 1;
    public Side selectedSide = null;
    public int lastId = 0;

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

        if(contents[1] != null && contents[1].getItem() instanceof AxeItem && contents[1].count <= 0){
            contents[1] = null;
        }

        parts.clear();

        if(contents[0] != null && contents[0].getItem() instanceof BlockItem blockItem && CatalystMultipart.validBlocks.contains(blockItem.getBlock())){
            if(contents[1] != null && contents[1].getItem() instanceof AxeItem){
                for (MultipartType type : MultipartType.types.values()) {
                    ItemStack stack = new ItemStack(CatalystMultipart.multipartItem,16 / type.thickness, 0);
                    NbtCompound multipartTag = new NbtCompound();
                    multipartTag.putString("Type",type.name.toString());
                    multipartTag.putString("Block", Catalyst.getIdFromStack(contents[0]));
                    multipartTag.putInt("Meta", contents[0].getDamage());
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

        if(FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER){
            MinecraftServer server = (MinecraftServer) FabricLoader.getInstance().getGameInstance();
            List<ServerPlayNetworkHandler> list = server.connections.connections;
            list.forEach(handler -> handler.sendPacket(new BlockEntityUpdatePacket(this)));
        }
    }

    @Override
    public void readNbt(NbtCompound nbttagcompound) {
        super.readNbt(nbttagcompound);
        NbtList nbttaglist = nbttagcompound.getList("Items");
        this.contents = new ItemStack[this.size()];

        for(int i = 0; i < nbttaglist.size(); ++i) {
            NbtCompound nbttagcompound1 = (NbtCompound)nbttaglist.get(i);
            int j = nbttagcompound1.getByte("Slot") & 255;
            if (j >= 0 && j < this.contents.length) {
                this.contents[j] = new ItemStack(nbttagcompound1);
            }
        }

    }

    @Override
    public void writeNbt(NbtCompound nbttagcompound) {
        super.writeNbt(nbttagcompound);
        NbtList nbttaglist = new NbtList();

        for(int i = 0; i < this.contents.length; ++i) {
            if (this.contents[i] != null) {
                NbtCompound nbttagcompound1 = new NbtCompound();
                nbttagcompound1.putByte("Slot", (byte)i);
                this.contents[i].writeNbt(nbttagcompound1);
                nbttaglist.add(nbttagcompound1);
            }
        }

        nbttagcompound.put("Items", nbttaglist);
    }

}
