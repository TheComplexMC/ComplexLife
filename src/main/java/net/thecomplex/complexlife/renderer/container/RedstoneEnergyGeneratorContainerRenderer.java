package net.thecomplex.complexlife.renderer.container;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.fixes.ItemSpawnEggSplit;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.ForgeHooksClient;
import net.thecomplex.complexlife.inventory.container.ContainerMortar;
import net.thecomplex.complexlife.inventory.container.ContainerRedstoneEnergyGenerator;
import net.thecomplex.complexlife.item.ItemManager;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.Random;

public class RedstoneEnergyGeneratorContainerRenderer extends ContainerScreen<ContainerRedstoneEnergyGenerator> {
    private ContainerRedstoneEnergyGenerator container;
    private final int WIDTH = 176;
    private final int HEIGHT = 166;

    private final int BATTERY_POSX = 8;
    private final int BATTERY_POSY = 45;
    private final int BATTERY_CONTENT_POSX = 176;
    private final int BATTERY_CONTENT_POSY = 32;
    private final int BATTERY_CONTENT_WIDTH = 16;
    private final int BATTERY_CONTENT_HEIGHT = 23;

    private final int REDSTONE_CONTENT_POSX = 176;
    private final int REDSTONE_CONTENT_POSY = 0;
    private final int REDSTONE_CONTENT_LENGTH = 16;

    private final int STONEDUST_CONTENT_POSX = 176;
    private final int STONEDUST_CONTENT_POSY = 16;
    private final int STONEDUST_CONTENT_LENGTH = 16;

    private final int GLASSFRONT_CONTENT_POSX = 176;
    private final int GLASSFRONT_CONTENT_POSY = 55;
    private final int GLASSFRONT_CONTENT_LENGTH = 16;

    public RedstoneEnergyGeneratorContainerRenderer(ContainerRedstoneEnergyGenerator container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
        this.container = container;
    }

    @Override
    protected void renderBg(MatrixStack p_230450_1_, float p_230450_2_, int p_230450_3_, int p_230450_4_) { }

    @Override
    public void render(MatrixStack matrixStack, int p_230430_2_, int p_230430_3_, float p_230430_4_) {

        renderBackground(matrixStack);
        super.render(matrixStack, p_230430_2_, p_230430_3_, p_230430_4_);
        renderTooltip(matrixStack, p_230430_2_, p_230430_3_);
    }

    @Override
    protected void renderTooltip(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.renderTooltip(matrixStack, mouseX, mouseY);
    }

    public void renderBackground(MatrixStack matrixStack) {
        super.renderBackground(matrixStack);
        minecraft.getTextureManager().bind(TEXTURE);

        int edgeSpacingX = (this.width - WIDTH) / 2;
        int edgeSpacingY = (this.height - HEIGHT) / 2;
        this.blit(matrixStack, edgeSpacingX, edgeSpacingY, 0, 0, WIDTH, HEIGHT);

        double energyStorageFill = container.getStateData().getEnergyStorageAmountDouble() / 1000D;
        float redstoneConsumingProgress = 0;
        if(container.getStateData().remainingFuelEnergyAmount > 0) {
            redstoneConsumingProgress =  (float)container.getStateData().remainingFuelEnergyAmount / container.getStateData().initialFuelEnergyAmount;
        }
        int offsetY = (int)((1.0 - energyStorageFill) * BATTERY_CONTENT_HEIGHT);
        this.blit(matrixStack, getGuiLeft() + BATTERY_POSX, getGuiTop() + BATTERY_POSY + offsetY,
                BATTERY_CONTENT_POSX, BATTERY_CONTENT_POSY + offsetY, BATTERY_CONTENT_WIDTH,
                BATTERY_CONTENT_HEIGHT - offsetY);

        renderFuelVisualizer(getGuiLeft() + 148, getGuiTop() + 13, 1 - redstoneConsumingProgress, redstoneConsumingProgress > 0);
    }

