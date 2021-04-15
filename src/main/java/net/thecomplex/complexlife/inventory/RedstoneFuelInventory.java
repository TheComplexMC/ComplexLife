package net.thecomplex.complexlife.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.items.ItemStackHandler;

import java.util.function.Predicate;

public class RedstoneFuelInventory implements IInventory {
    private final ItemStackHandler inventory;
    private Predicate<PlayerEntity> canPlayerAccessInventoryLambda = x -> true;

    private RedstoneFuelInventory(int size) {
        inventory = new ItemStackHandler(size);
    }

    private RedstoneFuelInventory(int size, Predicate<PlayerEntity> canPlayerAccessInventoryLambda) {
        this.inventory = new ItemStackHandler(size);
        this.canPlayerAccessInventoryLambda = canPlayerAccessInventoryLambda;

    }

    public static RedstoneFuelInventory createForTileEntity(int size, Predicate<PlayerEntity> canPlayerAccessInventoryLambda) {
        return new RedstoneFuelInventory(size, canPlayerAccessInventoryLambda);
    }

    public static RedstoneFuelInventory createForClientSideContainer(int size) {
        return new RedstoneFuelInventory(size);
    }

    public CompoundNBT serializeNBT() {
        return inventory.serializeNBT();
    }

    public void deserializeNBT(CompoundNBT nbt) {
        inventory.deserializeNBT(nbt);
    }

    public void setCanPlayerAccessInventoryLambda(Predicate<PlayerEntity> canPlayerAccessInventoryLambda) {
        this.canPlayerAccessInventoryLambda = canPlayerAccessInventoryLambda;
    }

    @Override
    public int getContainerSize() {
        return inventory.getSlots();
    }

    @Override
    public boolean isEmpty() {
        for(int i = 0; i < inventory.getSlots(); i++) {
            if(!inventory.getStackInSlot(i).isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int index) {
        return inventory.getStackInSlot(index);
    }

    @Override
    public ItemStack removeItem(int index, int amount) {
        return inventory.extractItem(index, amount, false);
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        int stackSize = inventory.getSlotLimit(index);
        return inventory.extractItem(index, stackSize, false);
    }

    @Override
    public void setItem(int index, ItemStack itemStack) {
        inventory.setStackInSlot(index, itemStack);
    }

    @Override
    public void setChanged() {

    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return canPlayerAccessInventoryLambda.test(player);
    }

    @Override
    public void clearContent() {
        for(int i = 0; i < inventory.getSlots(); i++) {
            inventory.setStackInSlot(i, ItemStack.EMPTY);
        }
    }
}
