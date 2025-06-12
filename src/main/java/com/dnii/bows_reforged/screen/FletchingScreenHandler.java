package com.dnii.bows_reforged.screen;

import com.dnii.bows_reforged.BowComponents;
import com.dnii.bows_reforged.BrItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class FletchingScreenHandler extends ScreenHandler {
    private static class NotifyingInventory extends SimpleInventory {
        private final ScreenHandler handler;

        public NotifyingInventory(ScreenHandler handler, int size) {
            super(size);
            this.handler = handler;
        }

        @Override
        public void markDirty() {
            super.markDirty();
            handler.onContentChanged(this);
        }
    }

    private boolean consumeSpyglass = false;
    private boolean consumeMaterial = false;

    private final Inventory inputInventory;
    private final Inventory outputInventory;

    public FletchingScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(ModScreenHandlers.FLETCHING_SCREEN_HANDLER, syncId);

        this.inputInventory = new NotifyingInventory(this, 4);
        this.outputInventory = new SimpleInventory(1);

        this.addSlot(new Slot(inputInventory, 0, 80, 12) { // spyglass
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.getItem() == Items.SPYGLASS;
            }
        });
        this.addSlot(new Slot(inputInventory, 1, 26, 48) { // fiber
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.getItem() == Items.STRING || stack.getItem() == BrItems.ARCANE_FIBER || stack.getItem() == BrItems.EXPLOSIVE_FIBER || stack.getItem() == BrItems.ELECTRIC_FIBER;
            }
        });
        this.addSlot(new Slot(inputInventory, 2, 44, 48) { // material
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isIn(ItemTags.STONE_TOOL_MATERIALS) || stack.getItem() == Items.IRON_INGOT || stack.getItem() == Items.GOLD_INGOT || stack.getItem() == Items.DIAMOND || stack.getItem() == Items.NETHERITE_INGOT;
            }
        });
        this.addSlot(new Slot(inputInventory, 3, 80, 48) { // base
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.getItem() == Items.BOW || stack.getItem() == BrItems.REFORGED_BOW;
            }
        });
        this.addSlot(new Slot(outputInventory, 0, 134, 48) { // output
            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }
            @Override
            public void onTakeItem(PlayerEntity player, ItemStack stack) {
                if (consumeSpyglass) {
                    if (!inputInventory.getStack(0).isEmpty()) {
                        inputInventory.getStack(0).decrement(1);
                    }
                }
                if (!inputInventory.getStack(1).isEmpty()) {
                    inputInventory.getStack(1).decrement(1);
                }
                if (consumeMaterial) {
                    if (!inputInventory.getStack(2).isEmpty()) {
                        inputInventory.getStack(2).decrement(1);
                    }
                }
                if (!inputInventory.getStack(3).isEmpty()) {
                    inputInventory.getStack(3).decrement(1);
                }
                onContentChanged(inputInventory);
            }
        });

        for (int i = 0; i < 3; ++i) { // Inventory
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) { // Hotbar
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        // Implement shift-click logic here (not needed)
        return ItemStack.EMPTY;
    }

    private String translate(ItemStack stack) {
        if (stack.isIn(ItemTags.STONE_TOOL_MATERIALS)) {
            return "stone";
        }
        if (stack.getItem() == Items.IRON_INGOT) {
            return "iron";
        }
        if (stack.getItem() == Items.GOLD_INGOT) {
            return "gold";
        }
        if (stack.getItem() == Items.DIAMOND) {
            return "diamond";
        }
        if (stack.getItem() == Items.NETHERITE_INGOT) {
            return "netherite";
        }
        return "none of the above";
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        super.onContentChanged(inventory);

        ItemStack input0 = inputInventory.getStack(0); // spyglass
        ItemStack input1 = inputInventory.getStack(1); // fiber
        ItemStack input2 = inputInventory.getStack(2); // material
        ItemStack input3 = inputInventory.getStack(3); // base

        ItemStack output = ItemStack.EMPTY;
        consumeSpyglass = true;
        consumeMaterial = true;

        if (!input3.isEmpty()) {
            if (input1.getItem() == Items.STRING) { // repair
                consumeSpyglass = false;
                if (input3.getItem() == BrItems.REFORGED_BOW) {
                    if (translate(input2).equals(input3.get(BowComponents.MATERIAL))) {
                        output = input3.copy();
                        output.setDamage(0);
                    }
                } else {
                    consumeMaterial = false;
                    output = input3.copy();
                    output.setDamage(0);
                }
            } else if (!input2.isEmpty() && input3.getItem() == Items.BOW) { // create
                output = new ItemStack(BrItems.REFORGED_BOW);
                output.set(BowComponents.AMMO, 0);
                if (input2.isIn(ItemTags.STONE_TOOL_MATERIALS)) {
                    output.set(BowComponents.MATERIAL, "stone");
                    output.set(BowComponents.DMG_BONUS, 1);
                    output.set(BowComponents.DRAW_TIME, 5.0f);
                }
                if (input2.getItem() == Items.IRON_INGOT) {
                    output.set(BowComponents.MATERIAL, "iron");
                    output.set(BowComponents.DMG_BONUS, 5);
                    output.set(BowComponents.DRAW_TIME, 20.0f);
                }
                if (input2.getItem() == Items.GOLD_INGOT) {
                    output.set(BowComponents.MATERIAL, "gold");
                    output.set(BowComponents.DMG_BONUS, 3);
                    output.set(BowComponents.DRAW_TIME, 1.0f);
                }
                if (input2.getItem() == Items.DIAMOND) {
                    output.set(BowComponents.MATERIAL, "diamond");
                    output.set(BowComponents.DMG_BONUS, 9);
                    output.set(BowComponents.DRAW_TIME, 30.0f);
                }
                if (input2.getItem() == Items.NETHERITE_INGOT) {
                    output.set(BowComponents.MATERIAL, "netherite");
                    output.set(BowComponents.DMG_BONUS, 7);
                    output.set(BowComponents.DRAW_TIME, 1.0f);
                }
                if (input1.isEmpty()) {
                    output.set(BowComponents.ATTRIBUTE, "none");
                } else if (input1.getItem() == BrItems.ARCANE_FIBER) {
                    output.set(BowComponents.ATTRIBUTE, "arcane");
                } else if (input1.getItem() == BrItems.ELECTRIC_FIBER) {
                    output.set(BowComponents.ATTRIBUTE, "electric");
                } else if (input1.getItem() == BrItems.EXPLOSIVE_FIBER) {
                    output.set(BowComponents.ATTRIBUTE, "explosive");
                }
                if (input0.isEmpty()) {
                    output.set(BowComponents.HAS_SCOPE, false);
                } else {
                    output.set(BowComponents.HAS_SCOPE, true);
                    output.set(BowComponents.DMG_BONUS, output.getOrDefault(BowComponents.DMG_BONUS, 0) + 3);
                    output.set(BowComponents.DRAW_TIME, output.getOrDefault(BowComponents.DRAW_TIME, 20.0f) + 5.0f);
                }
            }
        }

        outputInventory.setStack(0, output);
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);

        // Pop items only on server
        if (!player.getWorld().isClient) {
            for (int i = 0; i < inputInventory.size(); i++) {
                ItemStack stack = inputInventory.removeStack(i);
                if (!stack.isEmpty()) {
                    // Attempt to insert item into inventory
                    if (!player.getInventory().insertStack(stack)) {
                        player.dropItem(stack, false);
                    }
                }
            }
        }
    }
}
