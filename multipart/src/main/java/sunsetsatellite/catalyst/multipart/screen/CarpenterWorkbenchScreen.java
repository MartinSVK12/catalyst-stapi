package sunsetsatellite.catalyst.multipart.screen;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerInventory;
import org.lwjgl.opengl.GL11;
import sunsetsatellite.catalyst.core.util.Side;
import sunsetsatellite.catalyst.multipart.block.entity.CarpenterWorkbenchBlockEntity;
import sunsetsatellite.catalyst.multipart.screen.handler.CarpenterWorkbenchScreenHandler;
import sunsetsatellite.catalyst.multipart.util.SlotPartPicker;

public class CarpenterWorkbenchScreen extends HandledScreen {
    public CarpenterWorkbenchBlockEntity tile;

    public CarpenterWorkbenchScreen(PlayerInventory inventoryPlayer, CarpenterWorkbenchBlockEntity tile) {
        super(new CarpenterWorkbenchScreenHandler(inventoryPlayer, tile));
        this.tile = tile;
    }

    @Override
    public void init() {
        super.init();
        for (Object slot : container.slots) {
            if (slot instanceof SlotPartPicker) {
                ((SlotPartPicker) slot).variableIndex = (((SlotPartPicker) slot).getSlotIndex()) + (9 * (tile.page - 1));
            }
        }
        buttons.add(new ButtonWidget(0, Math.round((float) width / 2 + 60), Math.round((float) height / 2 - 68), 20, 20, "/\\"));
        buttons.add(new ButtonWidget(1, Math.round((float) width / 2 + 60), Math.round((float) height / 2 - 34), 20, 20, "\\/"));
        buttons.add(new ButtonWidget(2, Math.round((float) width / 2 - 82), Math.round((float) height / 2 - 51), 20, 20, "*"));
        ((ButtonWidget) buttons.get(2)).active = false;
    }

    @Override
    protected void drawForeground() {
        this.textRenderer.draw("Carpenter Workbench", 32, 6, 0x404040);
        this.textRenderer.draw("Side", 6, 24, 0x404040);
        this.textRenderer.draw("Inventory", 8, this.backgroundHeight - 96 + 2, 4210752);

        this.textRenderer.draw(String.valueOf(tile.page), 150, 40, 0x404040);
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        if (!button.active) {
            return;
        }
        switch (button.id) {
            case 0:
                if (tile.page < tile.maxPages) {
                    tile.page++;
                    for (Object slot : container.slots) {
                        if (slot instanceof SlotPartPicker) {
                            ((SlotPartPicker) slot).variableIndex += 9;
                        }
                    }
                }
                break;
            case 1:
                if (tile.page > 1) {
                    tile.page--;
                    for (Object slot : container.slots) {
                        if (slot instanceof SlotPartPicker) {
                            ((SlotPartPicker) slot).variableIndex -= 9;
                        }
                    }
                }
                break;
            case 2:
                int i = this.tile.selectedSide.ordinal();
                i++;
                if(i >= Side.values().length) {
                    i = 0;
                }
                this.tile.selectedSide = Side.values()[i];
                if(this.tile.selectedSide == null) {
                    button.text = "*";
                    break;
                }
                button.text = String.valueOf(this.tile.selectedSide.name().charAt(0));
                break;
        }
        //if(EnvironmentHelper.isClientWorld()){
        //    NetworkHandler.sendToServer(new PacketScreenAction(guibutton.id,0,0,new Vec3i(tile.x, tile.y, tile.z), tile.getClass()));
        //}
    }

    @Override
    protected void drawBackground(float tickDelta) {
        int txt = this.minecraft.textureManager.getTextureId("/assets/catalyst-multipart/stationapi/textures/gui/carpenter_workbench.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.textureManager.bindTexture(txt);
        int j = (this.width - this.backgroundWidth) / 2;
        int k = (this.height - this.backgroundHeight) / 2;
        this.drawTexture(j, k, 0, 0, this.backgroundWidth, this.backgroundHeight);
    }
}
