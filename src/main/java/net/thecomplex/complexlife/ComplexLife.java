package net.thecomplex.complexlife;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.datafix.fixes.RedstoneConnections;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.RegistryBuilder;
import net.thecomplex.complexlife.block.BlockManager;
import net.thecomplex.complexlife.config.Configuration;
import net.thecomplex.complexlife.crafting.serializer.RecipeSerializerManager;
import net.thecomplex.complexlife.entity.EntityCopperCable;
import net.thecomplex.complexlife.entity.EntityTypeManager;
import net.thecomplex.complexlife.entity.tileentity.TileEntityTypeManager;
import net.thecomplex.complexlife.renderer.container.RedstoneEnergyGeneratorContainerRenderer;
import net.thecomplex.complexlife.renderer.entity.EntityCopperCableRenderer;
import net.thecomplex.complexlife.inventory.container.ContainerManager;
import net.thecomplex.complexlife.renderer.container.MortarContainerRenderer;
import net.thecomplex.complexlife.item.ItemManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("complexlife")
public class ComplexLife
{
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public ComplexLife() {

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Configuration.CLIENT_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Configuration.COMMON_CONFIG);

        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        //Configuration.loadConfig(Configuration.CLIENT_CONFIG, FMLPaths.CONFIGDIR.get().resolve("mytutorial-client.toml"));
        Configuration.loadConfig(Configuration.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve("complexlife.toml"));

    }


    private void setup(final FMLCommonSetupEvent event)
    {
        ScreenManager.register(ContainerManager.MORTAR_TYPE, MortarContainerRenderer::new);
        ScreenManager.register(ContainerManager.REDSTONE_ENERGY_GENERATOR_TYPE, RedstoneEnergyGeneratorContainerRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypeManager.COPPER_CABLE, new EntityCopperCableRenderFactory());
    }

    private static class EntityCopperCableRenderFactory implements IRenderFactory<EntityCopperCable> {
        @Override
        public EntityRenderer<? super EntityCopperCable> createRenderFor(EntityRendererManager manager) {
            return new EntityCopperCableRenderer(manager);
        }
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {


        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            BlockManager.getInstance().RegisterBlocks(blockRegistryEvent.getRegistry());
        }

        @SubscribeEvent
        public static void onItemRegistry(final RegistryEvent.Register<Item> itemRegistryEvent) {
            BlockManager.getInstance().RegisterBlockItems(itemRegistryEvent.getRegistry());
            ItemManager.getInstance().RegisterItems(itemRegistryEvent.getRegistry());
        }

        @SubscribeEvent
        public static void onContainerRegistry(final RegistryEvent.Register<ContainerType<?>> containerRegistryEvent) {
            ContainerManager.getInstance().RegisterContainers(containerRegistryEvent.getRegistry());
        }

        @SubscribeEvent
        public static void onRecipeSerializerRegistry(final RegistryEvent.Register<IRecipeSerializer<?>> recipeSerializerRegistryEvent) {
            RecipeSerializerManager.getInstance().RegisterRecipeSerializer(recipeSerializerRegistryEvent.getRegistry());
        }

        @SubscribeEvent
        public static void onEntityTypeRegistry(final RegistryEvent.Register<EntityType<?>> entityTypeRegistryEvent) {
            EntityTypeManager.getInstance().registerEntityTypes(entityTypeRegistryEvent.getRegistry());
        }

        @SubscribeEvent
        public static void onTileEntityRegistry(final RegistryEvent.Register<TileEntityType<?>> tileEntityRegistryEvent) {
            TileEntityTypeManager.getInstance().registerTileEntityTypes(tileEntityRegistryEvent.getRegistry());
        }

        @SubscribeEvent
        public static void onNewRegistry(final RegistryEvent.NewRegistry newRegistryEvent) {
            
        }

    }
}
