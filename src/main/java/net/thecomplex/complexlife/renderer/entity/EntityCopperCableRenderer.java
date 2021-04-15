package net.thecomplex.complexlife.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.LightType;
import net.thecomplex.complexlife.entity.EntityCopperCable;
import net.thecomplex.complexlife.renderer.entity.model.ModelCablePlug;

import java.util.LinkedHashMap;
import java.util.Map;

public class EntityCopperCableRenderer extends EntityRenderer<EntityCopperCable> {
    private static final ResourceLocation PLUG_LOCATION = new ResourceLocation("complexlife:textures/entity/plug.png");
    private final ModelCablePlug<EntityCopperCable> modelPlug = new ModelCablePlug<>();

    private static final Map<Direction, Tuple<Float, Float>> directionRotationMap = new LinkedHashMap<Direction, Tuple<Float, Float>>(6);
    static {
        directionRotationMap.put(Direction.UP, new Tuple<>(0.0F, 0.0F));
        directionRotationMap.put(Direction.DOWN, new Tuple<>(180.0F, 0.0F));
        directionRotationMap.put(Direction.NORTH, new Tuple<>(90.0F, 180.0F));
        directionRotationMap.put(Direction.EAST, new Tuple<>(90.0F, 90.0F));
        directionRotationMap.put(Direction.SOUTH, new Tuple<>(90.0F, 0.0F));
        directionRotationMap.put(Direction.WEST, new Tuple<>(90.0F, 270.0F));
    }

    public EntityCopperCableRenderer(EntityRendererManager renderManager) {
        super(renderManager);
    }

    @Override
    public boolean shouldRender(EntityCopperCable p_225626_1_, ClippingHelper p_225626_2_, double p_225626_3_, double p_225626_5_, double p_225626_7_) {
        return true;
    }

    public void render(EntityCopperCable entity, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer typeBuffer, int packedLightIn) {

        Entity holder = entity.getHolder();
        renderPlug(entity, partialTicks, matrixStack, typeBuffer, packedLightIn);
        if(!entity.IsHolder())
            renderCable(entity, partialTicks, matrixStack, typeBuffer, holder);

        super.render(entity, entityYaw, partialTicks, matrixStack, typeBuffer, packedLightIn);
    }

