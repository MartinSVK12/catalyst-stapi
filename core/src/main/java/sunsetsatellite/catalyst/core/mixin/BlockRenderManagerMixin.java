package sunsetsatellite.catalyst.core.mixin;

import net.minecraft.block.Block;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlas;
import net.modificationstation.stationapi.api.world.BlockStateView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sunsetsatellite.catalyst.core.util.model.LayeredCubeModel;
import sunsetsatellite.catalyst.core.util.model.TextureLayer;

@Mixin(BlockRenderManager.class)
public abstract class BlockRenderManagerMixin {

    @Shadow
    private BlockView blockView;
    @Shadow
    private int textureOverride;
    @Shadow
    private boolean flipTextureHorizontally;
    @Shadow
    private boolean skipFaceCulling;
    @Shadow
    public static boolean fancyGraphics;
    @Shadow
    public boolean inventoryColorEnabled;
    @Shadow
    private int eastFaceRotation;
    @Shadow
    private int westFaceRotation;
    @Shadow
    private int southFaceRotation;
    @Shadow
    private int northFaceRotation;
    @Shadow
    private int topFaceRotation;
    @Shadow
    private int bottomFaceRotation;
    @Shadow
    private boolean useAo;
    @Shadow
    private float selfBrightness;
    @Shadow
    private float northBrightness;
    @Shadow
    private float bottomBrightness;
    @Shadow
    private float eastBrightness;
    @Shadow
    private float southBrightness;
    @Shadow
    private float topBrightness;
    @Shadow
    private float westBrightness;
    @Shadow
    private float northEastBottomBrightness;
    @Shadow
    private float northBottomBrightness;
    @Shadow
    private float northWestBottomBrightness;
    @Shadow
    private float eastBottomBrightness;
    @Shadow
    private float westBottomBrightness;
    @Shadow
    private float southEastBottomBrightness;
    @Shadow
    private float southBottomBrightness;
    @Shadow
    private float southWestBottomBrightness;
    @Shadow
    private float northEastTopBrightness;
    @Shadow
    private float northTopBrightness;
    @Shadow
    private float northWestTopBrightness;
    @Shadow
    private float eastTopBrightness;
    @Shadow
    private float southEastTopBrightness;
    @Shadow
    private float southTopBrightness;
    @Shadow
    private float westTopBrightness;
    @Shadow
    private float southWestTopBrightness;
    @Shadow
    private float northEastBrightness;
    @Shadow
    private float southEastBrightness;
    @Shadow
    private float northWestBrightness;
    @Shadow
    private float southWestBrightness;
    @Shadow
    private int useSurroundingBrightness;
    @Shadow
    private float firstVertexRed;
    @Shadow
    private float secondVertexRed;
    @Shadow
    private float thirdVertexRed;
    @Shadow
    private float fourthVertexRed;
    @Shadow
    private float firstVertexGreen;
    @Shadow
    private float secondVertexGreen;
    @Shadow
    private float thirdVertexGreen;
    @Shadow
    private float fourthVertexGreen;
    @Shadow
    private float firstVertexBlue;
    @Shadow
    private float secondVertexBlue;
    @Shadow
    private float thirdVertexBlue;
    @Shadow
    private float fourthVertexBlue;
    @Shadow
    private boolean topNorthEdgeTranslucent;
    @Shadow
    private boolean topEastEdgeTranslucent;
    @Shadow
    private boolean topWestEdgeTranslucent;
    @Shadow
    private boolean topSouthEdgeTranslucent;
    @Shadow
    private boolean northWestEdgeTranslucent;
    @Shadow
    private boolean southEastEdgeTranslucent;
    @Shadow
    private boolean southWestEdgeTranslucent;
    @Shadow
    private boolean northEastEdgeTranslucent;
    @Shadow
    private boolean bottomNorthEdgeTranslucent;
    @Shadow
    private boolean bottomEastEdgeTranslucent;
    @Shadow
    private boolean bottomWestEdgeTranslucent;
    @Shadow
    private boolean bottomSouthEdgeTranslucent;

    @Shadow
    public abstract void renderBottomFace(Block block, double x, double y, double z, int texture);

