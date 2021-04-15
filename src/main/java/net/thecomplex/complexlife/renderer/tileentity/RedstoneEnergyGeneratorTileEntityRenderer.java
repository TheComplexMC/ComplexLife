package net.thecomplex.complexlife.renderer.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.thecomplex.complexlife.entity.tileentity.TileEntityRedstoneEnergyGenerator;

@Deprecated
public class RedstoneEnergyGeneratorTileEntityRenderer extends TileEntityRenderer<TileEntityRedstoneEnergyGenerator> {
    public RedstoneEnergyGeneratorTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcher) {
        super(rendererDispatcher);
    }

    @Override
    public void render(TileEntityRedstoneEnergyGenerator entity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int p_225616_5_, int p_225616_6_) {

    }
}
