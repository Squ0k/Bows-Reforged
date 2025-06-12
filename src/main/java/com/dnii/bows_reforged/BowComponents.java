package com.dnii.bows_reforged;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class BowComponents {
    protected static void initialize() {}

    public static final ComponentType<String> MATERIAL = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of("bows-reforged", "material"),
            ComponentType.<String>builder().codec(Codec.STRING).build()
    );

    public static final ComponentType<String> ATTRIBUTE = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of("bows-reforged", "attribute"),
            ComponentType.<String>builder().codec(Codec.STRING).build()
    );

    public static final ComponentType<Integer> AMMO = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of("bows-reforged", "ammo"),
            ComponentType.<Integer>builder().codec(Codec.INT).build()
    );

    public static final ComponentType<Boolean> HAS_SCOPE = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of("bows-reforged", "has_scope"),
            ComponentType.<Boolean>builder().codec(Codec.BOOL).build()
    );

    public static final ComponentType<Integer> DMG_BONUS = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of("bows-reforged", "dmg_bonus"),
            ComponentType.<Integer>builder().codec(Codec.INT).build()
    );

    public static final ComponentType<Float> DRAW_TIME = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of("bows-reforged", "draw_time"),
            ComponentType.<Float>builder().codec(Codec.FLOAT).build()
    );
}