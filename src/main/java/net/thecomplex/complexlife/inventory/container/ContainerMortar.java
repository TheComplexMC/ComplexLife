package net.thecomplex.complexlife.inventory.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.*;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.thecomplex.complexlife.crafting.RecipeMortar;
import net.thecomplex.complexlife.crafting.RecipeTypeManager;
import net.thecomplex.complexlife.inventory.OnlyInputInventory;
import net.thecomplex.complexlife.inventory.MortarOutputInventory;

import java.util.Optional;

public class ContainerMortar extends Container {
    private OnlyInputInventory inputInventory = new OnlyInputInventory(this);
    private MortarOutputInventory outputInventory = new MortarOutputInventory(inputInventory);
    private PlayerEntity player;

    public static ContainerMortar createContainerClientSide(int windowID, PlayerInventory playerInventory, net.minecraft.network.PacketBuffer extraData) {
        return new ContainerMortar(windowID, playerInventory, extraData);
    }


    protected ContainerMortar(int windowId, PlayerInventory invPlayer, PacketBuffer extraData) {
        super(ContainerManager.MORTAR_TYPE, windowId);

        player = invPlayer.player;

        final int SLOT_X_SPACING = 18;
        final int SLOT_Y_SPACING = 18;
        final int HOTBAR_XPOS = 8;
        final int HOTBAR_YPOS = 154 - 6;

        // slotIndex: 0-8
        for(int x = 0; x < 9; x++) {
            addSlot(new Slot(invPlayer, x, HOTBAR_XPOS + SLOT_X_SPACING * x, HOTBAR_YPOS));
        }

        // slotIndex: 9 - 31
        final int PLAYER_INVENTORY_XPOS = 8;
        final int PLAYER_INVENTORY_YPOS = 96 - 6;
        // Add the rest of the players inventory to the gui
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                int slotNumber = 9 + y * 9 + x;
                int xpos = PLAYER_INVENTORY_XPOS + x * SLOT_X_SPACING;
                int ypos = PLAYER_INVENTORY_YPOS + y * SLOT_Y_SPACING;
                addSlot(new Slot(invPlayer, slotNumber,  xpos, ypos));
            }
        }

        // slotIndex 32
        addSlot(new Slot(inputInventory, 0, 35, 42-6));

        // slotIndex 33
        addSlot(new MortarOutputSlot(outputInventory, 0, 125, 42-6));
    }

    @Override
    public boolean stillValid(PlayerEntity playerEntity) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity p_82846_1_, int sourceSlot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void slotsChanged(IInventory inventory) {
        if(player.level.isClientSide) return;
        if(inventory instanceof OnlyInputInventory) {
            ItemStack itemstack = ItemStack.EMPTY;
            Optional<RecipeMortar> optional = player.getServer().getRecipeManager().getRecipeFor(RecipeTypeManager.MORTAR, (OnlyInputInventory) inventory, player.getCommandSenderWorld());
            if (optional.isPresent()) {
                RecipeMortar recipe = optional.get();
                itemstack = recipe.assemble((OnlyInputInventory) inventory);
            }

            outputInventory.setItem(0, itemstack);
            ((ServerPlayerEntity)player).connection.send(new SSetSlotPacket(this.containerId, 37, itemstack));
        }
    }
}
