package com.ianm1647.expandeddelight.world.feature.tree;

import com.ianm1647.expandeddelight.world.feature.ModConfiguredFeatures;
//import net.minecraft.block.sapling.SaplingGenerator;
//import net.minecraft.block.SaplingGenerator;
import net.minecraft.block.SaplingGenerator;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ModSaplingGenerators {
    public static final SaplingGenerator CINNAMON = new SaplingGenerator("cinnamon", 0f, Optional.empty(), Optional.empty(), Optional.of(ModConfiguredFeatures.CINNAMON_TREE), Optional.empty(), Optional.empty(), Optional.empty());
}

//public class CinnamonSaplingGenerator extends SaplingGenerator {
//    @Nullable
//    @Override
//    protected RegistryKey<ConfiguredFeature<?, ?>> getTreeFeature(Random random, boolean bees) {
//        return ModConfiguredFeatures.CINNAMON_TREE;
//    }
//}
