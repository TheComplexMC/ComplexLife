package net.thecomplex.complexlife.item;

import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;
import net.thecomplex.complexlife.itemgroup.ItemGroupManager;

public class ItemManager {
    private static ItemManager _instance;

    public static ItemManager getInstance() {
        if(_instance == null) {
            _instance = new ItemManager();
        }

        return _instance;
    }

    private static final Item.Properties DEFAULT_ITEM_PROPERTIES = new Item.Properties().tab(ItemGroupManager.CORE_ITEMS);

    public static final Item STONE_DUST = new Item(DEFAULT_ITEM_PROPERTIES).setRegistryName("stone_dust");
    public static final Item COPPER_CABLE = new ItemCopperCable(DEFAULT_ITEM_PROPERTIES).setRegistryName("copper_cable");
    public static final Item COPPER_INGOT = new Item(DEFAULT_ITEM_PROPERTIES).setRegistryName("copper_ingot");
    public static final Item COPPER_NUGGET = new Item(DEFAULT_ITEM_PROPERTIES).setRegistryName("copper_nugget");

    public void RegisterItems(final IForgeRegistry<Item> registry) {
        registry.register(STONE_DUST);
        registry.register(COPPER_CABLE);
        registry.register(COPPER_INGOT);
        registry.register(COPPER_NUGGET);
    }
}
