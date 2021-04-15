package net.thecomplex.complexlife.misc.energy.network;

public class NetworkInfo {
    private double producingCapacity;
    private double consumingAmount;
    private int componentCount;
    private int producerCount;

    public NetworkInfo() {
        this.consumingAmount = 0;
        this.producingCapacity = 0;
        this.componentCount = 0;
        this.producerCount = 0;
    }

    public int getProducerCount() {
        return producerCount;
    }

    public double getConsumingAmount() {
        return consumingAmount;
    }

    public double getProducingCapacity() {
        return producingCapacity;
    }

    public int getComponentCount() {
        return componentCount;
    }

    public void incrementProducingCapacity(double amount) {
        producingCapacity += amount;
    }

    public void incrementConsumingAmount(double amount) {
        consumingAmount += amount;
    }

    public void setProducingCapacity(double producingCapacity) {
        this.producingCapacity = producingCapacity;
    }

    public void setConsumingAmount(double consumingAmount) {
        this.consumingAmount = consumingAmount;
    }

    public void incrementComponentCount(boolean isProducer) {
        this.componentCount += 1;
        if(isProducer)
            this.producerCount++;
    }

    public boolean decrementComponentCount(boolean isProducer) {
        this.componentCount -= 1;
        if(isProducer)
            this.producerCount--;
        return componentCount <= 0;
    }
    public void setComponentCount(int componentCount) {
        this.componentCount = componentCount;
    }

    public void setProducerCount(int producerCount) {
        this.producerCount = producerCount;
    }

    public double energyLostPerComponent() {
        return Math.max(0, (consumingAmount - producingCapacity) / (componentCount - producerCount));
    }

    public boolean networkIsFullyOccupied() {
        return consumingAmount - producingCapacity >= -0.00001;
    }
}