    @Shadow
    public abstract void renderTopFace(Block block, double x, double y, double z, int texture);

    @Shadow
    public abstract void renderEastFace(Block block, double x, double y, double z, int texture);

    @Shadow
    public abstract void renderWestFace(Block block, double x, double y, double z, int texture);

    @Shadow
    public abstract void renderNorthFace(Block block, double x, double y, double z, int texture);

    @Shadow
    public abstract void renderSouthFace(Block block, double x, double y, double z, int texture);

    @Unique
    boolean var8 = false;
    @Unique
    float var9 = this.selfBrightness;
    @Unique
    float var10 = this.selfBrightness;
    @Unique
    float var11 = this.selfBrightness;
    @Unique
    float var12 = this.selfBrightness;
    @Unique
    boolean var13 = true;
    @Unique
    boolean var14 = true;
    @Unique
    boolean var15 = true;
    @Unique
    boolean var16 = true;
    @Unique
    boolean var17 = true;
    @Unique
    boolean var18 = true;

    @Inject(method = "renderSmooth", at = @At("HEAD"), cancellable = true)
    public void renderMachine(Block block, int x, int y, int z, float red, float green, float blue, CallbackInfoReturnable<Boolean> cir) {
        if(block instanceof LayeredCubeModel model){
            setup(block, x, y, z);

            this.skipFaceCulling = false;

            TextureLayer[] textureLayers = model.getTextureLayers();
            for (int i = 0; i < textureLayers.length; i++) {
                int meta = this.blockView.getBlockMeta(x,y,z);
                if(!model.renderLayer(this.blockView, x, y, z, meta, i)) continue;
                if(model.isLayerFullbright(i)){
                    this.useSurroundingBrightness = 0;
                    this.topBrightness = 1;
                    this.northBrightness = 1;
                    this.southBrightness = 1;
                    this.westBrightness = 1;
                    this.eastBrightness = 1;
                    this.bottomBrightness = 1;
                    this.selfBrightness = 1;
                } else {
                    this.useSurroundingBrightness = 1;
                }
                BlockStateView blockStateView = (BlockStateView) this.blockView;
                Atlas.Sprite bottomTex = model.getLayerTexture(this.blockView, blockStateView, x, y, z, meta, 0, i);
                Atlas.Sprite topTex = model.getLayerTexture(this.blockView, blockStateView, x, y, z, meta, 1, i);
                Atlas.Sprite eastTex = model.getLayerTexture(this.blockView, blockStateView, x, y, z, meta, 2, i);
                Atlas.Sprite westTex = model.getLayerTexture(this.blockView, blockStateView, x, y, z, meta, 3, i);
                Atlas.Sprite northTex = model.getLayerTexture(this.blockView, blockStateView, x, y, z, meta, 4, i);
                Atlas.Sprite southTex = model.getLayerTexture(this.blockView, blockStateView, x, y, z, meta, 5, i);
                if(bottomTex != null) bottomFace(block, x, y, z, red, green, blue, 0, 0, 0, bottomTex.index);
                if(topTex != null) topFace(block, x, y, z, red, green, blue, 0, 0, 0, topTex.index);
                if(eastTex != null) eastFace(block, x, y, z, red, green, blue, 0, 0, 0, eastTex.index);
                if(westTex != null) westFace(block, x, y, z, red, green, blue, 0, 0, 0, westTex.index);
                if(northTex != null) northFace(block, x, y, z, red, green, blue, 0, 0, 0, northTex.index);
                if(southTex != null) southFace(block, x, y, z, red, green, blue, 0, 0, 0, southTex.index);
            }

            this.useAo = false;
            cir.setReturnValue(true);
        }
    }

