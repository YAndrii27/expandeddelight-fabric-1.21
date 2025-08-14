package com.ianm1647.expandeddelight.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.UseAction;
import vectorwing.farmersdelight.common.item.ConsumableItem;

public class JuiceItem extends ConsumableItem {
    public JuiceItem(Item.Settings settings) {
        super(settings);
    }

    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }
}