package com.ianm1647.expandeddelight.util.recipe;

import com.ianm1647.expandeddelight.registry.RecipeRegistry;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.RecipeBookCategories;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import vectorwing.farmersdelight.common.registry.ModRecipeBookCategories;

import java.util.List;
import java.util.Optional;

public record JuicerRecipe(Ingredient inputA, Ingredient inputB, ItemStack output) implements Recipe<JuiceRecipeInput> {
    @Override
    public boolean matches(JuiceRecipeInput input, World world) {
        if (world.isClient) {
            return false;
        }

        boolean firstSlot = inputA.test(input.getStackInSlot(0));
//        System.out.println(input.getStackInSlot(0));
        boolean secondSlot = inputB.test(input.getStackInSlot(1));
//        System.out.println(input.getStackInSlot(1));
        return firstSlot && secondSlot;
    }

    @Override
    public ItemStack craft(JuiceRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        return output.copy();
    }

//    @Override
//    public boolean fits(int width, int height) {
//        return true;
//    }

//    @Override
//    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
//        return output;
//    }

    public ItemStack getBottle() {
        return Items.GLASS_BOTTLE.getDefaultStack();
    }

    public int getCookTime() {
        return 200;
    }

    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> list = DefaultedList.of();
        list.add(inputA);
        list.add(inputB);
        return list;
    }

    @Override
    public IngredientPlacement getIngredientPlacement() {
        return IngredientPlacement.forMultipleSlots(List.of(Optional.of(inputA), Optional.of(inputB)));
    }

    @Override
    public RecipeBookCategory getRecipeBookCategory() {
        return ModRecipeBookCategories.COOKING_DRINKS.get();
    }

    @Override
    public RecipeSerializer<? extends Recipe<JuiceRecipeInput>> getSerializer() {
        return RecipeRegistry.JUICER_SERIALIZER;
    }

    @Override
    public RecipeType<? extends Recipe<JuiceRecipeInput>> getType() {
        return RecipeRegistry.JUICER_TYPE;
    }

    public static class Serializer implements RecipeSerializer<JuicerRecipe> {
        public static final MapCodec<JuicerRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC.fieldOf("inputA").forGetter(JuicerRecipe::inputA),
                Ingredient.CODEC.fieldOf("inputB").forGetter(JuicerRecipe::inputB),
                ItemStack.CODEC.fieldOf("output").forGetter(JuicerRecipe::output)
        ).apply(inst, JuicerRecipe::new));

        public static final PacketCodec<RegistryByteBuf, JuicerRecipe> STREAM_CODEC =
                PacketCodec.tuple(
                        Ingredient.PACKET_CODEC, JuicerRecipe::inputA,
                        Ingredient.PACKET_CODEC, JuicerRecipe::inputB,
                        ItemStack.PACKET_CODEC, JuicerRecipe::output,
                        JuicerRecipe::new);

        @Override
        public MapCodec<JuicerRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, JuicerRecipe> packetCodec() {
            return STREAM_CODEC;
        }
    }
}
