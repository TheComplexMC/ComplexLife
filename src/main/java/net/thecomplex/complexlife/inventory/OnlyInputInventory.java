package net.thecomplex.complexlife.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.thecomplex.complexlife.inventory.container.ContainerMortar;

public class OnlyInputInventory implements IInventory {
    private ItemStack item;
    private ContainerMortar container;

    public OnlyInputInventory(ContainerMortar container) {
        item = ItemStack.EMPTY;
        this.container = container;
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return item.isEmpty();
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
            this.container.slotsChanged(this);
            return buffer;
        }
        item.setCount(item.getCount() - count);
        this.container.slotsChanged(this);
        return new ItemStack(item.getItem(), count);
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        ItemStack buffer = item;
        item = ItemStack.EMPTY;
        return buffer;
    }

    @Override
    public void setItem(int index, ItemStack item) {
        this.item = item;
        this.container.slotsChanged(this);
    }

    @Override
    public void setChanged() {

    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return true;
    }

    @Override
    public boolean canPlaceItem(int p_94041_1_, ItemStack p_94041_2_) {
        return true;
    }

    @Override
    public void clearContent() {
        item = ItemStack.EMPTY;
    }
}