    @Unique
    private void southFace(Block block, int x, int y, int z, float red, float green, float blue, double offsetX, double offsetY, double offsetZ, int textureId) {
        if (this.skipFaceCulling || block.isSideVisible(this.blockView, x + 1, y, z, 5)) {
            if (this.useSurroundingBrightness <= 0) {
                var9 = var10 = var11 = var12 = this.southBrightness;
            } else {
                this.southBottomBrightness = block.getLuminance(this.blockView, x + 1, y - 1, z);
                this.southEastBrightness = block.getLuminance(this.blockView, x + 1, y, z - 1);
                this.southWestBrightness = block.getLuminance(this.blockView, x + 1, y, z + 1);
                this.southTopBrightness = block.getLuminance(this.blockView, x + 1, y + 1, z);
                if (!this.bottomEastEdgeTranslucent && !this.northEastEdgeTranslucent) {
                    this.southEastBottomBrightness = this.southEastBrightness;
                } else {
                    this.southEastBottomBrightness = block.getLuminance(this.blockView, x + 1, y - 1, z - 1);
                }

                if (!this.bottomEastEdgeTranslucent && !this.southEastEdgeTranslucent) {
                    this.southWestBottomBrightness = this.southWestBrightness;
                } else {
                    this.southWestBottomBrightness = block.getLuminance(this.blockView, x + 1, y - 1, z + 1);
                }

                if (!this.topEastEdgeTranslucent && !this.northEastEdgeTranslucent) {
                    this.southEastTopBrightness = this.southEastBrightness;
                } else {
                    this.southEastTopBrightness = block.getLuminance(this.blockView, x + 1, y + 1, z - 1);
                }

                if (!this.topEastEdgeTranslucent && !this.southEastEdgeTranslucent) {
                    this.southWestTopBrightness = this.southWestBrightness;
                } else {
                    this.southWestTopBrightness = block.getLuminance(this.blockView, x + 1, y + 1, z + 1);
                }

                var9 = (this.southBottomBrightness + this.southWestBottomBrightness + this.southBrightness + this.southWestBrightness) / 4.0F;
                var12 = (this.southBrightness + this.southWestBrightness + this.southTopBrightness + this.southWestTopBrightness) / 4.0F;
                var11 = (this.southEastBrightness + this.southBrightness + this.southEastTopBrightness + this.southTopBrightness) / 4.0F;
                var10 = (this.southEastBottomBrightness + this.southBottomBrightness + this.southEastBrightness + this.southBrightness) / 4.0F;
            }

            this.firstVertexRed = this.secondVertexRed = this.thirdVertexRed = this.fourthVertexRed = (var18 ? red : 1.0F) * 0.6F;
            this.firstVertexGreen = this.secondVertexGreen = this.thirdVertexGreen = this.fourthVertexGreen = (var18 ? green : 1.0F) * 0.6F;
            this.firstVertexBlue = this.secondVertexBlue = this.thirdVertexBlue = this.fourthVertexBlue = (var18 ? blue : 1.0F) * 0.6F;
            this.firstVertexRed *= var9;
            this.firstVertexGreen *= var9;
            this.firstVertexBlue *= var9;
            this.secondVertexRed *= var10;
            this.secondVertexGreen *= var10;
            this.secondVertexBlue *= var10;
            this.thirdVertexRed *= var11;
            this.thirdVertexGreen *= var11;
            this.thirdVertexBlue *= var11;
            this.fourthVertexRed *= var12;
            this.fourthVertexGreen *= var12;
            this.fourthVertexBlue *= var12;
            this.renderSouthFace(block, x + offsetX, y + offsetY, z + offsetZ, textureId);

            var8 = true;
        }
    }

