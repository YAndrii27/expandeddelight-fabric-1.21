package com.ianm1647.expandeddelight.integration.rei;

import com.ianm1647.expandeddelight.ExpandedDelight;
import com.ianm1647.expandeddelight.integration.rei.juicing.JuicingRecipeDisplay;
import com.ianm1647.expandeddelight.registry.RecipeRegistry;
import com.ianm1647.expandeddelight.util.recipe.JuicerRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry;
import me.shedaniel.rei.api.common.plugins.REICommonPlugin;
import me.shedaniel.rei.api.common.registry.display.ServerDisplayRegistry;
import net.minecraft.util.Identifier;
import vectorwing.farmersdelight.common.registry.ModRecipeTypes;

public class ExpandedDelightREICommon implements REICommonPlugin {
    public static final CategoryIdentifier<JuicingRecipeDisplay> JUICING = CategoryIdentifier.of(ExpandedDelight.MODID, "juicing");

    @Override
    public void registerDisplaySerializer(DisplaySerializerRegistry registry) {
        registry.register(Identifier.of(ExpandedDelight.MODID, "juicing"), JuicingRecipeDisplay.SERIALIZER);
    }

    @Override
    public void registerDisplays(ServerDisplayRegistry registry) {
        registry.beginRecipeFiller(JuicerRecipe.class)
                .filterType(RecipeRegistry.JUICER_TYPE)
                .fill(JuicingRecipeDisplay::new);
    }
}