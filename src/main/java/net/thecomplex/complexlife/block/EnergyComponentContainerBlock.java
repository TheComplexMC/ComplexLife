package net.thecomplex.complexlife.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import net.thecomplex.complexlife.misc.energy.EnergyBlockSocket;
import net.thecomplex.complexlife.misc.energy.ISocketBlock;

import javax.annotation.Nullable;

public abstract class EnergyComponentContainerBlock extends ContainerBlock implements ISocketBlock {
    protected EnergyBlockSocket socket;

    public EnergyComponentContainerBlock(Properties properties, EnergyBlockSocket socket) {
        super(properties);
        this.socket = socket;
    }

    @Override
    public EnergyBlockSocket getSocket(BlockState blockState) {
        return socket;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public abstract TileEntity createTileEntity(BlockState state, IBlockReader world);

}
