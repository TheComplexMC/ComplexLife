package net.thecomplex.complexlife.misc.energy;

import net.minecraft.util.math.BlockPos;

public interface IEnergyConsumer {
    void onEnergyInit(EnergyConsumptionController energyConsumptionController);

    BlockPos getBlockPos();
}
