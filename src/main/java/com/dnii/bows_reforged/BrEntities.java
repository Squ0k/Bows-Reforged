package com.dnii.bows_reforged;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class BrEntities {
    public static void initialize() {
    }

    public static final EntityType<ArcaneArrowEntity> ARCANE_ARROW = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of("bows_reforged", "arcane_arrow"),
            EntityType.Builder.create(ArcaneArrowEntity::new, SpawnGroup.MISC)
                    .dimensions(0.5f, 0.5f)
                    .maxTrackingRange(6)
                    .trackingTickInterval(1)
                    .build()
    );

    public static final EntityType<ElectricArrowEntity> ELECTRIC_ARROW = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of("bows_reforged", "electric_arrow"),
            EntityType.Builder.create(ElectricArrowEntity::new, SpawnGroup.MISC)
                    .dimensions(0.5f, 0.5f)
                    .maxTrackingRange(4)
                    .trackingTickInterval(20)
                    .build()
    );

    public static final EntityType<ExplosionArrowEntity> EXPLOSION_ARROW = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of("bows_reforged", "explosion_arrow"),
            EntityType.Builder.create(ExplosionArrowEntity::new, SpawnGroup.MISC)
                    .dimensions(0.5f, 0.5f)
                    .maxTrackingRange(4)
                    .trackingTickInterval(20)
                    .build()
    );
}
