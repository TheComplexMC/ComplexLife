package net.thecomplex.complexlife.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class MortarOutputInventory implements IInventory {
    private ItemStack item;
    private OnlyInputInventory inputInventory;

    public MortarOutputInventory(OnlyInputInventory inputInventory) {
        item = ItemStack.EMPTY;
        this.inputInventory = inputInventory;
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return item == ItemStack.EMPTY;
    }

    @Override
    public ItemStack getItem(int index) {
        return item;
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        if(count >= item.getCount()) {
            ItemStack buffer = item;
            item = ItemStack.EMPTY;
            inputInventory.removeItem(0, count);
            return buffer;
        }
        item.setCount(item.getCount() - count);
        inputInventory.removeItem(0, count);
        return new ItemStack(item.getItem(), count);
    }

    @Override
    public ItemStack removeItemNoUpdate(int p_70304_1_) {
        ItemStack buffer = item;
        item = ItemStack.EMPTY;
        inputInventory.removeItemNoUpdate(0);
        return buffer;
    }

    @Override
    public void setItem(int index, ItemStack item) {
        this.item = item;
    }

    @Override
    public void setChanged() { }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return false;
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack player) {
        return false;
    }

    @Override
    public void clearContent() {
        item = ItemStack.EMPTY;
    }
}