    @Unique
    private void northFace(Block block, int x, int y, int z, float red, float green, float blue, double offsetX, double offsetY, double offsetZ, int textureId) {
        if (this.skipFaceCulling || block.isSideVisible(this.blockView, x - 1, y, z, 4)) {
            if (this.useSurroundingBrightness <= 0) {
                var9 = var10 = var11 = var12 = this.northBrightness;
            } else {
                this.northBottomBrightness = block.getLuminance(this.blockView, x - 1, y - 1, z);
                this.northEastBrightness = block.getLuminance(this.blockView, x - 1, y, z - 1);
                this.northWestBrightness = block.getLuminance(this.blockView, x - 1, y, z + 1);
                this.northTopBrightness = block.getLuminance(this.blockView, x - 1, y + 1, z);
                if (!this.northWestEdgeTranslucent && !this.bottomWestEdgeTranslucent) {
                    this.northEastBottomBrightness = this.northEastBrightness;
                } else {
                    this.northEastBottomBrightness = block.getLuminance(this.blockView, x - 1, y - 1, z - 1);
                }

                if (!this.southWestEdgeTranslucent && !this.bottomWestEdgeTranslucent) {
                    this.northWestBottomBrightness = this.northWestBrightness;
                } else {
                    this.northWestBottomBrightness = block.getLuminance(this.blockView, x - 1, y - 1, z + 1);
                }

                if (!this.northWestEdgeTranslucent && !this.topWestEdgeTranslucent) {
                    this.northEastTopBrightness = this.northEastBrightness;
                } else {
                    this.northEastTopBrightness = block.getLuminance(this.blockView, x - 1, y + 1, z - 1);
                }

                if (!this.southWestEdgeTranslucent && !this.topWestEdgeTranslucent) {
                    this.northWestTopBrightness = this.northWestBrightness;
                } else {
                    this.northWestTopBrightness = block.getLuminance(this.blockView, x - 1, y + 1, z + 1);
                }

                var12 = (this.northBottomBrightness + this.northWestBottomBrightness + this.northBrightness + this.northWestBrightness) / 4.0F;
                var9 = (this.northBrightness + this.northWestBrightness + this.northTopBrightness + this.northWestTopBrightness) / 4.0F;
                var10 = (this.northEastBrightness + this.northBrightness + this.northEastTopBrightness + this.northTopBrightness) / 4.0F;
                var11 = (this.northEastBottomBrightness + this.northBottomBrightness + this.northEastBrightness + this.northBrightness) / 4.0F;
            }

            this.firstVertexRed = this.secondVertexRed = this.thirdVertexRed = this.fourthVertexRed = (var17 ? red : 1.0F) * 0.6F;
            this.firstVertexGreen = this.secondVertexGreen = this.thirdVertexGreen = this.fourthVertexGreen = (var17 ? green : 1.0F) * 0.6F;
            this.firstVertexBlue = this.secondVertexBlue = this.thirdVertexBlue = this.fourthVertexBlue = (var17 ? blue : 1.0F) * 0.6F;
            this.firstVertexRed *= var9;
            this.firstVertexGreen *= var9;
            this.firstVertexBlue *= var9;
            this.secondVertexRed *= var10;
            this.secondVertexGreen *= var10;
            this.secondVertexBlue *= var10;
            this.thirdVertexRed *= var11;
            this.thirdVertexGreen *= var11;
            this.thirdVertexBlue *= var11;
            this.fourthVertexRed *= var12;
            this.fourthVertexGreen *= var12;
            this.fourthVertexBlue *= var12;
            this.renderNorthFace(block, x + offsetX, y + offsetY, z + offsetZ, textureId);
            var8 = true;
        }
    }

