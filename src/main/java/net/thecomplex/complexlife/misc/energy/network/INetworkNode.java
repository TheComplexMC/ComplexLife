package net.thecomplex.complexlife.misc.energy.network;

import net.minecraft.util.math.BlockPos;

public interface INetworkNode {
    int getNetworkId();
    void setNetworkId(int id);
    double getEnergyAmount();
    void setEnergyAmount(double amount);
    void setBlockPos(BlockPos pos);
    BlockPos getBlockPos();
}
