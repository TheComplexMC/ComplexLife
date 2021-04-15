package net.thecomplex.complexlife.block;


import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.registries.IForgeRegistry;
import net.thecomplex.complexlife.itemgroup.ItemGroupManager;

public class BlockManager {
    private static BlockManager _instance;

    public static BlockManager getInstance() {
        if(_instance == null) {
            _instance = new BlockManager();
        }
        return _instance;
    }

    private static final Item.Properties DEFAULT_BLOCK_ITEM_PROPERTIES = new Item.Properties().tab(ItemGroupManager.CORE_BLOCKS);

    //public static final BlockEntry OAK_FRAMEWORK = new BlockEntry(new Block(AbstractBlock.Properties.copy(Blocks.OAK_PLANKS)).setRegistryName("oak_framework"), DEFAULT_BLOCK_ITEM_PROPERTIES);
    public static final BlockEntry MORTAR = new BlockEntry(new BlockMortar().setRegistryName("mortar"), DEFAULT_BLOCK_ITEM_PROPERTIES);
    public static final BlockEntry REDSTONE_ENERGY_GENERATOR = new BlockEntry(new BlockRedstoneEnergyGenerator().setRegistryName("redstone_energy_generator"), DEFAULT_BLOCK_ITEM_PROPERTIES);
    public static final BlockEntry ELECTRIC_LIGHT = new BlockEntry(new BlockElectricLight().setRegistryName("electric_light"), DEFAULT_BLOCK_ITEM_PROPERTIES);
    public static final BlockEntry COPPER_ORE = new BlockEntry(new Block(AbstractBlock.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(3.0f, 3.0f).harvestLevel(1).harvestTool(ToolType.PICKAXE)).setRegistryName("copper_ore"), DEFAULT_BLOCK_ITEM_PROPERTIES);
    public static final BlockEntry COPPER_BLOCK = new BlockEntry(new Block(AbstractBlock.Properties.of(Material.METAL).requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL).harvestLevel(1).harvestTool(ToolType.PICKAXE)).setRegistryName("copper_block"), DEFAULT_BLOCK_ITEM_PROPERTIES);

    public void RegisterBlocks(final IForgeRegistry<Block> registry) {
        //registry.register(OAK_FRAMEWORK.getBlock());
        registry.register(MORTAR.getBlock());
        registry.register(REDSTONE_ENERGY_GENERATOR.getBlock());
        registry.register(ELECTRIC_LIGHT.getBlock());
        registry.register(COPPER_ORE.getBlock());
        registry.register(COPPER_BLOCK.getBlock());
    }

    public void RegisterBlockItems(final IForgeRegistry<Item> registry) {
        //registry.register(OAK_FRAMEWORK.getItem());
        registry.register(MORTAR.getItem());
        registry.register(REDSTONE_ENERGY_GENERATOR.getItem());
        registry.register(ELECTRIC_LIGHT.getItem());
        registry.register(COPPER_ORE.getItem());
        registry.register(COPPER_BLOCK.getItem());
    }


    public static class BlockEntry {
        private Block _block;
        private Item _item;

        public BlockEntry(Block block, Item.Properties itemProperties) {
            _block = block;
            _item = new BlockItem(block, itemProperties).setRegistryName(block.getRegistryName());
        }

        public Block getBlock() {
            return _block;
        }

        public Item getItem() {
            return _item;
        }
    }
}
