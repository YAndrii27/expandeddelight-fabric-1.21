package com.ianm1647.expandeddelight.registry;

import com.ianm1647.expandeddelight.ExpandedDelight;
import com.ianm1647.expandeddelight.block.BlockList;
import com.ianm1647.expandeddelight.block.custom.CinnamonLogBlock;
import com.ianm1647.expandeddelight.block.custom.DelightCropBlock;
import com.ianm1647.expandeddelight.block.custom.JuicerBlock;
import com.ianm1647.expandeddelight.block.custom.MortarPestleBlock;
import com.ianm1647.expandeddelight.world.feature.tree.ModSaplingGenerators;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.*;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import vectorwing.farmersdelight.common.block.WildCropBlock;

public class BlockRegistry {

    public static void registerBlocks() {
        //blocks
        BlockList.CINNAMON_SAPLING = block("cinnamon_sapling",
                new SaplingBlock(ModSaplingGenerators.CINNAMON, blockSettings(0f, 0f, BlockSoundGroup.GRASS, "cinnamon_sapling")));
        BlockList.CINNAMON_LOG = block("cinnamon_log",
                new CinnamonLogBlock(blockSettings(2.0f, 2.0f, BlockSoundGroup.WOOD, "cinnamon_log")));

        BlockList.SALT_ORE = block("salt_ore",
                new ExperienceDroppingBlock(UniformIntProvider.create(0, 2), blockSettings(3.0f, 3.0f, BlockSoundGroup.STONE, "salt_ore").requiresTool()));
        BlockList.DEEPSLATE_SALT_ORE = block("deepslate_salt_ore",
                new ExperienceDroppingBlock(UniformIntProvider.create(0, 2), blockSettings(4.5f, 3.0f, BlockSoundGroup.DEEPSLATE, "deepslate_salt_ore").requiresTool()));

        //crates
        BlockList.ASPARAGUS_CRATE = block("asparagus_crate",
                new Block(blockSettings(2.0f, 3.0f, BlockSoundGroup.WOOD, "asparagus_crate")));
        BlockList.SWEET_POTATO_CRATE = block("sweet_potato_crate",
                new Block(blockSettings(2.0f, 3.0f, BlockSoundGroup.WOOD, "sweet_potato_crate")));
        BlockList.CHILI_PEPPER_CRATE = block("chili_pepper_crate",
                new Block(blockSettings(2.0f, 3.0f, BlockSoundGroup.WOOD, "chili_pepper_crate")));

        //crops
        //TODO: Fix status effects
        BlockList.WILD_ASPARAGUS = block("wild_asparagus",
                new WildCropBlock(StatusEffects.ABSORPTION, 0, AbstractBlock.Settings.copy(Blocks.TALL_GRASS).registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(ExpandedDelight.MODID, "wild_asparagus")))));
        BlockList.WILD_SWEET_POTATO = block("wild_sweet_potatoes",
                new WildCropBlock(StatusEffects.ABSORPTION, 0, AbstractBlock.Settings.copy(Blocks.TALL_GRASS).registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(ExpandedDelight.MODID, "wild_sweet_potatoes")))));
        BlockList.WILD_CHILI_PEPPER = block("wild_chili_pepper",
                new WildCropBlock(StatusEffects.ABSORPTION, 0, AbstractBlock.Settings.copy(Blocks.TALL_GRASS).registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(ExpandedDelight.MODID, "wild_chili_pepper")))));
        BlockList.WILD_PEANUTS = block("wild_peanuts",
                new WildCropBlock(StatusEffects.ABSORPTION, 0, AbstractBlock.Settings.copy(Blocks.TALL_GRASS).registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(ExpandedDelight.MODID, "wild_peanuts")))));

        //ExpandedDelight.LOGGER.info("ExpandedDelight blocks loaded");
    }

    //crops
    public static final Block ASPARAGUS_CROP = withoutBlockItem("asparagus_crop",
            new DelightCropBlock(cropSettings("asparagus_crop")));
    public static final Block SWEET_POTATO_CROP = withoutBlockItem("sweet_potatoes_crop",
            new DelightCropBlock(cropSettings("sweet_potatoes_crop")));
    public static final Block CHILI_PEPPER_CROP = withoutBlockItem("chili_pepper_crop",
            new DelightCropBlock(cropSettings("chili_pepper_crop")));
    public static final Block PEANUT_CROP = withoutBlockItem("peanut_crop",
            new DelightCropBlock(cropSettings("peanut_crop")));

    //entities
    public static final Block MORTAR_AND_PESTLE = withoutBlockItem("mortar_and_pestle",
            new MortarPestleBlock(blockSettings(2.0f, 3.0f, BlockSoundGroup.STONE, "mortar_and_pestle").nonOpaque()));
    public static final Block JUICER = withoutBlockItem("juicer",
            new JuicerBlock(blockSettings(1.0f, 2.0f, BlockSoundGroup.WOOD, "juicer").nonOpaque()));


    private static AbstractBlock.Settings blockSettings(float hardness, float resistance, BlockSoundGroup sound, String name) {
        return AbstractBlock.Settings.create().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(ExpandedDelight.MODID, name))).strength(hardness, resistance).sounds(sound);
    }

    private static AbstractBlock.Settings cropSettings(String name) {
        return  AbstractBlock.Settings.copy(Blocks.WHEAT).sounds(BlockSoundGroup.CROP).breakInstantly().ticksRandomly().noCollision().nonOpaque().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(ExpandedDelight.MODID, name)));
    }

    private static Block block(String name, Block block) {
        blockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(ExpandedDelight.MODID, name), block);
    }

    private static Item blockItem(String name, Block block) {
        Item item = Registry.register(Registries.ITEM, Identifier.of(ExpandedDelight.MODID, name),
                new BlockItem(block, new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ExpandedDelight.MODID, name)))));
        ItemGroupEvents.modifyEntriesEvent(ExpandedDelight.GROUP).register(entries -> entries.add(item));
        return item;
    }

    private static Block withoutBlockItem(String name, Block block) {
        return Registry.register(Registries.BLOCK, Identifier.of(ExpandedDelight.MODID, name), block);
    }
}
