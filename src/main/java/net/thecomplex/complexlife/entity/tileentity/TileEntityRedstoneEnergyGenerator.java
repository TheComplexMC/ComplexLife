package net.thecomplex.complexlife.entity.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.thecomplex.complexlife.block.BlockRedstoneEnergyGenerator;
import net.thecomplex.complexlife.inventory.RedstoneFuelInventory;
import net.thecomplex.complexlife.inventory.container.ContainerRedstoneEnergyGenerator;
import net.thecomplex.complexlife.misc.energy.EnergyProducingController;
import net.thecomplex.complexlife.misc.energy.IEnergyProducer;

import javax.annotation.Nullable;

public class TileEntityRedstoneEnergyGenerator extends TileEntity implements ITickableTileEntity, INamedContainerProvider, IEnergyProducer {
    public static final int FUEL_SLOT_COUNT = 1;
    // W/tick?
    // 1000 tick ca. 1 Minecraft ingame Stunde
    // 50W per kilo-tick = 0,05W per tick
    public static final double ENERGY_OUTPUT = 0.05;

    private RedstoneFuelInventory fuelInventory;
    private final RedstoneEnergyGeneratorStateData stateData = new RedstoneEnergyGeneratorStateData();
    private boolean isProducingEnergy;
    private EnergyProducingController controller;

    public TileEntityRedstoneEnergyGenerator() {
        super(TileEntityTypeManager.REDSTONE_ENERGY_GENERATOR);
        this.fuelInventory = RedstoneFuelInventory.createForTileEntity(FUEL_SLOT_COUNT, this::canPlayerAccessInventory);
        isProducingEnergy = false;
    }

    public boolean canPlayerAccessInventory(PlayerEntity player) {
        if (this.level.getBlockEntity(this.getBlockPos()) != this) return false;
        final double X_CENTRE_OFFSET = 0.5;
        final double Y_CENTRE_OFFSET = 0.5;
        final double Z_CENTRE_OFFSET = 0.5;
        final double MAXIMUM_DISTANCE_SQ = 8.0 * 8.0;
        return player.position().distanceToSqr(getBlockPos().getX() + X_CENTRE_OFFSET, getBlockPos().getY() + Y_CENTRE_OFFSET, getBlockPos().getZ() + Z_CENTRE_OFFSET) < MAXIMUM_DISTANCE_SQ;
    }

    @Override
    public void onEnergyInit(EnergyProducingController energyProducingController) {
        if(level.isClientSide) return;
        controller = energyProducingController;
    }

    @Override
    public void tick() {
        if(level.isClientSide) return;
        if(controller == null) return;
        this.isProducingEnergy = convertEnergy();
        double producedEnergy = getEnergyOutput();
        if(producedEnergy != controller.getCurrentRegisteredProductionAmount()) {
            controller.setProductionAmount(producedEnergy);
        }
        double usedEnergyAmount = controller.getCurrentlyUsedEnergyAmount();
        stateData.setEnergyStorageAmount(stateData.getEnergyStorageAmountDouble() + (producedEnergy - usedEnergyAmount));
    }

    public boolean convertEnergy() {
        boolean producesEnergy = false;
        if(stateData.remainingFuelEnergyAmount > 0) {
            stateData.remainingFuelEnergyAmount--;
            producesEnergy = true;
        }
        else {
            ItemStack fuelItemStack = fuelInventory.getItem(0);
            if(!fuelItemStack.isEmpty() && BlockRedstoneEnergyGenerator.getEnergyAmountFromItem(fuelItemStack) > 0) {
                int energyAmount = BlockRedstoneEnergyGenerator.getEnergyAmountFromItem(fuelItemStack);
                stateData.initialFuelEnergyAmount = energyAmount;
                stateData.remainingFuelEnergyAmount = energyAmount;
                fuelInventory.removeItem(0, 1);
                producesEnergy = true;
                setChanged();
            }
        }

        return producesEnergy;
    }

    public int getEnergyStorageCapacity() {
        return 1000;
    }

    public double getEnergyOutput() {
        if(isProducingEnergy) {
            return ENERGY_OUTPUT;
        }
        return 0;
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        stateData.putInNBT(nbt);
        nbt.put("fuelSlots", fuelInventory.serializeNBT());
        return super.save(nbt);
    }