    /***
     * Renders the transition from redstone to stone dust as indicator for the remaining fuel.
     * Based on static images.
     * TODO renderFuelContainer(): renders the items used as fuel dynamic
     * @param x render position x
     * @param y render position y
     * @param progress fuel consumption progress
     * @param renderContent render indicator (true) or only the glass (false)
     */
    private void renderFuelVisualizer(int x, int y, float progress, boolean renderContent) {
        RenderSystem.pushMatrix();

        minecraft.getTextureManager().bind(TEXTURE);
        RenderSystem.enableRescaleNormal();
        RenderSystem.colorMask(true, true, true, false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableAlphaTest();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1 - progress);
        MatrixStack matrixStack = new MatrixStack();
        if(renderContent) {
            matrixStack.pushPose();
            this.blit(matrixStack, x, y, REDSTONE_CONTENT_POSX, REDSTONE_CONTENT_POSY, REDSTONE_CONTENT_LENGTH, REDSTONE_CONTENT_LENGTH);
            matrixStack.popPose();
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, progress);
            matrixStack.pushPose();
            this.blit(matrixStack, x, y, STONEDUST_CONTENT_POSX, STONEDUST_CONTENT_POSY, STONEDUST_CONTENT_LENGTH, STONEDUST_CONTENT_LENGTH);
            matrixStack.popPose();
        }
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        matrixStack.pushPose();
        this.blit(matrixStack, x, y, GLASSFRONT_CONTENT_POSX, GLASSFRONT_CONTENT_POSY, GLASSFRONT_CONTENT_LENGTH, GLASSFRONT_CONTENT_LENGTH);
        matrixStack.popPose();
        RenderSystem.disableAlphaTest();
        RenderSystem.disableRescaleNormal();
        RenderSystem.popMatrix();
    }

    /* TODO: Deprecated because not working */
    @Deprecated
    private void renderFuelContainer(ItemStack fuelItem, ItemStack consumedItem, int x, int y, float transitionProgress) {
        IBakedModel fuelItemModel = itemRenderer.getModel(fuelItem, null, null);

        RenderSystem.pushMatrix();
        minecraft.getTextureManager().bind(AtlasTexture.LOCATION_BLOCKS);
        minecraft.getTextureManager().getTexture(AtlasTexture.LOCATION_BLOCKS);

        RenderSystem.enableRescaleNormal();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.translatef((float)x, (float)y, 100.0F + 100.0F);
        RenderSystem.translatef(8.0F, 8.0F, 0.0F);
        RenderSystem.scalef(1.0F, -1.0F, 1.0F);
        RenderSystem.scalef(16.0F, 16.0F, 16.0F);
        MatrixStack newMatrixStack = new MatrixStack();
        IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().renderBuffers().bufferSource();
        boolean flag = !fuelItemModel.usesBlockLight();
        if (flag) {
            RenderHelper.setupForFlatItems();
        }
        ItemCameraTransforms.TransformType transformType = ItemCameraTransforms.TransformType.GUI;
        // Render START
        newMatrixStack.pushPose();
        fuelItemModel = ForgeHooksClient.handleCameraTransforms(newMatrixStack, fuelItemModel, transformType, false);
        newMatrixStack.translate(-0.5, -0.5, -0.5);
        if(!fuelItemModel.isCustomRenderer()) {
            RenderType renderType = RenderTypeLookup.getRenderType(fuelItem, true);
            IVertexBuilder builder = irendertypebuffer$impl.getBuffer(renderType);
            renderModelLists(fuelItemModel, fuelItem, 15728880, OverlayTexture.NO_OVERLAY, newMatrixStack, builder, transitionProgress);
        }
        else {
            fuelItem.getItem().getItemStackTileEntityRenderer().renderByItem(fuelItem, transformType, newMatrixStack, irendertypebuffer$impl, 15728880, OverlayTexture.NO_OVERLAY);
        }
        newMatrixStack.popPose();
        // Render END

        irendertypebuffer$impl.endBatch();
        RenderSystem.enableDepthTest();
        if(flag) {
            RenderHelper.setupFor3DItems();
        }

        RenderSystem.disableAlphaTest();
        RenderSystem.disableRescaleNormal();
        RenderSystem.popMatrix();
    }

    public void renderModelLists(IBakedModel p_229114_1_, ItemStack p_229114_2_, int p_229114_3_, int p_229114_4_, MatrixStack p_229114_5_, IVertexBuilder p_229114_6_, float transitionProgress) {
        Random random = new Random();
        long i = 42L;

        for(Direction direction : Direction.values()) {
            random.setSeed(42L);
            this.renderQuadList(p_229114_5_, p_229114_6_, p_229114_1_.getQuads((BlockState)null, direction, random), p_229114_2_, p_229114_3_, p_229114_4_, transitionProgress);
        }

        random.setSeed(42L);
        this.renderQuadList(p_229114_5_, p_229114_6_, p_229114_1_.getQuads((BlockState)null, (Direction)null, random), p_229114_2_, p_229114_3_, p_229114_4_, transitionProgress);
    }

    public void renderQuadList(MatrixStack matrixStack, IVertexBuilder builder, List<BakedQuad> quads, ItemStack itemStack, int lightMapCoord, int overlayColor, float transitionProgress) {
        boolean flag = !itemStack.isEmpty();
        MatrixStack.Entry matrixstack$entry = matrixStack.last();

        for (BakedQuad bakedquad : quads) {
            int i = -1;
            if (flag && bakedquad.isTinted()) {
                ItemColors itemColors = ItemColors.createDefault(BlockColors.createDefault());
                i = itemColors.getColor(itemStack, bakedquad.getTintIndex());
            }

            float f = (float) (i >> 16 & 255) / 255.0F;
            float f1 = (float) (i >> 8 & 255) / 255.0F;
            float f2 = (float) (i & 255) / 255.0F;
            builder.addVertexData(matrixstack$entry, bakedquad, f, f1, f2, transitionProgress, lightMapCoord, overlayColor, false);
        }
    }

    private static final ResourceLocation TEXTURE = new ResourceLocation("complexlife", "textures/gui/redstone_energy_generator.png");
}