    @Unique
    private void westFace(Block block, int x, int y, int z, float red, float green, float blue, double offsetX, double offsetY, double offsetZ, int textureId) {
        if (this.skipFaceCulling || block.isSideVisible(this.blockView, x, y, z + 1, 3)) {
            if (this.useSurroundingBrightness <= 0) {
                var9 = var10 = var11 = var12 = this.westBrightness;
            } else {
                this.northWestBrightness = block.getLuminance(this.blockView, x - 1, y, z + 1);
                this.southWestBrightness = block.getLuminance(this.blockView, x + 1, y, z + 1);
                this.westBottomBrightness = block.getLuminance(this.blockView, x, y - 1, z + 1);
                this.westTopBrightness = block.getLuminance(this.blockView, x, y + 1, z + 1);
                if (!this.southWestEdgeTranslucent && !this.bottomSouthEdgeTranslucent) {
                    this.northWestBottomBrightness = this.northWestBrightness;
                } else {
                    this.northWestBottomBrightness = block.getLuminance(this.blockView, x - 1, y - 1, z + 1);
                }

                if (!this.southWestEdgeTranslucent && !this.topSouthEdgeTranslucent) {
                    this.northWestTopBrightness = this.northWestBrightness;
                } else {
                    this.northWestTopBrightness = block.getLuminance(this.blockView, x - 1, y + 1, z + 1);
                }

                if (!this.southEastEdgeTranslucent && !this.bottomSouthEdgeTranslucent) {
                    this.southWestBottomBrightness = this.southWestBrightness;
                } else {
                    this.southWestBottomBrightness = block.getLuminance(this.blockView, x + 1, y - 1, z + 1);
                }

                if (!this.southEastEdgeTranslucent && !this.topSouthEdgeTranslucent) {
                    this.southWestTopBrightness = this.southWestBrightness;
                } else {
                    this.southWestTopBrightness = block.getLuminance(this.blockView, x + 1, y + 1, z + 1);
                }

                var9 = (this.northWestBrightness + this.northWestTopBrightness + this.westBrightness + this.westTopBrightness) / 4.0F;
                var12 = (this.westBrightness + this.westTopBrightness + this.southWestBrightness + this.southWestTopBrightness) / 4.0F;
                var11 = (this.westBottomBrightness + this.westBrightness + this.southWestBottomBrightness + this.southWestBrightness) / 4.0F;
                var10 = (this.northWestBottomBrightness + this.northWestBrightness + this.westBottomBrightness + this.westBrightness) / 4.0F;
            }

            this.firstVertexRed = this.secondVertexRed = this.thirdVertexRed = this.fourthVertexRed = (var16 ? red : 1.0F) * 0.8F;
            this.firstVertexGreen = this.secondVertexGreen = this.thirdVertexGreen = this.fourthVertexGreen = (var16 ? green : 1.0F) * 0.8F;
            this.firstVertexBlue = this.secondVertexBlue = this.thirdVertexBlue = this.fourthVertexBlue = (var16 ? blue : 1.0F) * 0.8F;
            this.firstVertexRed *= var9;
            this.firstVertexGreen *= var9;
            this.firstVertexBlue *= var9;
            this.secondVertexRed *= var10;
            this.secondVertexGreen *= var10;
            this.secondVertexBlue *= var10;
            this.thirdVertexRed *= var11;
            this.thirdVertexGreen *= var11;
            this.thirdVertexBlue *= var11;
            this.fourthVertexRed *= var12;
            this.fourthVertexGreen *= var12;
            this.fourthVertexBlue *= var12;
            this.renderWestFace(block, x + offsetX, y + offsetY, z + offsetZ, textureId);

            var8 = true;
        }
    }

