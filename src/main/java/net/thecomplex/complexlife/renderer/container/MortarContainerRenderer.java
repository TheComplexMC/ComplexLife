package net.thecomplex.complexlife.renderer.container;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.thecomplex.complexlife.inventory.container.ContainerMortar;

import java.awt.*;

public class MortarContainerRenderer extends ContainerScreen<ContainerMortar> {
    private ContainerMortar containerMortar;
    private PlayerInventory playerInventory;
    private final int sizeX = 176;
    private final int sizeY = 178;

    public MortarContainerRenderer(ContainerMortar containerMortar, PlayerInventory playerInventory, ITextComponent title) {
        super(containerMortar, playerInventory, title);

        this.containerMortar = containerMortar;
        this.playerInventory = playerInventory;
    }

    @Override
    protected void init() {
        super.init();

        /*this.addButton(new ImageButton(this.leftPos + 5, this.height / 2 - 49, 20, 18, 0, 0, 19, RECIPE_BUTTON_LOCATION, (p_214076_1_) -> {
            this.recipeBookComponent.initVisuals(false);
            this.recipeBookComponent.toggleVisibility();
            this.leftPos = this.recipeBookComponent.updateScreenPosition(false, this.width, this.imageWidth);
            ((ImageButton)p_214076_1_).setPosition(this.leftPos + 5, this.height / 2 - 49);
        }));*/
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) { }

    @Override
    public void renderBackground(MatrixStack matrixStack) {
        super.renderBackground(matrixStack);
        int edgeSpacingX = (this.width - this.sizeX) / 2;
        int edgeSpacingY = (this.height - this.sizeY) / 2;
        this.blit(matrixStack, edgeSpacingX, edgeSpacingY, 0, 0, this.sizeX, this.sizeY);
    }

    @Override
    public void render(MatrixStack matrixStack, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
        this.minecraft.getTextureManager().bind(TEXTURE);
        final int LABEL_XPOS = (this.width - this.sizeX) / 2;
        final int LABEL_YPOS = (this.width - this.sizeX) / 2;
        renderBackground(matrixStack);
        //this.font.draw(matrixStack, this.title, LABEL_XPOS, LABEL_YPOS, Color.darkGray.getRGB());     ///    this.font.drawString

        // draw the label for the player inventory slots
        this.font.draw(matrixStack, this.playerInventory.getDisplayName(),                  ///    this.font.drawString
                (this.width - this.sizeX) / 2, LABEL_YPOS + 105, Color.darkGray.getRGB());

        this.titleLabelX = 8;
        this.titleLabelY = 0;
        super.render(matrixStack, p_230430_2_, p_230430_3_, p_230430_4_);
    }

    private static final ResourceLocation TEXTURE = new ResourceLocation("complexlife", "textures/gui/mortar.png");
    private static final ResourceLocation RECIPE_BUTTON_LOCATION = new ResourceLocation("textures/gui/recipe_button.png");
}
