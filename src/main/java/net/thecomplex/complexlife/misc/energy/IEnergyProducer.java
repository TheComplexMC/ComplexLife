package net.thecomplex.complexlife.misc.energy;

import net.minecraft.util.math.BlockPos;

public interface IEnergyProducer {
    void onEnergyInit(EnergyProducingController energyProducingController);

    BlockPos getBlockPos();
}