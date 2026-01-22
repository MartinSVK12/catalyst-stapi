package sunsetsatellite.catalyst.core.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.opengl.GL11;

public class TextHelper {
    public static int drawAdjustedText(int x, int y, String text, int color) {
        float size;
        int width = Minecraft.INSTANCE.textRenderer.getWidth(text);
        if(text.length() <= 4){
            size = 0.75f;
        } else if (text.length() == 5) {
            size = 0.6f;
        } else {
            size = 0.5f;
        }
        GL11.glPushMatrix();
        GL11.glTranslatef(x - width * size, y, 0);
        GL11.glScalef(size, size, 1.0F);
        Minecraft.INSTANCE.textRenderer.drawWithShadow(text, 0,0, color);
        GL11.glPopMatrix();
        return width / 2;
    }
}
