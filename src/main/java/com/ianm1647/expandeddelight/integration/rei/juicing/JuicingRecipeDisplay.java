package com.ianm1647.expandeddelight.integration.rei.juicing;

import com.google.common.collect.ImmutableList;
import com.ianm1647.expandeddelight.integration.rei.ExpandedDelightREICommon;
import com.ianm1647.expandeddelight.registry.BlockRegistry;
import com.ianm1647.expandeddelight.util.recipe.JuicerRecipe;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.InputIngredient;
import me.shedaniel.rei.api.common.util.CollectionUtils;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.display.RecipeDisplay;
import net.minecraft.recipe.display.SlotDisplay;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JuicingRecipeDisplay extends BasicDisplay implements RecipeDisplay {

    public static final DisplaySerializer<JuicingRecipeDisplay> SERIALIZER = DisplaySerializer.of(
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                                    EntryIngredient
                                            .codec()
                                            .listOf()
                                            .fieldOf("inputs")
                                            .forGetter(JuicingRecipeDisplay::getInputEntries),
                                    EntryIngredient
                                            .codec()
                                            .listOf()
                                            .fieldOf("outputs")
                                            .forGetter(JuicingRecipeDisplay::getOutputEntries),
                                    EntryIngredient
                                            .codec()
                                            .fieldOf("bottleOutput")
                                            .forGetter(JuicingRecipeDisplay::getBottleOutput),
                                    Identifier.CODEC
                                            .optionalFieldOf("location")
                                            .forGetter(JuicingRecipeDisplay::getDisplayLocation),
                                    Codec.INT.fieldOf("cookTime").forGetter(JuicingRecipeDisplay::getCookTime)
                            ).apply(instance, JuicingRecipeDisplay::new)
            ),
            PacketCodec.tuple(
                    EntryIngredient.streamCodec().collect(PacketCodecs.toList()),
                    JuicingRecipeDisplay::getInputEntries,
                    EntryIngredient.streamCodec().collect(PacketCodecs.toList()),
                    JuicingRecipeDisplay::getOutputEntries,
                    EntryIngredient.streamCodec(),
                    JuicingRecipeDisplay::getBottleOutput,
                    PacketCodecs.optional(Identifier.PACKET_CODEC),
                    JuicingRecipeDisplay::getDisplayLocation,
                    PacketCodecs.INTEGER,
                    JuicingRecipeDisplay::getCookTime,
                    JuicingRecipeDisplay::new
            )
    );

    private final EntryIngredient bottleOutput;
    private final int cookTime;

    public JuicingRecipeDisplay(RecipeEntry<JuicerRecipe> recipe) {
        this(
                CollectionUtils.map(
                        recipe.value().getIngredients(),
                        EntryIngredients::ofIngredient
                ),
                List.of(EntryIngredients.of(recipe.value().output())),
                EntryIngredients.of(recipe.value().getBottle()),
                Optional.of(recipe.id().getValue()),
                recipe.value().getCookTime()
        );
    }

    public JuicingRecipeDisplay(
            List<EntryIngredient> inputs,
            List<EntryIngredient> outputs,
            EntryIngredient bottleOutput,
            Optional<Identifier> location,
            int cookTime
    ) {
        super(inputs, outputs, location);
        this.bottleOutput = bottleOutput;
        this.cookTime = cookTime;
        this.outputs = outputs;
    }

    public CategoryIdentifier<?> getCategoryIdentifier() {
        return ExpandedDelightREICommon.JUICING;
    }

    @Override
    public Optional<Identifier> getDisplayLocation() {
        return Optional.empty();
    }

    public List<EntryIngredient> getInputEntries() {
        List<EntryIngredient> inputEntryList = new ArrayList<>(super.getInputEntries());
//        inputEntryList.add(this.bottleOutput);
        return ImmutableList.copyOf(inputEntryList);
    }

    @Override
    public List<InputIngredient<EntryStack<?>>> getInputIngredients(@Nullable ScreenHandler menu, @Nullable PlayerEntity player) {
        return super.getInputIngredients(menu, player);
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

    public EntryIngredient getBottleOutput() {
        return this.bottleOutput;
    }

    @Override
    public @Nullable DisplaySerializer<JuicingRecipeDisplay> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public SlotDisplay result() {
        return null;
    }

    @Override
    public SlotDisplay craftingStation() {
        return new SlotDisplay.ItemSlotDisplay(BlockRegistry.JUICER.asItem());
    }

    @Override
    public Serializer<? extends RecipeDisplay> serializer() {
        return null;
    }
}