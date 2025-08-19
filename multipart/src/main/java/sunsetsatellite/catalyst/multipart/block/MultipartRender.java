package sunsetsatellite.catalyst.multipart.block;

import net.minecraft.item.ItemStack;
import sunsetsatellite.catalyst.multipart.api.Multipart;

public interface MultipartRender {

    Multipart getMultipart(ItemStack stack);
}
