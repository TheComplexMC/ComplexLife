package net.thecomplex.complexlife.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import net.thecomplex.complexlife.misc.energy.EnergyBlockSocket;
import net.thecomplex.complexlife.misc.energy.ISocketBlock;

import javax.annotation.Nullable;

public abstract class EnergyComponentBlock extends Block implements ISocketBlock {
    protected EnergyBlockSocket socket;

    public EnergyComponentBlock(Properties properties, EnergyBlockSocket socket) {
        super(properties);
        this.socket = socket;
    }

    @Override
    public EnergyBlockSocket getSocket(BlockState state) {
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
