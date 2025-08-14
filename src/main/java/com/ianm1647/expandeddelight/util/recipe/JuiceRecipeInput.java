package com.ianm1647.expandeddelight.util.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;

public record JuiceRecipeInput(ItemStack inputA, ItemStack inputB) implements RecipeInput {
    @Override
    public ItemStack getStackInSlot(int slot) {

//        System.out.println(input);

        return switch (slot) {
            case 0 -> inputA;
            case 1 -> inputB;
            default -> throw new IllegalStateException("Unexpected value: " + slot);
        };
    }

    @Override
    public int size() {
        return 2;
    }
}
