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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
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
        progress = nbt.getInt("growth_chamber.progress");
        maxProgress = nbt.getInt("growth_chamber.max_progress");
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
        return this.getWorld().getRecipeManager()
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

//    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(5, ItemStack.EMPTY);
//
//    protected final PropertyDelegate propertyDelegate;
//    private int progress = 0;
//    private int maxProgress = 200;
//    private RecipeType<? extends JuicerRecipe> recipeType;
//
//    public JuicerBlockEntity(BlockPos blockPos, BlockState blockState) {
//        super(BlockEntityRegistry.JUICER, blockPos, blockState);
//
//        this.propertyDelegate = new PropertyDelegate() {
//            public int get(int index) {
//                return switch (index) {
//                    case 0 -> JuicerBlockEntity.this.progress;
//                    case 1 -> JuicerBlockEntity.this.maxProgress;
//                    default -> 0;
//                };
//            }
//
//            public void set(int index, int value) {
//                switch (index) {
//                    case 0 -> JuicerBlockEntity.this.progress = value;
//                    case 1 -> JuicerBlockEntity.this.maxProgress = value;
//                }
//            }
//
//            public int size() {
//                return 2;
//            }
//        };
//    }
//
//    @Override
//    public DefaultedList<ItemStack> getItems() {
//        return inventory;
//    }
//
//    @Override
//    public Text getDisplayName() {
//        return Text.translatable("container.expandeddelight.juicer");
//    }
//
//    @Nullable
//    @Override
//    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
//        return new JuicerScreenHandler(syncId, inv, this, this.propertyDelegate);
//    }
//
//    @Override
//    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
//        super.writeNbt(nbt, registryLookup);
//        Inventories.writeNbt(nbt, inventory, registryLookup);
//        nbt.putInt("juicer.progress", progress);
//    }
//
////    @Override
////    protected void writeNbt(NbtCompound nbt) {
////        super.writeNbt(nbt);
////
////    }
//
//    @Override
//    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
//        Inventories.readNbt(nbt, inventory, registryLookup);
//        super.readNbt(nbt, registryLookup);
////        super.readNbt(nbt);
//        progress = nbt.getInt("juicer.progress");
//    }
//
////    @Override
////    public void readNbt(NbtCompound nbt) {
////
////    }
//
//    public static void tick(World world, BlockPos pos, BlockState state, JuicerBlockEntity entity) {
//        if(hasRecipe(entity)) {
//            entity.progress++;
//            if(entity.progress > entity.maxProgress) {
//                craftItem(entity);
//            }
//        } else {
//            entity.resetProgress();
//        }
//        if (!entity.getStack(3).isEmpty()) {
//            if (!entity.getStack(2).isEmpty()) {
//                entity.useStoredBottleOnJuice();
//            }
//        }
//    }
//
//    private static void craftItem(JuicerBlockEntity entity) {
//        World world = entity.world;
////        SimpleInventory inventory = new SimpleInventory(entity.inventory.size());
//
//        DefaultedList<ItemStack> inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);
////
////        for (int i = 0; i < entity.inventory.size(); i++) {
////            inventory.setStack(i, entity.getStack(i));
////        }
//
//        Optional<RecipeEntry<JuicerRecipe>> match = world.getRecipeManager()
//                .getFirstMatch(RecipeRegistry.JUICER_TYPE, new JuiceRecipeInput(inventory.get(0)), world);
//
//        if(match.isPresent()) {
//            entity.removeStack(0,1);
//            entity.removeStack(1, 1);
//
//            entity.setStack(3, new ItemStack(match.getOutput().getItem(),
//                    entity.getStack(3).getCount() + 1));
//
//            entity.resetProgress();
//        }
//    }
//
//    private static boolean hasRecipe(JuicerBlockEntity entity) {
//        World world = entity.world;
//
//        if (world == null) {
//            return false;
//        }
//
//        SimpleInventory inventory = new SimpleInventory(entity.inventory.size());
//
//        for (int i = 0; i < entity.inventory.size(); i++) {
//            inventory.setStack(i, entity.getStack(i));
//        }
//
//        Optional<JuicerRecipe> match = world.getRecipeManager()
//                .getFirstMatch(RecipeRegistry.JUICER_TYPE, inventory, world);
//
//        return match.isPresent() && canInsertAmountIntoOutputSlot(inventory)
//                && canInsertItemIntoOutputSlot(inventory, match.get().getOutput());
//    }
//
//    private static boolean canInsertItemIntoOutputSlot(SimpleInventory inventory, ItemStack output) {
//        return inventory.getStack(3).getItem() == output.getItem() || inventory.getStack(3).isEmpty();
//    }
//
//    private static boolean canInsertAmountIntoOutputSlot(SimpleInventory inventory) {
//        return inventory.getStack(3).getMaxCount() > inventory.getStack(3).getCount();
//    }
//
//    private void resetProgress() {
//        this.progress = 0;
//    }
//
//    /* from CookingPotBlockEntity class */
//
//    private void useStoredBottleOnJuice() {
//        ItemStack juiceDisplay = this.getStack(3);
//        ItemStack bottleInput = this.getStack(2);
//        ItemStack finalOutput = this.getStack(4);
//        if (bottleInput.isOf(Items.GLASS_BOTTLE) && finalOutput.getCount() < finalOutput.getMaxCount()) {
//            int smallerStack = Math.min(juiceDisplay.getCount(), bottleInput.getCount());
//            int juiceCount = Math.min(smallerStack, juiceDisplay.getMaxCount() - finalOutput.getCount());
//            if (finalOutput.isEmpty()) {
//                bottleInput.decrement(juiceCount);
//                this.setStack(4, juiceDisplay.split(juiceCount));
//            } else if (finalOutput.getItem() == juiceDisplay.getItem()) {
//                juiceDisplay.decrement(juiceCount);
//                bottleInput.decrement(juiceCount);
//                finalOutput.increment(juiceCount);
//            }
//        }
//
//    }
//
//    public ItemStack useBottleOnJuice(ItemStack container) {
//        if (container.isOf(Items.GLASS_BOTTLE) && !this.getStack(3).isEmpty()) {
//            container.decrement(1);
//            return this.getStack(3).split(1);
//        } else {
//            return ItemStack.EMPTY;
//        }
//    }
//
//    public DefaultedList<ItemStack> getDroppableInventory() {
//        DefaultedList<ItemStack> drops = DefaultedList.of();
//        for (int i = 0; i < 5; ++i) {
//            drops.add(i == 3 ? ItemStack.EMPTY : this.getStack(i));
//        }
//        return drops;
//    }


}
