package net.thecomplex.complexlife.misc.energy;

import net.minecraft.util.math.BlockPos;
import net.thecomplex.complexlife.misc.energy.network.EnergyNetworkManager;

public class EnergyConsumptionController {
    private EnergyNetworkManager energyNetworkManager;
    private BlockPos pos;

    public EnergyConsumptionController(EnergyNetworkManager energyNetworkManager, BlockPos pos) {
        this.energyNetworkManager = energyNetworkManager;
        this.pos = pos;
    }

    public double getCurrentRegisteredConsumptionAmount() {
        return energyNetworkManager.getEnergyAmount(pos);
    }

    public double getAvailableEnergyAmount() {
        return energyNetworkManager.getAvailableConsumptionAmount(pos);
    }

    public void setConsumptionAmount(double amount) {
        energyNetworkManager.updateEnergyAmount(pos, amount);
    }
}
