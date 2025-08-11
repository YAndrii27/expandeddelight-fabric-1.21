package com.ianm1647.expandeddelight.util;

import com.ianm1647.expandeddelight.item.ItemList;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.item.Item;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.RegistryKey;

public class LootTableUtil {
    public static void modifyLootTables() {
        lootTable(LootTables.VILLAGE_DESERT_HOUSE_CHEST, ItemList.ASPARAGUS_SEEDS, 0.5f, 1.0f, 3.0f);
        lootTable(LootTables.VILLAGE_DESERT_HOUSE_CHEST, ItemList.ASPARAGUS, 0.25f, 1.0f, 2.0f);
        lootTable(LootTables.VILLAGE_SAVANNA_HOUSE_CHEST, ItemList.SWEET_POTATO, 0.5f, 1.0f, 3.0f);
        lootTable(LootTables.VILLAGE_SNOWY_HOUSE_CHEST, ItemList.CHILI_PEPPER, 0.5f, 1.0f, 3.0f);
        lootTable(LootTables.VILLAGE_SNOWY_HOUSE_CHEST, ItemList.CHILI_PEPPER_SEEDS, 0.25f, 1.0f, 2.0f);
    }

    private static void lootTable(RegistryKey<LootTable> registryKey, Item item, float chance, float min, float max) {
        LootTableEvents.MODIFY.register(((key, tableBuilder, source, registries) -> {
            if (registryKey.equals(key)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(chance))
                        .with(ItemEntry.builder(item))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(min, max)).build());

                tableBuilder.pool(poolBuilder);
            }
        }));
    }
}
