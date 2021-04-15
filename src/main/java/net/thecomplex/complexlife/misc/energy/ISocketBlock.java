package net.thecomplex.complexlife.misc.energy;

import net.minecraft.block.BlockState;

public interface ISocketBlock {
    EnergyBlockSocket getSocket(BlockState blockState);
}
