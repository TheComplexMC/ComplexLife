package net.thecomplex.complexlife.inventory.container;


import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.registries.IForgeRegistry;

public class ContainerManager {
    private static ContainerManager _instance;

    public static ContainerManager getInstance() {
        if(_instance == null)
            _instance = new ContainerManager();
        return _instance;
    }

    public static final ContainerType<ContainerMortar> MORTAR_TYPE = IForgeContainerType.create(ContainerMortar::createContainerClientSide);
    public static final ContainerType<ContainerRedstoneEnergyGenerator> REDSTONE_ENERGY_GENERATOR_TYPE = IForgeContainerType.create(ContainerRedstoneEnergyGenerator::createContainerClientSide);

    public void RegisterContainers(IForgeRegistry<ContainerType<?>> registry) {
        MORTAR_TYPE.setRegistryName("mortar");
        registry.register(MORTAR_TYPE);

        REDSTONE_ENERGY_GENERATOR_TYPE.setRegistryName("redstone_energy_generator");
        registry.register(REDSTONE_ENERGY_GENERATOR_TYPE);
    }
}
