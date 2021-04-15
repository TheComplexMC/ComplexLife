package net.thecomplex.complexlife.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.ToolType;
import net.thecomplex.complexlife.entity.tileentity.TileEntityElectricLight;
import net.thecomplex.complexlife.misc.energy.EnergyBlockSocket;

import javax.annotation.Nullable;

public class BlockElectricLight extends EnergyComponentBlock {
    public static final IntegerProperty POWER = BlockStateProperties.POWER;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public BlockElectricLight() {
        super(Properties.of(Material.GLASS).strength(0.3f).sound(SoundType.GLASS).harvestTool(ToolType.PICKAXE), new EnergyBlockSocket(0, Direction.UP, 8, 8));
        this.registerDefaultState(this.getStateDefinition().any().setValue(POWER, 0).setValue(POWERED, false));
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileEntityElectricLight();
    }

    @Override
    public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
        if(state.getValue(POWERED))
            return state.getValue(POWER);
        else
            return 0;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(POWER, POWERED);
    }
}
