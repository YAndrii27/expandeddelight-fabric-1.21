package com.ianm1647.expandeddelight.registry;

import com.ianm1647.expandeddelight.ExpandedDelight;
import com.ianm1647.expandeddelight.util.recipe.JuicerRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class RecipeRegistry {
    public static RecipeSerializer<JuicerRecipe> JUICER_SERIALIZER = Registry.register(Registries.RECIPE_SERIALIZER, Identifier.of(ExpandedDelight.MODID, "juicing"), new JuicerRecipe.Serializer());
    public static RecipeType<JuicerRecipe> JUICER_TYPE = Registry.register(Registries.RECIPE_TYPE, Identifier.of(ExpandedDelight.MODID, "juicing"), new RecipeType<>() {
        @Override
        public String toString() {
            return "juicing";
        }
    });


    public static void registerRecipes() {

//        JUICER_SERIALIZER = serializer("juicing", new JuicerRecipe.Serializer());
//        JUICER_TYPE = type("juicing", new RecipeType<>() {
//            @Override
//            public String toString() {
//                return "juicing";
//            }
//        });
    }

    public static <T extends Recipe<?>> RecipeType<T> type(String name, RecipeType<T> recipe) {
        return Registry.register(Registries.RECIPE_TYPE, Identifier.of(ExpandedDelight.MODID, name), recipe);
    }

    public static <T extends Recipe<?>> RecipeSerializer<T> serializer(String name, RecipeSerializer<T> recipe) {
        return Registry.register(Registries.RECIPE_SERIALIZER, Identifier.of(ExpandedDelight.MODID, name), recipe);
    }
}
