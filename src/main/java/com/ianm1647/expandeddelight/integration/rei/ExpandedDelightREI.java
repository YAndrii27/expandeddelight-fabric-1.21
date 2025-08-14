package com.ianm1647.expandeddelight.integration.rei;

import com.ianm1647.expandeddelight.ExpandedDelight;
import com.ianm1647.expandeddelight.block.BlockList;
import com.ianm1647.expandeddelight.integration.rei.juicing.JuicingRecipeCategory;
import com.ianm1647.expandeddelight.integration.rei.juicing.JuicingRecipeDisplay;
import com.ianm1647.expandeddelight.registry.RecipeRegistry;
import com.ianm1647.expandeddelight.screen.custom.JuicerScreen;
import com.ianm1647.expandeddelight.screen.custom.JuicerScreenHandler;
import com.ianm1647.expandeddelight.util.recipe.JuiceRecipeInput;
import com.ianm1647.expandeddelight.util.recipe.JuicerRecipe;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;

public class ExpandedDelightREI implements REIClientPlugin {
    public static final CategoryIdentifier<JuicingRecipeDisplay> JUICING = CategoryIdentifier.of(ExpandedDelight.MODID, "juicing");

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new JuicingRecipeCategory());
        registry.addWorkstations(JUICING, EntryStacks.of(BlockList.JUICER));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        // TODO: figure something out for bring compatibility with REI back

//        registry.registerRecipeFiller(JuicerRecipe.class, RecipeRegistry.JUICER_TYPE, JuicingRecipeDisplay::new);
//        registry.beginRecipeFiller(JuicingRecipeDisplay.class).filterType();
    }

    @Override
    public void registerScreens(ScreenRegistry registry) {
        registry.registerContainerClickArea(new Rectangle(79, 35, 24, 17), JuicerScreen.class, JUICING);
    }
}