package net.thecomplex.complexlife;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.blaze3d.vertex.VertexBuilderUtils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.Color;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.thecomplex.complexlife.entity.EntityCopperCable;
import net.thecomplex.complexlife.misc.energy.ISocketBlock;
import net.thecomplex.complexlife.renderer.draft.IDraftRenderer;
import net.thecomplex.complexlife.renderer.draft.PlugDraftRenderer;
import net.thecomplex.complexlife.renderer.entity.model.ModelCablePlug;
import org.codehaus.plexus.util.FastMap;

import java.util.*;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientEventSubscriber {

    // TODO: Using Forge Registries
    private static Map<Class<?>, IDraftRenderer<?>> draftRenderers = new HashMap<>();
    static {
        draftRenderers.put(ISocketBlock.class, new PlugDraftRenderer());
    }

    @SubscribeEvent
    public static void onWorldLastRender(final RenderWorldLastEvent event) {
        RayTraceResult rtr = Minecraft.getInstance().hitResult;
        if(rtr.getType() != RayTraceResult.Type.BLOCK) return;
        BlockRayTraceResult blockRayTraceResult = (BlockRayTraceResult) rtr;
        Block b = Minecraft.getInstance().level.getBlockState(blockRayTraceResult.getBlockPos()).getBlock();

        IDraftRenderer<?> current = null;
        Class<?> cl = null;
        for(Map.Entry<Class<?>, IDraftRenderer<?>> entry : draftRenderers.entrySet()) {
            if(entry.getKey().isAssignableFrom(b.getClass())) {
                current = entry.getValue();
                cl = entry.getKey();
                break;
            }
        }
        if(current == null) return;

        Vector2f facePosition = null;
        float x;
        float y;
        switch (blockRayTraceResult.getDirection()) {
            case UP:
            case DOWN:
                x = Math.round((blockRayTraceResult.getLocation().x - blockRayTraceResult.getBlockPos().getX()) * 16);
                y = Math.round((blockRayTraceResult.getLocation().z - blockRayTraceResult.getBlockPos().getZ()) * 16);
                facePosition = new Vector2f(x, y);
                break;
            case NORTH:
            case SOUTH:
                x = Math.round((blockRayTraceResult.getLocation().x - blockRayTraceResult.getBlockPos().getX()) * 16);
                y = Math.round((blockRayTraceResult.getLocation().y - blockRayTraceResult.getBlockPos().getY()) * 16);
                facePosition = new Vector2f(x, y);
                break;
            case EAST:
            case WEST:
                x = Math.round((blockRayTraceResult.getLocation().z - blockRayTraceResult.getBlockPos().getZ()) * 16);
                y = Math.round((blockRayTraceResult.getLocation().y - blockRayTraceResult.getBlockPos().getY()) * 16);
                facePosition = new Vector2f(x, y);
                break;
        }

        Vector3d tr = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        MatrixStack matrixStack = event.getMatrixStack();
        matrixStack.pushPose();
        matrixStack.translate(-tr.x(), -tr.y(), -tr.z());
        current.render(cl.cast(b), facePosition, Minecraft.getInstance().level.getBlockState(blockRayTraceResult.getBlockPos()), blockRayTraceResult.getBlockPos(), blockRayTraceResult.getDirection(), event.getPartialTicks(), event.getMatrixStack(), Minecraft.getInstance().renderBuffers().bufferSource());
        matrixStack.popPose();
    }

    public static DynamicTexture texture;
    public static ResourceLocation loc;
    @SubscribeEvent
    public static void onGUIRender(final RenderGameOverlayEvent event) {
        /*MatrixStack matrixStack = event.getMatrixStack();

        //Screen.fill(matrixStack, 108, 108, 8, 8, 0xFFFFFFFF);
        NativeImage image = new NativeImage(100, 100, false);

        image.fillRect(0, 0, 50, 50, 0xFF00FFFF);
        for(int y = 0; y < 100; y++) {
            for(int x = 50; x < 100; x++) {
                image.setPixelRGBA(x, y, 0xFF0000FF);
            }
        }

        if(texture == null) {
            texture = new DynamicTexture(image);
            loc = Minecraft.getInstance().getTextureManager().register("networkmap", texture);
        }
        else texture.setPixels(image);

        Minecraft.getInstance().getTextureManager().bind(loc);
        Screen.blit(matrixStack, 8, 8, 0, 0, 0, 100, 100, 100, 100);*/
    }
}