    @Unique
    private void eastFace(Block block, int x, int y, int z, float red, float green, float blue, double offsetX, double offsetY, double offsetZ, int textureId) {
        if (this.skipFaceCulling || block.isSideVisible(this.blockView, x, y, z - 1, 2)) {
            if (this.useSurroundingBrightness <= 0) {
                var9 = var10 = var11 = var12 = this.eastBrightness;
            } else {
                this.northEastBrightness = block.getLuminance(this.blockView, x - 1, y, z - 1);
                this.eastBottomBrightness = block.getLuminance(this.blockView, x, y - 1, z - 1);
                this.eastTopBrightness = block.getLuminance(this.blockView, x, y + 1, z - 1);
                this.southEastBrightness = block.getLuminance(this.blockView, x + 1, y, z - 1);
                if (!this.northWestEdgeTranslucent && !this.bottomNorthEdgeTranslucent) {
                    this.northEastBottomBrightness = this.northEastBrightness;
                } else {
                    this.northEastBottomBrightness = block.getLuminance(this.blockView, x - 1, y - 1, z - 1);
                }

                if (!this.northWestEdgeTranslucent && !this.topNorthEdgeTranslucent) {
                    this.northEastTopBrightness = this.northEastBrightness;
                } else {
                    this.northEastTopBrightness = block.getLuminance(this.blockView, x - 1, y + 1, z - 1);
                }

                if (!this.northEastEdgeTranslucent && !this.bottomNorthEdgeTranslucent) {
                    this.southEastBottomBrightness = this.southEastBrightness;
                } else {
                    this.southEastBottomBrightness = block.getLuminance(this.blockView, x + 1, y - 1, z - 1);
                }

                if (!this.northEastEdgeTranslucent && !this.topNorthEdgeTranslucent) {
                    this.southEastTopBrightness = this.southEastBrightness;
                } else {
                    this.southEastTopBrightness = block.getLuminance(this.blockView, x + 1, y + 1, z - 1);
                }
                var9 = (this.northEastBrightness + this.northEastTopBrightness + this.eastBrightness + this.eastTopBrightness) / 4.0F;
                var10 = (this.eastBrightness + this.eastTopBrightness + this.southEastBrightness + this.southEastTopBrightness) / 4.0F;
                var11 = (this.eastBottomBrightness + this.eastBrightness + this.southEastBottomBrightness + this.southEastBrightness) / 4.0F;
                var12 = (this.northEastBottomBrightness + this.northEastBrightness + this.eastBottomBrightness + this.eastBrightness) / 4.0F;
            }

            this.firstVertexRed = this.secondVertexRed = this.thirdVertexRed = this.fourthVertexRed = (var15 ? red : 1.0F) * 0.8F;
            this.firstVertexGreen = this.secondVertexGreen = this.thirdVertexGreen = this.fourthVertexGreen = (var15 ? green : 1.0F) * 0.8F;
            this.firstVertexBlue = this.secondVertexBlue = this.thirdVertexBlue = this.fourthVertexBlue = (var15 ? blue : 1.0F) * 0.8F;
            this.firstVertexRed *= var9;
            this.firstVertexGreen *= var9;
            this.firstVertexBlue *= var9;
            this.secondVertexRed *= var10;
            this.secondVertexGreen *= var10;
            this.secondVertexBlue *= var10;
            this.thirdVertexRed *= var11;
            this.thirdVertexGreen *= var11;
            this.thirdVertexBlue *= var11;
            this.fourthVertexRed *= var12;
            this.fourthVertexGreen *= var12;
            this.fourthVertexBlue *= var12;
            this.renderEastFace(block, x + offsetX, y + offsetY, z + offsetZ, textureId);

            var8 = true;
        }
    }

    @Unique
    private void topFace(Block block, int x, int y, int z, float red, float green, float blue, double offsetX, double offsetY, double offsetZ, int textureId) {
        if (this.skipFaceCulling || block.isSideVisible(this.blockView, x, y + 1, z, 1)) {
            if (this.useSurroundingBrightness <= 0) {
                var9 = var10 = var11 = var12 = this.topBrightness;
            } else {
                this.northTopBrightness = block.getLuminance(this.blockView, x - 1, y + 1, z);
                this.southTopBrightness = block.getLuminance(this.blockView, x + 1, y + 1, z);
                this.eastTopBrightness = block.getLuminance(this.blockView, x, y + 1, z - 1);
                this.westTopBrightness = block.getLuminance(this.blockView, x, y + 1, z + 1);
                if (!this.topNorthEdgeTranslucent && !this.topWestEdgeTranslucent) {
                    this.northEastTopBrightness = this.northTopBrightness;
                } else {
                    this.northEastTopBrightness = block.getLuminance(this.blockView, x - 1, y + 1, z - 1);
                }

                if (!this.topNorthEdgeTranslucent && !this.topEastEdgeTranslucent) {
                    this.southEastTopBrightness = this.southTopBrightness;
                } else {
                    this.southEastTopBrightness = block.getLuminance(this.blockView, x + 1, y + 1, z - 1);
                }

                if (!this.topSouthEdgeTranslucent && !this.topWestEdgeTranslucent) {
                    this.northWestTopBrightness = this.northTopBrightness;
                } else {
                    this.northWestTopBrightness = block.getLuminance(this.blockView, x - 1, y + 1, z + 1);
                }

                if (!this.topSouthEdgeTranslucent && !this.topEastEdgeTranslucent) {
                    this.southWestTopBrightness = this.southTopBrightness;
                } else {
                    this.southWestTopBrightness = block.getLuminance(this.blockView, x + 1, y + 1, z + 1);
                }

                var12 = (this.northWestTopBrightness + this.northTopBrightness + this.westTopBrightness + this.topBrightness) / 4.0F;
                var9 = (this.westTopBrightness + this.topBrightness + this.southWestTopBrightness + this.southTopBrightness) / 4.0F;
                var10 = (this.topBrightness + this.eastTopBrightness + this.southTopBrightness + this.southEastTopBrightness) / 4.0F;
                var11 = (this.northTopBrightness + this.northEastTopBrightness + this.topBrightness + this.eastTopBrightness) / 4.0F;
            }

            this.firstVertexRed = this.secondVertexRed = this.thirdVertexRed = this.fourthVertexRed = var14 ? red : 1.0F;
            this.firstVertexGreen = this.secondVertexGreen = this.thirdVertexGreen = this.fourthVertexGreen = var14 ? green : 1.0F;
            this.firstVertexBlue = this.secondVertexBlue = this.thirdVertexBlue = this.fourthVertexBlue = var14 ? blue : 1.0F;
            this.firstVertexRed *= var9;
            this.firstVertexGreen *= var9;
            this.firstVertexBlue *= var9;
            this.secondVertexRed *= var10;
            this.secondVertexGreen *= var10;
            this.secondVertexBlue *= var10;
            this.thirdVertexRed *= var11;
            this.thirdVertexGreen *= var11;
            this.thirdVertexBlue *= var11;
            this.fourthVertexRed *= var12;
            this.fourthVertexGreen *= var12;
            this.fourthVertexBlue *= var12;
            this.renderTopFace(block, x + offsetX, y + offsetY, z + offsetZ, textureId);
            var8 = true;
        }
    }

