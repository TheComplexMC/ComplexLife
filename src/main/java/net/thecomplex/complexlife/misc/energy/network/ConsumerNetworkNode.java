package net.thecomplex.complexlife.misc.energy.network;

import net.minecraft.util.math.BlockPos;

public class ConsumerNetworkNode implements INetworkNode{
    private int networkId;
    private double neededEnergy;
    private BlockPos pos;

    @Override
    public int getNetworkId() {
        return networkId;
    }

    @Override
    public void setNetworkId(int id) {
        networkId = id;
    }

    @Override
    public double getEnergyAmount() {
        return neededEnergy;
    }

    @Override
    public void setEnergyAmount(double amount) {
        this.neededEnergy = amount;
    }

    @Override
    public void setBlockPos(BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public BlockPos getBlockPos() {
        return pos;
    }
}
