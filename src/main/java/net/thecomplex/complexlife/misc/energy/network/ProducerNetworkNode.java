package net.thecomplex.complexlife.misc.energy.network;

import net.minecraft.util.math.BlockPos;

public class ProducerNetworkNode implements INetworkNode{
    private int networkId;
    private double producingAmount;
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
        return getProducingAmount();
    }

    @Override
    public void setEnergyAmount(double amount) {
        setProducingAmount(amount);
    }

    @Override
    public void setBlockPos(BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public BlockPos getBlockPos() {
        return pos;
    }

    public double getProducingAmount() {
        return producingAmount;
    }

    public void setProducingAmount(double amount) {
        producingAmount = amount;
    }
}
