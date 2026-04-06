package sunsetsatellite.catalyst.multiblocks;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.World;
import sunsetsatellite.catalyst.CatalystMultiblocks;
import sunsetsatellite.catalyst.core.util.BlockInstance;
import sunsetsatellite.catalyst.core.util.Direction;
import sunsetsatellite.catalyst.core.util.vector.Vec3i;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class CustomStructure extends Structure {

    public World world;
    public boolean hasOrigin = false;

    public CustomStructure(String modId, String id, World world, boolean placeAir, boolean replaceBlocks) {
        super(modId, new Class[]{}, id, new NbtCompound(), placeAir, replaceBlocks);
        this.world = world;
        this.translateKey = id;
        loadFromNBT(id);
    }

    @Override
    public String getTranslatedName() {
        return data.getString("Name");
    }

    @Override
    public String getFullFilePath() {
        return world.storage.getWorldPropertiesFile("struct_" + translateKey).getPath();
    }

    @Override
    protected void loadFromNBT(String id) {
        try {
            File file = world.storage.getWorldPropertiesFile("struct_" + id);
            if (file == null) return;
            data = NbtIo.readCompressed(Files.newInputStream(file.toPath()));
            hasOrigin = data.contains("Origin");
        } catch (IOException e) {
            CatalystMultiblocks.LOGGER.error("Failed to load structure: {}", id);
            e.printStackTrace();
        }
    }

    @Override
    public BlockInstance getOrigin() {
        if(hasOrigin) super.getOrigin();
        return null;
    }

    @Override
    public BlockInstance getOrigin(Vec3i origin) {
        if(hasOrigin) super.getOrigin(origin);
        return null;
    }

    @Override
    public BlockInstance getOrigin(World world, Vec3i origin) {
        if(hasOrigin) super.getOrigin(world, origin);
        return null;
    }

    @Override
    public BlockInstance getOrigin(Vec3i origin, Direction dir) {
        if(hasOrigin) super.getOrigin(origin, dir);
        return null;
    }
}
