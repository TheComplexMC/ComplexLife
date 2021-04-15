package net.thecomplex.complexlife.block;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.*;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.client.ClientHooks;
import net.minecraftforge.fml.network.NetworkHooks;
import net.thecomplex.complexlife.inventory.container.ContainerMortar;

import javax.annotation.Nullable;

public class BlockMortar extends Block implements INamedContainerProvider {
    public static final DirectionProperty FACING = HorizontalBlock.FACING;

    private static final VoxelShape BLOCK_BOTTOM = Block.box(0, 0, 0, 16, 8, 16);
    private static final VoxelShape BLOCK_TOP = Block.box(4, 8, 4, 12, 13, 12);
    private static final VoxelShape MORTAR = VoxelShapes.or(BLOCK_BOTTOM, BLOCK_TOP);

    public BlockMortar() {
        super(AbstractBlock.Properties.of(Material.STONE).strength(2.3F).harvestTool(ToolType.AXE).harvestLevel(0));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public ActionResultType use(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockRayTraceResult traceResult) {
        if(world.isClientSide()) {
            return ActionResultType.SUCCESS;
        }
        else {
            NetworkHooks.openGui((ServerPlayerEntity) playerEntity, this);
            //playerEntity.openMenu(this);
            return ActionResultType.CONSUME;
        }
    }

    @Override
    public BlockRenderType getRenderShape(BlockState p_149645_1_) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
        return MORTAR;
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

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("container.complexlife.mortar");
    }

    @Nullable
    @Override
    public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity player) {
        return ContainerMortar.createContainerClientSide(windowId, playerInventory, null);
    }
}
