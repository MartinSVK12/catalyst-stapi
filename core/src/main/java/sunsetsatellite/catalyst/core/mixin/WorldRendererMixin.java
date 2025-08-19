package sunsetsatellite.catalyst.core.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.Box;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import sunsetsatellite.catalyst.core.util.section.SideInteractable;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Shadow
    private Minecraft client;

    @Redirect(method = "renderBlockOutline",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;renderOutline(Lnet/minecraft/util/math/Box;)V"))
    public void drawOutlinedSectionedBoundingBox(WorldRenderer instance, Box box, @Local(name = "var7") int blockId)
    {
        if(client.player.inventory.getSelectedItem() != null && client.player.inventory.getSelectedItem().getItem() instanceof SideInteractable && ((SideInteractable) client.player.inventory.getSelectedItem().getItem()).alwaysShowOutlineWhenHeld()){
            double minX = box.minX;
            double minY = box.minY;
            double minZ = box.minZ;
            double maxX = box.maxX;
            double maxY = box.maxY;
            double maxZ = box.maxZ;
            Tessellator tessellator = Tessellator.INSTANCE;

            tessellator.start(GL11.GL_LINE_STRIP);
            tessellator.vertex(minX, minY, minZ);
            tessellator.vertex(maxX, minY, minZ);
            tessellator.vertex(maxX, minY, maxZ);
            tessellator.vertex(minX, minY, maxZ);
            tessellator.vertex(minX, minY, minZ);
            tessellator.draw();

            tessellator.start(GL11.GL_LINE_STRIP);
            tessellator.vertex(minX, maxY, minZ);
            tessellator.vertex(maxX, maxY, minZ);
            tessellator.vertex(maxX, maxY, maxZ);
            tessellator.vertex(minX, maxY, maxZ);
            tessellator.vertex(minX, maxY, minZ);
            tessellator.draw();

            //bottom
            tessellator.start(GL11.GL_LINE_STRIP);
            tessellator.vertex(minX+0.3f, minY, minZ);
            tessellator.vertex(maxX-0.3f, minY, minZ);
            tessellator.vertex(maxX-0.3f, minY, maxZ);
            tessellator.vertex(minX+0.3f, minY, maxZ);
            tessellator.vertex(minX+0.3f, minY, minZ);
            tessellator.draw();

            tessellator.start(GL11.GL_LINE_STRIP);
            tessellator.vertex(minX, minY, minZ+0.3f);
            tessellator.vertex(maxX, minY, minZ+0.3f);
            tessellator.vertex(maxX, minY, maxZ-0.3f);
            tessellator.vertex(minX, minY, maxZ-0.3f);
            tessellator.vertex(minX, minY, minZ+0.3f);
            tessellator.draw();

            //top
            tessellator.start(GL11.GL_LINE_STRIP);
            tessellator.vertex(minX+0.3f, maxY, minZ);
            tessellator.vertex(maxX-0.3f, maxY, minZ);
            tessellator.vertex(maxX-0.3f, maxY, maxZ);
            tessellator.vertex(minX+0.3f, maxY, maxZ);
            tessellator.vertex(minX+0.3f, maxY, minZ);
            tessellator.draw();

            tessellator.start(GL11.GL_LINE_STRIP);
            tessellator.vertex(minX, maxY, minZ+0.3f);
            tessellator.vertex(maxX, maxY, minZ+0.3f);
            tessellator.vertex(maxX, maxY, maxZ-0.3f);
            tessellator.vertex(minX, maxY, maxZ-0.3f);
            tessellator.vertex(minX, maxY, minZ+0.3f);
            tessellator.draw();

            //sides
            tessellator.start(GL11.GL_LINE_STRIP);
            tessellator.vertex(minX, minY+0.3f, minZ);
            tessellator.vertex(maxX, minY+0.3f, minZ);
            tessellator.vertex(maxX, minY+0.3f, maxZ);
            tessellator.vertex(minX, minY+0.3f, maxZ);
            tessellator.vertex(minX, minY+0.3f, minZ);
            tessellator.draw();

            tessellator.start(GL11.GL_LINE_STRIP);
            tessellator.vertex(minX, maxY-0.3f, minZ);
            tessellator.vertex(maxX, maxY-0.3f, minZ);
            tessellator.vertex(maxX, maxY-0.3f, maxZ);
            tessellator.vertex(minX, maxY-0.3f, maxZ);
            tessellator.vertex(minX, maxY-0.3f, minZ);
            tessellator.draw();

            tessellator.start(GL11.GL_LINES);
            tessellator.vertex(minX+0.3f, minY, minZ);
            tessellator.vertex(minX+0.3f, maxY, minZ);
            tessellator.vertex(maxX-0.3f, minY, minZ);
            tessellator.vertex(maxX-0.3f, maxY, minZ);
            tessellator.vertex(minX+0.3f, minY, maxZ);
            tessellator.vertex(minX+0.3f, maxY, maxZ);
            tessellator.vertex(maxX-0.3f, minY, maxZ);
            tessellator.vertex(maxX-0.3f, maxY, maxZ);
            tessellator.draw();

            tessellator.start(GL11.GL_LINES);
            tessellator.vertex(minX, minY, minZ+0.3f);
            tessellator.vertex(minX, maxY, minZ+0.3f);
            tessellator.vertex(maxX, minY, minZ+0.3f);
            tessellator.vertex(maxX, maxY, minZ+0.3f);
            tessellator.vertex(minX, minY, maxZ-0.3f);
            tessellator.vertex(minX, maxY, maxZ-0.3f);
            tessellator.vertex(maxX, minY, maxZ-0.3f);
            tessellator.vertex(maxX, maxY, maxZ-0.3f);
            tessellator.draw();

            //cube outline
            tessellator.start(GL11.GL_LINES);
            tessellator.vertex(minX, minY, minZ);
            tessellator.vertex(minX, maxY, minZ);
            tessellator.vertex(maxX, minY, minZ);
            tessellator.vertex(maxX, maxY, minZ);
            tessellator.vertex(maxX, minY, maxZ);
            tessellator.vertex(maxX, maxY, maxZ);
            tessellator.vertex(minX, minY, maxZ);
            tessellator.vertex(minX, maxY, maxZ);
            tessellator.draw();
        }
        Block block = Block.BLOCKS[blockId];
        if(block instanceof SideInteractable sideInteractable && (!sideInteractable.needsItemToShowOutline()) || (client.player.inventory.getSelectedItem() != null && client.player.inventory.getSelectedItem().getItem() instanceof SideInteractable)){
            double minX = box.minX;
            double minY = box.minY;
            double minZ = box.minZ;
            double maxX = box.maxX;
            double maxY = box.maxY;
            double maxZ = box.maxZ;
            Tessellator tessellator = Tessellator.INSTANCE;

            tessellator.start(GL11.GL_LINE_STRIP);
            tessellator.vertex(minX, minY, minZ);
            tessellator.vertex(maxX, minY, minZ);
            tessellator.vertex(maxX, minY, maxZ);
            tessellator.vertex(minX, minY, maxZ);
            tessellator.vertex(minX, minY, minZ);
            tessellator.draw();

            tessellator.start(GL11.GL_LINE_STRIP);
            tessellator.vertex(minX, maxY, minZ);
            tessellator.vertex(maxX, maxY, minZ);
            tessellator.vertex(maxX, maxY, maxZ);
            tessellator.vertex(minX, maxY, maxZ);
            tessellator.vertex(minX, maxY, minZ);
            tessellator.draw();

            //bottom
            tessellator.start(GL11.GL_LINE_STRIP);
            tessellator.vertex(minX+0.3f, minY, minZ);
            tessellator.vertex(maxX-0.3f, minY, minZ);
            tessellator.vertex(maxX-0.3f, minY, maxZ);
            tessellator.vertex(minX+0.3f, minY, maxZ);
            tessellator.vertex(minX+0.3f, minY, minZ);
            tessellator.draw();

            tessellator.start(GL11.GL_LINE_STRIP);
            tessellator.vertex(minX, minY, minZ+0.3f);
            tessellator.vertex(maxX, minY, minZ+0.3f);
            tessellator.vertex(maxX, minY, maxZ-0.3f);
            tessellator.vertex(minX, minY, maxZ-0.3f);
            tessellator.vertex(minX, minY, minZ+0.3f);
            tessellator.draw();

            //top
            tessellator.start(GL11.GL_LINE_STRIP);
            tessellator.vertex(minX+0.3f, maxY, minZ);
            tessellator.vertex(maxX-0.3f, maxY, minZ);
            tessellator.vertex(maxX-0.3f, maxY, maxZ);
            tessellator.vertex(minX+0.3f, maxY, maxZ);
            tessellator.vertex(minX+0.3f, maxY, minZ);
            tessellator.draw();

            tessellator.start(GL11.GL_LINE_STRIP);
            tessellator.vertex(minX, maxY, minZ+0.3f);
            tessellator.vertex(maxX, maxY, minZ+0.3f);
            tessellator.vertex(maxX, maxY, maxZ-0.3f);
            tessellator.vertex(minX, maxY, maxZ-0.3f);
            tessellator.vertex(minX, maxY, minZ+0.3f);
            tessellator.draw();

            //sides
            tessellator.start(GL11.GL_LINE_STRIP);
            tessellator.vertex(minX, minY+0.3f, minZ);
            tessellator.vertex(maxX, minY+0.3f, minZ);
            tessellator.vertex(maxX, minY+0.3f, maxZ);
            tessellator.vertex(minX, minY+0.3f, maxZ);
            tessellator.vertex(minX, minY+0.3f, minZ);
            tessellator.draw();

            tessellator.start(GL11.GL_LINE_STRIP);
            tessellator.vertex(minX, maxY-0.3f, minZ);
            tessellator.vertex(maxX, maxY-0.3f, minZ);
            tessellator.vertex(maxX, maxY-0.3f, maxZ);
            tessellator.vertex(minX, maxY-0.3f, maxZ);
            tessellator.vertex(minX, maxY-0.3f, minZ);
            tessellator.draw();

            tessellator.start(GL11.GL_LINES);
            tessellator.vertex(minX+0.3f, minY, minZ);
            tessellator.vertex(minX+0.3f, maxY, minZ);
            tessellator.vertex(maxX-0.3f, minY, minZ);
            tessellator.vertex(maxX-0.3f, maxY, minZ);
            tessellator.vertex(minX+0.3f, minY, maxZ);
            tessellator.vertex(minX+0.3f, maxY, maxZ);
            tessellator.vertex(maxX-0.3f, minY, maxZ);
            tessellator.vertex(maxX-0.3f, maxY, maxZ);
            tessellator.draw();

            tessellator.start(GL11.GL_LINES);
            tessellator.vertex(minX, minY, minZ+0.3f);
            tessellator.vertex(minX, maxY, minZ+0.3f);
            tessellator.vertex(maxX, minY, minZ+0.3f);
            tessellator.vertex(maxX, maxY, minZ+0.3f);
            tessellator.vertex(minX, minY, maxZ-0.3f);
            tessellator.vertex(minX, maxY, maxZ-0.3f);
            tessellator.vertex(maxX, minY, maxZ-0.3f);
            tessellator.vertex(maxX, maxY, maxZ-0.3f);
            tessellator.draw();

            //cube outline
            tessellator.start(GL11.GL_LINES);
            tessellator.vertex(minX, minY, minZ);
            tessellator.vertex(minX, maxY, minZ);
            tessellator.vertex(maxX, minY, minZ);
            tessellator.vertex(maxX, maxY, minZ);
            tessellator.vertex(maxX, minY, maxZ);
            tessellator.vertex(maxX, maxY, maxZ);
            tessellator.vertex(minX, minY, maxZ);
            tessellator.vertex(minX, maxY, maxZ);
            tessellator.draw();
        } else {
            instance.renderOutline(box);
        }
    }

}
