package com.ianm1647.expandeddelight.integration.rei.juicing;

import com.google.common.collect.ImmutableList;
import com.ianm1647.expandeddelight.integration.rei.ExpandedDelightREI;
import com.ianm1647.expandeddelight.util.recipe.JuicerRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.InputIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.display.RecipeDisplay;
import net.minecraft.recipe.display.SlotDisplay;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Environment(EnvType.CLIENT)
public class JuicingRecipeDisplay extends BasicDisplay implements RecipeDisplay {
    private final EntryIngredient bottleOutput;
    private final int cookTime;
    private final ItemStack output;

//    private final int width;

    public JuicingRecipeDisplay(RecipeEntry<JuicerRecipe> recipe) {
        super(List.of(EntryIngredients.ofIngredients(recipe.value().getIngredients()).get(0), EntryIngredients.ofIngredients(recipe.value().getIngredients()).get(1)), List.of(EntryIngredient.of(EntryStacks.of(recipe.value().output()))));

//        super(EntryIngredients.ofIngredients(recipe.getIngredients()), Collections.singletonList(EntryIngredients.of(recipe.getOutput())), Optional.of(Identifier.of(ExpandedDelight.MODID, "juicing")));
        this.bottleOutput = EntryIngredients.of(recipe.value().getBottle());
        this.cookTime = recipe.value().getCookTime();
        this.output = recipe.value().output();
    }

    public CategoryIdentifier<?> getCategoryIdentifier() {
        return ExpandedDelightREI.JUICING;
    }

    @Override
    public Optional<Identifier> getDisplayLocation() {
        return Optional.empty();
    }

    public List<EntryIngredient> getInputEntries() {
        List<EntryIngredient> inputEntryList = new ArrayList<>(super.getInputEntries());
        inputEntryList.add(this.bottleOutput);
        return ImmutableList.copyOf(inputEntryList);
    }

    @Override
    public List<InputIngredient<EntryStack<?>>> getInputIngredients(@Nullable ScreenHandler menu, @Nullable PlayerEntity player) {
        return super.getInputIngredients(menu, player);
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return List.of();
    }

    @Override
    public List<EntryIngredient> getRequiredEntries() {
        return super.getRequiredEntries();
    }

    public List<EntryIngredient> getIngredientEntries() {
        return super.getInputEntries();
    }

    public EntryIngredient getContainerOutput() {
        return this.bottleOutput;
    }

    public int getCookTime() {
        return this.cookTime;
    }

    @Override
    public @Nullable DisplaySerializer<JuicingRecipeDisplay> getSerializer() {
        return null;
    }

    @Override
    public SlotDisplay result() {
        return null;
    }

    @Override
    public SlotDisplay craftingStation() {
        return null;
    }

    @Override
    public Serializer<? extends RecipeDisplay> serializer() {
        return null;
    }
}