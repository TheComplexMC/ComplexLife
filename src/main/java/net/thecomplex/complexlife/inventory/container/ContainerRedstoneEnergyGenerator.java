package net.thecomplex.complexlife.inventory.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IIntArray;
import net.minecraft.world.World;
import net.thecomplex.complexlife.entity.tileentity.TileEntityRedstoneEnergyGenerator;
import net.thecomplex.complexlife.inventory.RedstoneFuelInventory;

import javax.annotation.Nullable;

public class ContainerRedstoneEnergyGenerator extends Container {
    private World world;
    private RedstoneFuelInventory fuelInventory;
    private TileEntityRedstoneEnergyGenerator.RedstoneEnergyGeneratorStateData stateData;

    public static ContainerRedstoneEnergyGenerator createContainerServerSide(int windowId, PlayerInventory playerInventory, RedstoneFuelInventory fuelInventory, TileEntityRedstoneEnergyGenerator.RedstoneEnergyGeneratorStateData stateData) {
        return new ContainerRedstoneEnergyGenerator(windowId, playerInventory, fuelInventory, stateData);
    }

    public static ContainerRedstoneEnergyGenerator createContainerClientSide(int windowId, PlayerInventory playerInventory, PacketBuffer extraData) {
        RedstoneFuelInventory fuelInventory = RedstoneFuelInventory.createForClientSideContainer(1);
        TileEntityRedstoneEnergyGenerator.RedstoneEnergyGeneratorStateData stateData = new TileEntityRedstoneEnergyGenerator.RedstoneEnergyGeneratorStateData();

        return new ContainerRedstoneEnergyGenerator(windowId, playerInventory, fuelInventory, stateData);
    }

    protected ContainerRedstoneEnergyGenerator(int windowId, PlayerInventory playerInventory, RedstoneFuelInventory fuelInventory, TileEntityRedstoneEnergyGenerator.RedstoneEnergyGeneratorStateData stateData) {
        super(ContainerManager.REDSTONE_ENERGY_GENERATOR_TYPE, windowId);
        this.world = playerInventory.player.level;
        this.fuelInventory = fuelInventory;
        this.stateData = stateData;

        addDataSlots(stateData);

        final int SLOT_X_SPACING = 18;
        final int SLOT_Y_SPACING = 18;
        final int HOTBAR_XPOS = 8;
        final int HOTBAR_YPOS = 142;
        final int PLAYER_INVENTORY_XPOS = 8;
        final int PLAYER_INVENTORY_YPOS = 84;
        // Add the players hotbar to the gui - the [xpos, ypos] location of each item
        for (int x = 0; x < 9; x++) {
            int slotNumber = x;
            addSlot(new Slot(playerInventory, slotNumber, HOTBAR_XPOS + SLOT_X_SPACING * x, HOTBAR_YPOS));
        }

        // Add the rest of the players inventory to the gui
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                int slotNumber = 9 + y * 9 + x;
                int xpos = PLAYER_INVENTORY_XPOS + x * SLOT_X_SPACING;
                int ypos = PLAYER_INVENTORY_YPOS + y * SLOT_Y_SPACING;
                addSlot(new Slot(playerInventory, slotNumber,  xpos, ypos));
            }
        }

        addSlot(new EnergyFuelSlot(fuelInventory, 0, 80, 34));
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity p_82846_1_, int p_82846_2_) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return fuelInventory.stillValid(player);
    }

    public TileEntityRedstoneEnergyGenerator.RedstoneEnergyGeneratorStateData getStateData() {
        return this.stateData;
    }
}