    @Unique
    private void bottomFace(Block block, int x, int y, int z, float red, float green, float blue, double offsetX, double offsetY, double offsetZ, int textureId) {
        if (this.skipFaceCulling || block.isSideVisible(this.blockView, x, y - 1, z, 0)) {
            if (this.useSurroundingBrightness <= 0) {
                var9 = var10 = var11 = var12 = this.bottomBrightness;
            } else {
                this.northBottomBrightness = block.getLuminance(this.blockView, x - 1, y - 1, z);
                this.eastBottomBrightness = block.getLuminance(this.blockView, x, y - 1, z - 1);
                this.westBottomBrightness = block.getLuminance(this.blockView, x, y - 1, z + 1);
                this.southBottomBrightness = block.getLuminance(this.blockView, x + 1, y - 1, z);
                if (!this.bottomNorthEdgeTranslucent && !this.bottomWestEdgeTranslucent) {
                    this.northEastBottomBrightness = this.northBottomBrightness;
                } else {
                    this.northEastBottomBrightness = block.getLuminance(this.blockView, x - 1, y - 1, z - 1);
                }

                if (!this.bottomSouthEdgeTranslucent && !this.bottomWestEdgeTranslucent) {
                    this.northWestBottomBrightness = this.northBottomBrightness;
                } else {
                    this.northWestBottomBrightness = block.getLuminance(this.blockView, x - 1, y - 1, z + 1);
                }

                if (!this.bottomNorthEdgeTranslucent && !this.bottomEastEdgeTranslucent) {
                    this.southEastBottomBrightness = this.southBottomBrightness;
                } else {
                    this.southEastBottomBrightness = block.getLuminance(this.blockView, x + 1, y - 1, z - 1);
                }

                if (!this.bottomSouthEdgeTranslucent && !this.bottomEastEdgeTranslucent) {
                    this.southWestBottomBrightness = this.southBottomBrightness;
                } else {
                    this.southWestBottomBrightness = block.getLuminance(this.blockView, x + 1, y - 1, z + 1);
                }

                var9 = (this.northWestBottomBrightness + this.northBottomBrightness + this.westBottomBrightness + this.bottomBrightness) / 4.0F;
                var12 = (this.westBottomBrightness + this.bottomBrightness + this.southWestBottomBrightness + this.southBottomBrightness) / 4.0F;
                var11 = (this.bottomBrightness + this.eastBottomBrightness + this.southBottomBrightness + this.southEastBottomBrightness) / 4.0F;
                var10 = (this.northBottomBrightness + this.northEastBottomBrightness + this.bottomBrightness + this.eastBottomBrightness) / 4.0F;
            }

            this.firstVertexRed = this.secondVertexRed = this.thirdVertexRed = this.fourthVertexRed = (var13 ? red : 1.0F) * 0.5F;
            this.firstVertexGreen = this.secondVertexGreen = this.thirdVertexGreen = this.fourthVertexGreen = (var13 ? green : 1.0F) * 0.5F;
            this.firstVertexBlue = this.secondVertexBlue = this.thirdVertexBlue = this.fourthVertexBlue = (var13 ? blue : 1.0F) * 0.5F;
            this.firstVertexRed *= var9;
            this.firstVertexGreen *= var9;
            this.firstVertexBlue *= var9;
            this.secondVertexRed *= var10;
            this.secondVertexGreen *= var10;
            this.secondVertexBlue *= var10;
            this.thirdVertexRed *= var11;
            this.thirdVertexGreen *= var11;
            this.thirdVertexBlue *= var11;
            this.fourthVertexRed *= var12;
            this.fourthVertexGreen *= var12;
            this.fourthVertexBlue *= var12;
            this.renderBottomFace(block, x + offsetX, y + offsetY, z + offsetZ, textureId);
            var8 = true;
        }
    }