    private void renderPlug(EntityCopperCable entity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int packedLightIn) {
        matrixStack.pushPose();
        this.modelPlug.setupAnim(entity, 0, 0, 0, directionRotationMap.get(entity.getDirection()).getB(), directionRotationMap.get(entity.getDirection()).getA());
        IVertexBuilder ivertexbuilder = renderTypeBuffer.getBuffer(this.modelPlug.renderType(PLUG_LOCATION));
        this.modelPlug.renderToBuffer(matrixStack, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStack.popPose();
    }

    private <E extends Entity> void renderCable(EntityCopperCable entity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, E holder) {
        if(holder == null) return;

        float thickness = 0.025F;

        Vector3i directionVector = entity.getDirection().getNormal();

        matrixStack.pushPose();
        Vector3d holdingPosition;

        if(holder instanceof EntityCopperCable) {
            Vector3i v = ((EntityCopperCable)holder).getDirection().getNormal();
            holdingPosition = new Vector3d(holder.getX() + v.getX() * (3D/16D),holder.getY() + v.getY() * (3D/16D),holder.getZ() + v.getZ() * (3D/16D));
        }
        else {
            holdingPosition = holder.getRopeHoldPosition(partialTicks);
        }

        /*/ ???
        double d0 = (double)(MathHelper.lerp(partialTicks, 0, 0) * ((float)Math.PI / 180F)) + (Math.PI / 2D);
        Vector3d leashOffset = entity.getLeashOffset();
        double d1 = Math.cos(d0) * leashOffset.z + Math.sin(d0) * leashOffset.x;
        double d2 = Math.sin(d0) * leashOffset.z - Math.cos(d0) * leashOffset.x;
        */

        // Berechnung von zwischenpositionen bei Animationen. entity.?o origin-position; entity.get? ziel? position
        double entityX = MathHelper.lerp((double)partialTicks, entity.xo, entity.getX());// + d1;
        double entityY = MathHelper.lerp((double)partialTicks, entity.yo, entity.getY());// + vector3d1.y;
        double entityZ = MathHelper.lerp((double)partialTicks, entity.zo, entity.getZ());// + d2;

        //matrixStack.translate(d1, vector3d1.y, d2);
        // Plug top offset
        matrixStack.translate((directionVector.getX() * (3D/16D)), (directionVector.getY() * (3D/16D)), (directionVector.getZ() * (3D/16D)));

        // Vektor vom entity zum holder (Relative Holder Position)
        float relativeHolderPosX = (float)(holdingPosition.x - (entityX + (directionVector.getX() * (3D/16D))));
        float relativeHolderPosY = (float)(holdingPosition.y - (entityY + (directionVector.getY() * (3D/16D))));
        float relativeHolderPosZ = (float)(holdingPosition.z - (entityZ + (directionVector.getZ() * (3D/16D))));
        IVertexBuilder builder = renderTypeBuffer.getBuffer(RenderType.leash());
        Matrix4f matrix4f = matrixStack.last().pose();

        // Lightlevel calculation
        BlockPos entityBlockPos = new BlockPos(entity.getEyePosition(partialTicks));
        BlockPos holderBlockPos = new BlockPos(holder.getEyePosition(partialTicks));
        int entityLightLevel = this.getBlockLightLevel(entity, entityBlockPos);
        int holderLightLevel = 0;
        int entitySkyLightLevel = entity.level.getBrightness(LightType.SKY, entityBlockPos);
        int holderSkyLightLevel = entity.level.getBrightness(LightType.SKY, holderBlockPos);


        Vector3f entityToHolderVector = new Vector3f(relativeHolderPosX, relativeHolderPosY, relativeHolderPosZ);
        Vector3f thicknessLineXZ = Vector3f.YP.copy();
        thicknessLineXZ.cross(entityToHolderVector);
        thicknessLineXZ.normalize();

        //Vector3f thicknessLineY = thicknessLineXZ.copy();
        //thicknessLineY.cross(entityToHolderVector);
        //thicknessLineY.normalize();

        float thicknessOffsetX = thicknessLineXZ.x() * thickness;
        float thicknessOffsetZ = thicknessLineXZ.z() * thickness;
        // berechnung für variable y-Achse
        float thicknessOffsetY = 0.0F;

        // TOP: y is statisch
        renderCable(builder, matrix4f, entityToHolderVector, entityLightLevel, holderLightLevel, entitySkyLightLevel, holderSkyLightLevel, thickness, thicknessOffsetX, thicknessOffsetZ, thickness);

        matrixStack.popPose();
    }

    public void renderCable(IVertexBuilder builder, Matrix4f matrix, Vector3f entityToHolderVector, int entityLightLevel, int holderLightLevel, int entitySkyLightLevel, int holderSkyLightLevel, float thickness, float thicknessOffsetX, float thicknessOffsetZ, float offsetGamma1) {
        int i = 24;

        for(int j = 0; j < 24; ++j) {
            float f = (float)j / 23.0F;
            int positionEntityLightLevel = (int)MathHelper.lerp(f, (float)entityLightLevel, (float)holderLightLevel);
            int positionHolderLightLevel = (int)MathHelper.lerp(f, (float)entitySkyLightLevel, (float)holderSkyLightLevel);
            int packedLightLevel = LightTexture.pack(positionEntityLightLevel, positionHolderLightLevel);
            addVertexPairVertical(builder, matrix, packedLightLevel, entityToHolderVector, thickness, thicknessOffsetX, thicknessOffsetZ, offsetGamma1, 24, j, false, 1);
            addVertexPairVertical(builder, matrix, packedLightLevel, entityToHolderVector, thickness, thicknessOffsetX, thicknessOffsetZ, offsetGamma1, 24, j + 1, true, 1);
            addVertexPairVertical(builder, matrix, packedLightLevel, entityToHolderVector, thickness, thicknessOffsetX, thicknessOffsetZ, offsetGamma1, 24, j, false, -1);
            addVertexPairVertical(builder, matrix, packedLightLevel, entityToHolderVector, thickness, thicknessOffsetX, thicknessOffsetZ, offsetGamma1, 24, j + 1, true, -1);
            addVertexPairHorizontal(builder, matrix, packedLightLevel, entityToHolderVector, thickness, thicknessOffsetX, thicknessOffsetZ, offsetGamma1, 24, j, false, 1);
            addVertexPairHorizontal(builder, matrix, packedLightLevel, entityToHolderVector, thickness, thicknessOffsetX, thicknessOffsetZ, offsetGamma1, 24, j + 1, true, 1);
            addVertexPairHorizontal(builder, matrix, packedLightLevel, entityToHolderVector, thickness, thicknessOffsetX, thicknessOffsetZ, offsetGamma1, 24, j, false, -1);
            addVertexPairHorizontal(builder, matrix, packedLightLevel, entityToHolderVector, thickness, thicknessOffsetX, thicknessOffsetZ, offsetGamma1, 24, j + 1, true, -1);
        }
    }

    public void addVertexPairVertical(IVertexBuilder builder, Matrix4f matrix, int packedLightLevel, Vector3f entityToHolderVector, float thickness, float thicknessOffsetX, float thicknessOffsetZ, float offsetGamma1, int partCount, int currentPart, boolean isPartEnd, int direction) {
        float colorRed = 0.4F;
        float colorGreen = 0.3F;
        float colorBlue = 0.2F;

        float segment = (float)currentPart / (float)partCount;
        // relativeHolderPath ist der Vektor startend beim entity, zeigend auf den holder.
        // Das segment beschreibt die aktuelle position auf dieser linie.
        float segmentPosX = entityToHolderVector.x() * segment;
        float segmentPosY = entityToHolderVector.y() > 0.0F ? entityToHolderVector.y() * segment * segment : entityToHolderVector.y() - entityToHolderVector.y() * (1.0F - segment) * (1.0F - segment);
        float segmentPosZ = entityToHolderVector.z() * segment;


        if(!isPartEnd) {
            builder.vertex(matrix, segmentPosX + thicknessOffsetX, segmentPosY + direction * thickness, segmentPosZ + thicknessOffsetZ).color(colorRed, colorGreen, colorBlue, 1.0F).uv2(packedLightLevel).endVertex();
        }

        builder.vertex(matrix, segmentPosX - thicknessOffsetX, segmentPosY + direction * thickness, segmentPosZ - thicknessOffsetZ).color(colorRed, colorGreen, colorBlue, 1.0F).uv2(packedLightLevel).endVertex();

        if(isPartEnd) {
            builder.vertex(matrix, segmentPosX + thicknessOffsetX, segmentPosY + direction * thickness, segmentPosZ + thicknessOffsetZ).color(colorRed, colorGreen, colorBlue, 1.0F).uv2(packedLightLevel).endVertex();
        }
    }

    public void addVertexPairHorizontal(IVertexBuilder builder, Matrix4f matrix, int packedLightLevel, Vector3f entityToHolderVector, float thickness, float thicknessOffsetX, float thicknessOffsetZ, float offsetGamma1, int partCount, int currentPart, boolean isPartEnd, int direction) {
        float colorRed = 0.4F;
        float colorGreen = 0.3F;
        float colorBlue = 0.2F;

        float segment = (float)currentPart / (float)partCount;
        // relativeHolderPath ist der Vektor startend beim entity, zeigend auf den holder.
        // Das segment beschreibt die aktuelle position auf dieser linie.
        float segmentPosX = entityToHolderVector.x() * segment;
        float segmentPosY = entityToHolderVector.y() > 0.0F ? entityToHolderVector.y() * segment * segment : entityToHolderVector.y() - entityToHolderVector.y() * (1.0F - segment) * (1.0F - segment);
        float segmentPosZ = entityToHolderVector.z() * segment;


        if(!isPartEnd) {
            builder.vertex(matrix, segmentPosX + direction * thicknessOffsetX, segmentPosY + thickness, segmentPosZ + direction * thicknessOffsetZ).color(colorRed, colorGreen, colorBlue, 1.0F).uv2(packedLightLevel).endVertex();
        }

        builder.vertex(matrix, segmentPosX + direction * thicknessOffsetX, segmentPosY - thickness, segmentPosZ + direction * thicknessOffsetZ).color(colorRed, colorGreen, colorBlue, 1.0F).uv2(packedLightLevel).endVertex();

        if(isPartEnd) {
            builder.vertex(matrix, segmentPosX + direction * thicknessOffsetX, segmentPosY + thickness, segmentPosZ + direction * thicknessOffsetZ).color(colorRed, colorGreen, colorBlue, 1.0F).uv2(packedLightLevel).endVertex();
        }
    }

    public ResourceLocation getTextureLocation(EntityCopperCable p_110775_1_) {
        return PLUG_LOCATION;
    }
}
