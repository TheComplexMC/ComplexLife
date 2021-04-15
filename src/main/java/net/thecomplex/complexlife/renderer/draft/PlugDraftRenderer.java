package net.thecomplex.complexlife.renderer.draft;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.thecomplex.complexlife.entity.EntityCopperCable;
import net.thecomplex.complexlife.item.ItemManager;
import net.thecomplex.complexlife.misc.energy.ISocketBlock;
import net.thecomplex.complexlife.renderer.entity.model.ModelCablePlug;

import java.util.LinkedHashMap;
import java.util.Map;

public class PlugDraftRenderer implements IDraftRenderer<ISocketBlock>{
    private static final ResourceLocation PLUG_LOCATION = new ResourceLocation("complexlife:textures/entity/plug.png");
    private static final ModelCablePlug<EntityCopperCable> modelPlug = new ModelCablePlug<>();

    private static final Map<Direction, Tuple<Float, Float>> directionRotationMap = new LinkedHashMap<Direction, Tuple<Float, Float>>(6);
    static {
        directionRotationMap.put(Direction.UP, new Tuple<>(0.0F, 0.0F));
        directionRotationMap.put(Direction.DOWN, new Tuple<>(180.0F, 0.0F));
        directionRotationMap.put(Direction.NORTH, new Tuple<>(90.0F, 180.0F));
        directionRotationMap.put(Direction.EAST, new Tuple<>(90.0F, 90.0F));
        directionRotationMap.put(Direction.SOUTH, new Tuple<>(90.0F, 0.0F));
        directionRotationMap.put(Direction.WEST, new Tuple<>(90.0F, 270.0F));
    }

    @Override
    public <T> void render(T destinationObject, Vector2f interBlockPosition, BlockState blockState, BlockPos blockPos, Direction side, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer) {
        if(side != ((ISocketBlock)destinationObject).getSocket(blockState).getSide()) return;
        if(((ISocketBlock)destinationObject).getSocket(blockState).isOccupied()) return;
        boolean flag = false;
        for(ItemStack stack : Minecraft.getInstance().player.getHandSlots()) {
            if(stack.getItem() == ItemManager.COPPER_CABLE)
                flag = true;
        }
        if(!flag) return;
        IVertexBuilder builder = renderTypeBuffer.getBuffer(modelPlug.renderType(PLUG_LOCATION));
        Vector3d pos = ((ISocketBlock)destinationObject).getSocket(blockState).getWorldPos(blockPos);
        matrixStack.translate(pos.x, pos.y, pos.z);
        modelPlug.setupAnim(null, 0, 0, 0, directionRotationMap.get(side).getB(), directionRotationMap.get(side).getA());
        modelPlug.renderToBuffer(matrixStack, builder, LightTexture.pack(15, 15), OverlayTexture.NO_OVERLAY, 0.15F, 1.0F, 0.2F, 1.0F);
    }
}
