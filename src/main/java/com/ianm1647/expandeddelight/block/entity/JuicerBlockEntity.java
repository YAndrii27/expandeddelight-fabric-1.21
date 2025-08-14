package com.ianm1647.expandeddelight.block.entity;

import com.ianm1647.expandeddelight.block.entity.custom.ImplementedInventory;
import com.ianm1647.expandeddelight.registry.BlockEntityRegistry;
import com.ianm1647.expandeddelight.registry.RecipeRegistry;
import com.ianm1647.expandeddelight.screen.custom.JuicerScreenHandler;
import com.ianm1647.expandeddelight.util.recipe.JuiceRecipeInput;
import com.ianm1647.expandeddelight.util.recipe.JuicerRecipe;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.recipebook.ClientRecipeManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.ServerRecipeManager;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class JuicerBlockEntity extends BlockEntity implements ImplementedInventory, ExtendedScreenHandlerFactory<BlockPos> {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(5, ItemStack.EMPTY);

    private static final int INPUT_A = 0;
    private static final int INPUT_B = 1;
    private static final int INPUT_BOTTLE = 2;
    private static final int OUTPUT_DISPLAY = 3;
    private static final int OUTPUT = 4;

    protected final PropertyDelegate propertyDelegate;
    private int progress = 0;
    private int maxProgress = 200;

    public JuicerBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.JUICER, pos, state);
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> JuicerBlockEntity.this.progress;
                    case 1 -> JuicerBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0: JuicerBlockEntity.this.progress = value;
                    case 1: JuicerBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int size() {
                return 2;
            }
        };
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayerEntity player) {
        return this.pos;
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.expandeddelight.juicer");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new JuicerScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, inventory, registryLookup);
        nbt.putInt("growth_chamber.progress", progress);
        nbt.putInt("growth_chamber.max_progress", maxProgress);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        Inventories.readNbt(nbt, inventory, registryLookup);
        progress = nbt.getInt("growth_chamber.progress", 0);
        maxProgress = nbt.getInt("growth_chamber.max_progress", this.maxProgress);
        super.readNbt(nbt, registryLookup);
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if(hasRecipe()) {
            increaseCraftingProgress();
            markDirty(world, pos, state);

            if(hasCraftingFinished()) {
                craftItem();
                resetProgress();
            }
        } else {
            resetProgress();
        }
    }

    private void resetProgress() {
        this.progress = 0;
        this.maxProgress = 200;
    }

    private void craftItem() {
        Optional<RecipeEntry<JuicerRecipe>> recipe = getCurrentRecipe();

        if (recipe.isPresent()) {
            ItemStack output = recipe.get().value().output();
            this.removeStack(INPUT_A, 1);
            this.removeStack(INPUT_B, 1);
            this.removeStack(INPUT_BOTTLE, 1);
            this.setStack(OUTPUT, new ItemStack(output.getItem(),
                    this.getStack(OUTPUT).getCount() + output.getCount()));
        }
    }

    private boolean hasCraftingFinished() {
        return this.progress >= this.maxProgress;
    }

    private void increaseCraftingProgress() {
        this.progress++;
    }

    private boolean hasRecipe() {
        if (!world.isClient) {
            Optional<RecipeEntry<JuicerRecipe>> recipe = getCurrentRecipe();
//        System.out.println(recipe);
            if (recipe.isEmpty()) {
                return false;
            }

            ItemStack output = recipe.get().value().output();
            return canInsertAmountIntoOutputSlot(output.getCount()) && canInsertItemIntoOutputSlot(output);
        }
        return false;
    }

    private Optional<RecipeEntry<JuicerRecipe>> getCurrentRecipe() {
//        System.out.println(inventory.get(INPUT_A));
//        System.out.println(inventory.get(INPUT_B));

//        System.out.println(this.getWorld().getRecipeManager().listAllOfType(RecipeRegistry.JUICER_TYPE));
        return ((ServerWorld) this.getWorld()).getRecipeManager()
                .getFirstMatch(RecipeRegistry.JUICER_TYPE, new JuiceRecipeInput(inventory.get(INPUT_A), inventory.get(INPUT_B)), this.getWorld());
    }

    private boolean canInsertItemIntoOutputSlot(ItemStack output) {
        return this.getStack(OUTPUT).isEmpty() || this.getStack(3).getItem() == output.getItem();
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        int maxCount = this.getStack(OUTPUT).isEmpty() ? 64 : this.getStack(OUTPUT).getMaxCount();
        int currentCount = this.getStack(OUTPUT).getCount();

        return maxCount >= currentCount + count;
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }

    public DefaultedList<ItemStack> getDroppableInventory() {
        DefaultedList<ItemStack> drops = DefaultedList.of();
        for (int i = 0; i < 5; ++i) {
            drops.add(i == 3 ? ItemStack.EMPTY : this.getStack(i));
        }
        return drops;
    }
}
