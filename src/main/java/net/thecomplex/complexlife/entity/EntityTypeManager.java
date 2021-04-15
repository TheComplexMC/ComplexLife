package net.thecomplex.complexlife.entity;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.common.extensions.IForgeEntity;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class EntityTypeManager {
    private static EntityTypeManager _instance;

    public static EntityTypeManager getInstance() {
        if(_instance == null)
            _instance = new EntityTypeManager();
        return _instance;
    }

    public static EntityType<EntityCopperCable> COPPER_CABLE = EntityType.Builder.<EntityCopperCable>of(EntityCopperCable::new ,EntityClassification.MISC).noSave().sized(1F, 1F).clientTrackingRange(10).updateInterval(Integer.MAX_VALUE).build("complexlife:copper_cable");

    public void registerEntityTypes(IForgeRegistry<EntityType<?>> registry) {
        COPPER_CABLE.setRegistryName("complexlife:copper_cable");
        registry.register(COPPER_CABLE);
    }
}
