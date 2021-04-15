package net.thecomplex.complexlife.inventory.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class MortarOutputSlot extends Slot {
    public MortarOutputSlot(IInventory inventory, int index, int posX, int posY) {
        super(inventory, index, posX, posY);
    }

    @Override
    public boolean mayPlace(ItemStack inputStack) {
        return false;
    }

    @Override
    public ItemStack onTake(PlayerEntity player, ItemStack outputStack) {
        return super.onTake(player, outputStack);
    }
}
