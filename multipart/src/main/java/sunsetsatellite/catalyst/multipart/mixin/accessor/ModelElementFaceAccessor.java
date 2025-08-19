package sunsetsatellite.catalyst.multipart.mixin.accessor;

import net.modificationstation.stationapi.api.client.render.model.json.ModelElementFace;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ModelElementFace.class,remap = false)
public interface ModelElementFaceAccessor {

    @Mutable
    @Accessor
    void setTintIndex(int tintIndex);

}
