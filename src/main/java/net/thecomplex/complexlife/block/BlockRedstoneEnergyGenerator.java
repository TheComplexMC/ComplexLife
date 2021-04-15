package net.thecomplex.complexlife.block;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.network.NetworkHooks;
import net.thecomplex.complexlife.entity.tileentity.TileEntityRedstoneEnergyGenerator;
import net.thecomplex.complexlife.misc.energy.EnergyBlockSocket;

import javax.annotation.Nullable;

public class BlockRedstoneEnergyGenerator extends EnergyComponentContainerBlock {
    public static final DirectionProperty FACING = HorizontalBlock.FACING;

    public static int getEnergyAmountFromItem(ItemStack itemStack) {
        if(itemStack.getItem() == Items.REDSTONE) {
            // Hält eine ingame stunde
            return 1000;
        }
        return -1;
    }

    public BlockRedstoneEnergyGenerator() {
        super(Properties.of(Material.METAL).strength(3.5F).sound(SoundType.METAL).harvestTool(ToolType.PICKAXE).harvestLevel(1), new EnergyBlockSocket(0, Direction.SOUTH, 7, 7));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }


    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return newBlockEntity(world);
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(IBlockReader world) {
        return new TileEntityRedstoneEnergyGenerator();
    }

    @Override
    public ActionResultType use(BlockState blockState, World world, BlockPos blockPos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult) {
        if(world.isClientSide) return ActionResultType.SUCCESS;

        INamedContainerProvider containerProvider = getMenuProvider(blockState, world, blockPos);
        if(containerProvider != null) {
            if(!(player instanceof ServerPlayerEntity)) return ActionResultType.FAIL;
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
            NetworkHooks.openGui(serverPlayer, containerProvider, (packetBuffer -> {}));
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public BlockRenderType getRenderShape(BlockState p_149645_1_) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState blockState, Rotation rotation) {
        return blockState.setValue(FACING, rotation.rotate(blockState.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState blockState, Mirror mirror) {
        return blockState.rotate(mirror.getRotation(blockState.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