    @Unique
    private void setup(Block block, int x, int y, int z) {
        this.useAo = true;
        this.selfBrightness = block.getLuminance(this.blockView, x, y, z);
        this.northBrightness = block.getLuminance(this.blockView, x - 1, y, z);
        this.bottomBrightness = block.getLuminance(this.blockView, x, y - 1, z);
        this.eastBrightness = block.getLuminance(this.blockView, x, y, z - 1);
        this.southBrightness = block.getLuminance(this.blockView, x + 1, y, z);
        this.topBrightness = block.getLuminance(this.blockView, x, y + 1, z);
        this.westBrightness = block.getLuminance(this.blockView, x, y, z + 1);
        this.topEastEdgeTranslucent = Block.BLOCKS_ALLOW_VISION[this.blockView.getBlockId(x + 1, y + 1, z)];
        this.bottomEastEdgeTranslucent = Block.BLOCKS_ALLOW_VISION[this.blockView.getBlockId(x + 1, y - 1, z)];
        this.southEastEdgeTranslucent = Block.BLOCKS_ALLOW_VISION[this.blockView.getBlockId(x + 1, y, z + 1)];
        this.northEastEdgeTranslucent = Block.BLOCKS_ALLOW_VISION[this.blockView.getBlockId(x + 1, y, z - 1)];
        this.topWestEdgeTranslucent = Block.BLOCKS_ALLOW_VISION[this.blockView.getBlockId(x - 1, y + 1, z)];
        this.bottomWestEdgeTranslucent = Block.BLOCKS_ALLOW_VISION[this.blockView.getBlockId(x - 1, y - 1, z)];
        this.northWestEdgeTranslucent = Block.BLOCKS_ALLOW_VISION[this.blockView.getBlockId(x - 1, y, z - 1)];
        this.southWestEdgeTranslucent = Block.BLOCKS_ALLOW_VISION[this.blockView.getBlockId(x - 1, y, z + 1)];
        this.topSouthEdgeTranslucent = Block.BLOCKS_ALLOW_VISION[this.blockView.getBlockId(x, y + 1, z + 1)];
        this.topNorthEdgeTranslucent = Block.BLOCKS_ALLOW_VISION[this.blockView.getBlockId(x, y + 1, z - 1)];
        this.bottomSouthEdgeTranslucent = Block.BLOCKS_ALLOW_VISION[this.blockView.getBlockId(x, y - 1, z + 1)];
        this.bottomNorthEdgeTranslucent = Block.BLOCKS_ALLOW_VISION[this.blockView.getBlockId(x, y - 1, z - 1)];
        if (block.textureId == 3) {
            var18 = false;
            var17 = false;
            var16 = false;
            var15 = false;
            var13 = false;
        }

        if (this.textureOverride >= 0) {
            var18 = false;
            var17 = false;
            var16 = false;
            var15 = false;
            var13 = false;
        }
    }

}
