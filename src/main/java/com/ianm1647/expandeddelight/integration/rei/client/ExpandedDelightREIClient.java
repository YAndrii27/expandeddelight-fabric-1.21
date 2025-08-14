package com.ianm1647.expandeddelight.integration.rei.client;

import com.ianm1647.expandeddelight.ExpandedDelight;
import com.ianm1647.expandeddelight.block.BlockList;
import com.ianm1647.expandeddelight.integration.rei.juicing.JuicingRecipeDisplay;
import com.ianm1647.expandeddelight.screen.custom.JuicerScreen;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;

public class ExpandedDelightREIClient implements REIClientPlugin {
    public static final CategoryIdentifier<JuicingRecipeDisplay> JUICING = CategoryIdentifier.of(ExpandedDelight.MODID, "juicing");

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new JuicingRecipeCategory());

        registry.addWorkstations(JUICING, EntryStacks.of(BlockList.JUICER));
    }

    @Override
    public void registerScreens(ScreenRegistry registry) {
        registry.registerContainerClickArea(new Rectangle(79, 35, 24, 17), JuicerScreen.class, JUICING);
    }
}
