package net.thecomplex.complexlife;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.template.RuleTest;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.FolderName;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.thecomplex.complexlife.block.BlockManager;
import net.thecomplex.complexlife.entity.EntityCopperCable;
import net.thecomplex.complexlife.misc.energy.EnergyBlockSocket;
import net.thecomplex.complexlife.misc.energy.*;
import net.thecomplex.complexlife.misc.energy.network.EnergyNetworkManager;
import net.thecomplex.complexlife.misc.energy.network.NetworkEdge;

import java.io.*;
import java.util.*;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ServerEventSubscriber {
    @SubscribeEvent
    public static void onTick(final TickEvent.WorldTickEvent tickEvent) {
        if(tickEvent.side == LogicalSide.CLIENT) return;
        if(tickEvent.type == TickEvent.Type.WORLD) {
            switch (tickEvent.phase) {
                case START:
                    // Assigning the energy component block entities their
                    // access to the energy network
                    if(firstWorldTick) {
                        firstWorldTick = false;
                        World world = tickEvent.world;
                        Set<BlockPos> blockPoses = globalEnergyNetwork.getNodes().keySet();
                        for(BlockPos pos : blockPoses) {
                            TileEntity entity = world.getBlockEntity(pos);
                            if(entity == null) {
                                if (globalEnergyNetwork.getComponentType(pos) == EnergyNetworkManager.EnergyComponentType.PRODUCER) {
                                    globalEnergyNetwork.removeProducer(pos);
                                }
                                else {
                                    globalEnergyNetwork.removeConsumer(pos);
                                }
                                continue;
                            }
                            if (globalEnergyNetwork.getComponentType(pos) == EnergyNetworkManager.EnergyComponentType.PRODUCER) {
                                ((IEnergyProducer)entity).onEnergyInit(new EnergyProducingController(globalEnergyNetwork, pos));
                            }
                            else if (globalEnergyNetwork.getComponentType(pos) == EnergyNetworkManager.EnergyComponentType.CONSUMER) {
                                ((IEnergyConsumer)entity).onEnergyInit(new EnergyConsumptionController(globalEnergyNetwork, pos));
                            }
                        }
                        List<NetworkEdge> edges = globalEnergyNetwork.getEdges();
                        for(NetworkEdge edge : edges) {
                            BlockState blockStateA = world.getBlockState(edge.getNodeA().getBlockPos());
                            EnergyBlockSocket socketA = ((ISocketBlock)blockStateA.getBlock()).getSocket(blockStateA);
                            BlockState blockStateB = world.getBlockState(edge.getNodeB().getBlockPos());
                            EnergyBlockSocket socketB = ((ISocketBlock)blockStateB.getBlock()).getSocket(blockStateA);
                            EntityCopperCable cableEnd = EntityCopperCable.create(world, edge.getNodeB().getBlockPos(), null, socketB.getWorldPos(edge.getNodeB().getBlockPos()), socketB.getSide(), true);
                            EntityCopperCable cableStart = EntityCopperCable.create(world, edge.getNodeA().getBlockPos(), cableEnd, socketA.getWorldPos(edge.getNodeA().getBlockPos()), socketA.getSide(), false);
                            cableEnd.setHolder(cableStart);
                            cableStart.setHolder(cableEnd);
                        }

                    }
                    break;
                case END:
                    globalEnergyNetwork.updateNetworks();
                    break;
            }
        }
    }

    private static final EnergyNetworkManager globalEnergyNetwork = new EnergyNetworkManager();
    private static boolean firstWorldTick = true;

    @SubscribeEvent
    public static void onBlockPlaced(final BlockEvent.EntityPlaceEvent event) {
        if(event.getWorld().isClientSide()) return;
        TileEntity entity = event.getWorld().getBlockEntity(event.getPos());
        if(entity == null) return;
        if(entity instanceof IEnergyProducer) {
            ((IEnergyProducer) entity).onEnergyInit(new EnergyProducingController(globalEnergyNetwork, event.getPos()));
            globalEnergyNetwork.addProducer(event.getPos());
        }

        if(entity instanceof IEnergyConsumer) {
            ((IEnergyConsumer) entity).onEnergyInit(new EnergyConsumptionController(globalEnergyNetwork, event.getPos()));
            globalEnergyNetwork.addConsumer(event.getPos());
        }
    }

    public static void executeConnect(BlockPos posA, int socketAId, BlockPos posB, int socketBId) {
        globalEnergyNetwork.connect(posA, socketAId, posB, socketBId);
    }

    public static void executeDisconnect(BlockPos posA, BlockPos posB) {
        globalEnergyNetwork.disconnect(posA, posB);
    }

    public static void executeDisconnect(BlockPos pos) {
        globalEnergyNetwork.disconnectAll(pos);
    }


    @SubscribeEvent
    public static void onBlockBroke(final BlockEvent.BreakEvent event) {
        if(event.getWorld().isClientSide()) return;
        BlockState block = event.getWorld().getBlockState(event.getPos());
        ISocketBlock socketBlock = null;
        if(block.getBlock() instanceof ISocketBlock)
            socketBlock = (ISocketBlock)block.getBlock();

        if(socketBlock != null)
            if(socketBlock.getSocket(block).isOccupied())
                socketBlock.getSocket(block).getSocketConnector().disconnect(event.getPlayer());
        switch(globalEnergyNetwork.getComponentType(event.getPos()))
        {
            case NOTHING:
                break;
            case PRODUCER:
                globalEnergyNetwork.removeProducer(event.getPos());
                break;
            case CONSUMER:
                globalEnergyNetwork.removeConsumer(event.getPos());
                break;
        }
    }

    @SubscribeEvent
    public static void onSaveChunk(final WorldEvent.Save event) {
        if(event.getWorld().isClientSide()) return;
        try {
            File file = ((ServerWorld) event.getWorld()).getServer().getWorldPath(FolderName.ROOT).resolveSibling("data/energyinfo.dat").toFile();
            OutputStream stream = new FileOutputStream(file);
            DataOutput data = new DataOutputStream(stream);
            globalEnergyNetwork.write(data);
            stream.close();
        } catch(Exception ignored) { /* TODO ERROR HANDLING */ }
    }

    @SubscribeEvent
    public static void onLoad(final WorldEvent.Load event) {
        if(event.getWorld().isClientSide()) return;
        firstWorldTick = true;
        try {
            File file = ((ServerWorld) event.getWorld()).getServer().getWorldPath(FolderName.ROOT).resolveSibling("data/energyinfo.dat").toFile();
            if(!file.exists()) {
                if(!file.createNewFile())
                    throw new IOException("Cannot create energy info!"); // TODO BETTER ERROR HANDLING
                return;
            }
            InputStream stream = new FileInputStream(file);
            DataInput data = new DataInputStream(stream);
            globalEnergyNetwork.read(data);
            stream.close();
        } catch(Exception ignored) { /* TODO ERROR HANDLING*/ }
    }

    @SubscribeEvent
    public static void generateOres(final BiomeLoadingEvent event) {
        if(event.getCategory().equals(Biome.Category.NETHER) || event.getCategory().equals(Biome.Category.THEEND))
            return;

        RuleTest fillerType = OreFeatureConfig.FillerBlockType.NATURAL_STONE;
        BlockState state = BlockManager.COPPER_ORE.getBlock().defaultBlockState();
        int veinSize = 4;
        int countPerChunk = 20;

        event.getGeneration().addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.ORE.configured(new OreFeatureConfig(fillerType, state, veinSize)).range(64).squared().count(countPerChunk));
    }
}
