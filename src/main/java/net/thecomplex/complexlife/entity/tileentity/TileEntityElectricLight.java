package net.thecomplex.complexlife.entity.tileentity;

import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.thecomplex.complexlife.block.BlockElectricLight;
import net.thecomplex.complexlife.misc.energy.EnergyConsumptionController;
import net.thecomplex.complexlife.misc.energy.IEnergyConsumer;

public class TileEntityElectricLight extends TileEntity implements ITickableTileEntity, IEnergyConsumer {
    EnergyConsumptionController energyController;
    private static final double NEEDED_ENERGY = 0.005D;
    private double lastEnergyCapacity = 0;

    public TileEntityElectricLight() {
        super(TileEntityTypeManager.ELECTRIC_LIGHT);
    }

    @Override
    public void onEnergyInit(EnergyConsumptionController energyConsumptionController) {
        energyController = energyConsumptionController;
    }

    @Override
    public void tick() {
        if(energyController == null) return;
        if(energyController.getCurrentRegisteredConsumptionAmount() != NEEDED_ENERGY)
            energyController.setConsumptionAmount(NEEDED_ENERGY);

        if(lastEnergyCapacity != energyController.getAvailableEnergyAmount())
            lastEnergyCapacity = energyController.getAvailableEnergyAmount();
        else
            return;

        int energy = Math.min(15, (int)Math.round((energyController.getAvailableEnergyAmount() / NEEDED_ENERGY) * 15));
        level.setBlock(getBlockPos(), level.getBlockState(getBlockPos()).setValue(BlockElectricLight.POWER, energy).setValue(BlockElectricLight.POWERED, energy > 0.000001), 3);
    }
}
