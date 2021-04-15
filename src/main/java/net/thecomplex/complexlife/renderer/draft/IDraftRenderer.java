package net.thecomplex.complexlife.renderer.draft;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.thecomplex.complexlife.draft.IDraftEntity;

public interface IDraftRenderer<T> {
    public <M> void render(M destinationObject, Vector2f interBlockPosition, BlockState blockState, BlockPos blockPos, Direction side, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer);

}
