package net.thecomplex.complexlife.entity.tileentity;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.IForgeRegistry;
import net.thecomplex.complexlife.block.BlockManager;

public class TileEntityTypeManager {
    private static TileEntityTypeManager _instance;

    public static TileEntityTypeManager getInstance() {
        if(_instance == null)
            _instance = new TileEntityTypeManager();
        return _instance;
    }

    public static TileEntityType<TileEntityRedstoneEnergyGenerator> REDSTONE_ENERGY_GENERATOR = TileEntityType.Builder.of(TileEntityRedstoneEnergyGenerator::new, BlockManager.REDSTONE_ENERGY_GENERATOR.getBlock()).build(null);
    public static TileEntityType<TileEntityElectricLight> ELECTRIC_LIGHT = TileEntityType.Builder.of(TileEntityElectricLight::new, BlockManager.ELECTRIC_LIGHT.getBlock()).build(null);

    public void registerTileEntityTypes(IForgeRegistry<TileEntityType<?>> registry) {
        REDSTONE_ENERGY_GENERATOR.setRegistryName("complexlife:redstone_energy_generator");
        registry.register(REDSTONE_ENERGY_GENERATOR);

        ELECTRIC_LIGHT.setRegistryName("complexlife:electric_light");
        registry.register(ELECTRIC_LIGHT);
    }
}
