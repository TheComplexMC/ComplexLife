package net.thecomplex.complexlife.misc.energy;

import net.minecraft.util.math.BlockPos;
import net.thecomplex.complexlife.misc.energy.network.EnergyNetworkManager;

public class EnergyProducingController {
    private EnergyNetworkManager energyNetworkManager;
    private BlockPos pos;

    public EnergyProducingController(EnergyNetworkManager energyNetworkManager, BlockPos pos) {
        this.energyNetworkManager = energyNetworkManager;
        this.pos = pos;
    }

    public double getCurrentRegisteredProductionAmount() {
        return energyNetworkManager.getEnergyAmount(pos);
    }

    public double getCurrentlyUsedEnergyAmount() {
        return energyNetworkManager.getProducerUsedCapacity(pos);
    }

    public void setProductionAmount(double amount) {
        energyNetworkManager.updateEnergyAmount(pos, amount);
    }
}