    @Override
    public void load(BlockState blockState, CompoundNBT nbt) {
        super.load(blockState, nbt);
        deserializeNBT(nbt);
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        stateData.readFromNBT(nbt);
        fuelInventory.deserializeNBT(nbt.getCompound("fuelSlots"));
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        stateData.putInNBT(nbt);
        nbt.put("fuelSlots", fuelInventory.serializeNBT());
        return nbt;
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(getBlockPos(), 42, getUpdateTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return serializeNBT();
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        CompoundNBT nbt = pkt.getTag();
        BlockState state = level.getBlockState(getBlockPos());
        handleUpdateTag(state, nbt);
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        deserializeNBT(tag);
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("container.complexlife.redstone_energy_generator");
    }

    @Nullable
    @Override
    public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity player) {
        return ContainerRedstoneEnergyGenerator.createContainerServerSide(windowId, playerInventory, fuelInventory, stateData);
    }

    public static class RedstoneEnergyGeneratorStateData implements IIntArray {
        public static final int INITIAL_FUEL_ENERGY_AMOUNT_INDEX = 0;
        public static final int REMAINING_FUEL_ENERGY_AMOUNT_INDEX = 1;
        public static final int ENERGY_STORAGE_AMOUNT_LOWER = 2;
        public static final int ENERGY_STORAGE_AMOUNT_UPPER = 3;

        public int initialFuelEnergyAmount;
        public int remainingFuelEnergyAmount;
        public int energyStorageAmountLower;
        public int energyStorageAmountUpper;

        public double getEnergyStorageAmountDouble() {
            long energyStorageAmountBits = 0;
            energyStorageAmountBits |= energyStorageAmountLower & 0xffffffffL;
            energyStorageAmountBits |= (long)energyStorageAmountUpper << 32;
            return Double.longBitsToDouble(energyStorageAmountBits);
        }

        public void setEnergyStorageAmount(double value) {
            long energyStorageAmountBits = Double.doubleToLongBits(value);
            energyStorageAmountLower = 0;
            energyStorageAmountLower |= (energyStorageAmountBits);
            energyStorageAmountUpper = 0;
            energyStorageAmountUpper |= (energyStorageAmountBits >>> 32);
        }

        @Override
        public int get(int index) {
            switch (index) {
                case INITIAL_FUEL_ENERGY_AMOUNT_INDEX:
                    return initialFuelEnergyAmount;
                case REMAINING_FUEL_ENERGY_AMOUNT_INDEX:
                    return remainingFuelEnergyAmount;
                case ENERGY_STORAGE_AMOUNT_LOWER:
                    return energyStorageAmountLower;
                case ENERGY_STORAGE_AMOUNT_UPPER:
                    return energyStorageAmountUpper;
                default:
                    throw new IndexOutOfBoundsException("Index out of bounds!");
            }
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case INITIAL_FUEL_ENERGY_AMOUNT_INDEX:
                    initialFuelEnergyAmount = value;
                    break;
                case REMAINING_FUEL_ENERGY_AMOUNT_INDEX:
                    remainingFuelEnergyAmount = value;
                    break;
                case ENERGY_STORAGE_AMOUNT_LOWER:
                    energyStorageAmountLower = value;
                    break;
                case ENERGY_STORAGE_AMOUNT_UPPER:
                    energyStorageAmountUpper = value;
                    break;
                default:
                    throw new IndexOutOfBoundsException("Index out of bounds!");
            }
        }

        @Override
        public int getCount() {
            return 4;
        }

        public void putInNBT(CompoundNBT nbt) {
            nbt.putInt("initialFuelEnergyAmount", initialFuelEnergyAmount);
            nbt.putInt("remainingFuelEnergyAmount", remainingFuelEnergyAmount);
            nbt.putInt("energyStorageAmountLower", energyStorageAmountLower);
            nbt.putInt("energyStorageAmountUpper", energyStorageAmountUpper);
        }

        public void readFromNBT(CompoundNBT nbt) {
            initialFuelEnergyAmount = nbt.getInt("initialFuelEnergyAmount");
            remainingFuelEnergyAmount = nbt.getInt("remainingFuelEnergyAmount");
            energyStorageAmountLower = nbt.getInt("energyStorageAmountLower");
            energyStorageAmountUpper = nbt.getInt("energyStorageAmountUpper");
        }
    }
}
