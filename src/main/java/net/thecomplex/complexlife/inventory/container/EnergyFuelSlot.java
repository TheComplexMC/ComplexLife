package net.thecomplex.complexlife.inventory.container;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class EnergyFuelSlot extends Slot {
    public EnergyFuelSlot(IInventory inventory, int slotNumber, int posX, int posY) {
        super(inventory, slotNumber, posX, posY);
    }

    @Override
    public boolean mayPlace(ItemStack itemStack) {
        return itemStack.getItem() == Items.REDSTONE;
    }
}
