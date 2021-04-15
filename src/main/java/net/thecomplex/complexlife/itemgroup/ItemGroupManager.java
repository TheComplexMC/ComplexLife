package net.thecomplex.complexlife.itemgroup;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.thecomplex.complexlife.block.BlockManager;
import net.thecomplex.complexlife.item.ItemManager;

public class ItemGroupManager {
    private static ItemGroupManager _instance;

    public static ItemGroupManager getInstance() {
        if(_instance == null) {
            _instance = new ItemGroupManager();
        }

        return _instance;
    }

    public static final ItemGroup CORE_BLOCKS = new ItemGroup("core_blocks") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(BlockManager.MORTAR.getItem());
        }
    };

    public static final ItemGroup CORE_ITEMS = new ItemGroup("core_items") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ItemManager.STONE_DUST);
        }
    };
}
