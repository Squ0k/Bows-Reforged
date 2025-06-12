package com.dnii.bows_reforged;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.function.Function;

public class BrItems {
    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register((itemGroup) -> itemGroup.add(BrItems.ARCANE_FIBER));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register((itemGroup) -> itemGroup.add(BrItems.ELECTRIC_FIBER));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register((itemGroup) -> itemGroup.add(BrItems.EXPLOSIVE_FIBER));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register((itemGroup) -> itemGroup.add(BrItems.REFORGED_BOW));
    }

    public static Item register(String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(BowsReforged.MOD_ID, name)); // Create the item key
        Item item = itemFactory.apply(settings); // Create the item instance
        Registry.register(Registries.ITEM, itemKey, item); // Register the item
        return item;
    }

    public static final Item ARCANE_FIBER = register("arcane_fiber", Item::new, new Item.Settings());
    public static final Item ELECTRIC_FIBER = register("electric_fiber", Item::new, new Item.Settings());
    public static final Item EXPLOSIVE_FIBER = register("explosive_fiber", Item::new, new Item.Settings());
    // 1152 = 18 * 64
    public static final Item REFORGED_BOW = register("reforged_bow", ReforgedBowItem::new, new Item.Settings().maxDamage(1152).rarity(Rarity.RARE));
}
