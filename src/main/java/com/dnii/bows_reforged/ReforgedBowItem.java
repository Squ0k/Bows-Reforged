package com.dnii.bows_reforged;

import net.fabricmc.fabric.api.item.v1.EnchantingContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;

public class ReforgedBowItem extends BowItem {
    public ReforgedBowItem(Settings settings) {
        super(settings);
    }
    private int holdDelay = 0;

    public static float calcPullProgress(int useTicks, float ticksNeeded) {
        float f = useTicks / ticksNeeded;
        f = (f * f + f * 2.0f) / 3.0f;
        if (f > 1.0f) {
            f = 1.0f;
        }
        return f;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        boolean creative = user.getAbilities().creativeMode;

        if (stack.getOrDefault(BowComponents.AMMO, 0) > 0 || creative) {
            // Inject phantom arrow to allow vanilla draw animation
            ItemStack record = user.getInventory().getStack(0);
            user.getInventory().setStack(0, new ItemStack(Items.ARROW, 1));

            TypedActionResult<ItemStack> result = super.use(world, user, hand);

            user.getInventory().setStack(0, record);
            return result;
        }
        return TypedActionResult.fail(stack);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        super.usageTick(world, user, stack, remainingUseTicks);

        if (!world.isClient && user instanceof PlayerEntity player) {
            if (stack.getOrDefault(BowComponents.DRAW_TIME, 20.0f) == 1.0f) {
                holdDelay = 1 - holdDelay;
                if (holdDelay > 0) return;

                boolean creative = player.getAbilities().creativeMode;
                if (stack.getOrDefault(BowComponents.AMMO, 0) == 0 && !creative) return;

                // Summon Arrow
                ArrowEntity arrow;
                if (Objects.equals(stack.getOrDefault(BowComponents.ATTRIBUTE, "none"), "arcane")) {
                    arrow = new ArcaneArrowEntity(BrEntities.ARCANE_ARROW, world);
                } else if (Objects.equals(stack.getOrDefault(BowComponents.ATTRIBUTE, "none"), "electric")) {
                    arrow = new ElectricArrowEntity(BrEntities.ELECTRIC_ARROW, world);
                } else if (Objects.equals(stack.getOrDefault(BowComponents.ATTRIBUTE, "none"), "explosive")) {
                    arrow = new ExplosionArrowEntity(BrEntities.EXPLOSION_ARROW, world);
                } else {
                    arrow = new ArrowEntity(EntityType.ARROW, world);
                }

                // Modify Arrow
                arrow.setVelocity(player, player.getPitch(), player.getYaw(), 0.0F, 3.0F, 1.0F);
                arrow.setCritical(false);
                arrow.setDamage(arrow.getDamage() + stack.getOrDefault(BowComponents.DMG_BONUS, 0)); // Extra damage

                arrow.setOwner(player);
                arrow.pickupType = creative? PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY : PersistentProjectileEntity.PickupPermission.ALLOWED;
                arrow.setPosition(player.getX(), player.getEyeY() - 0.1, player.getZ());
                world.spawnEntity(arrow);

                // Play Sound
                world.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F,
                        1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);

                // Update Inventory
                if (!creative) {
                    int extraDamage = switch (stack.getOrDefault(BowComponents.MATERIAL, "undefined")) {
                        case "stone" -> 9;
                        case "iron" -> 3;
                        case "gold" -> 6;
                        case "diamond" -> 2;
                        // case "netherite" -> 1;
                        default -> 1;
                    };
                    stack.damage(extraDamage, player, (user.getActiveHand() == Hand.MAIN_HAND) ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
                    stack.set(BowComponents.AMMO, stack.getOrDefault(BowComponents.AMMO, 0) - 1);
                }
            }
        }
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity player)) return;
        holdDelay = 0;

        float pullProgress = calcPullProgress(this.getMaxUseTime(stack, player) - remainingUseTicks,
                stack.getOrDefault(BowComponents.DRAW_TIME, 20.0f));
        if (pullProgress < 0.1F) return;

        boolean creative = player.getAbilities().creativeMode;

        if (stack.getOrDefault(BowComponents.AMMO, 0) == 0 && !creative) return;
        if (stack.getOrDefault(BowComponents.DRAW_TIME, 20.0f) == 1.0f) return;

        if (!world.isClient) {
            // Summon Arrow
            ArrowEntity arrow;
            if (Objects.equals(stack.getOrDefault(BowComponents.ATTRIBUTE, "none"), "arcane")) {
                arrow = new ArcaneArrowEntity(BrEntities.ARCANE_ARROW, world);
            } else if (Objects.equals(stack.getOrDefault(BowComponents.ATTRIBUTE, "none"), "electric")) {
                arrow = new ElectricArrowEntity(BrEntities.ELECTRIC_ARROW, world);
            } else if (Objects.equals(stack.getOrDefault(BowComponents.ATTRIBUTE, "none"), "explosive")) {
                arrow = new ExplosionArrowEntity(BrEntities.EXPLOSION_ARROW, world);
            } else {
                arrow = new ArrowEntity(EntityType.ARROW, world);
            }

            // Modify Arrow
            arrow.setVelocity(player, player.getPitch(), player.getYaw(), 0.0F,
                    pullProgress * 3.0F * (stack.getOrDefault(BowComponents.HAS_SCOPE, false)? 1.5F : 1.0F),
                    stack.getOrDefault(BowComponents.HAS_SCOPE, false)? 0.0F : 1.0F);
            arrow.setCritical(pullProgress == 1.0F);
            arrow.setDamage(arrow.getDamage() + stack.getOrDefault(BowComponents.DMG_BONUS, 0)); // Extra damage

            arrow.setOwner(player);
            arrow.pickupType = creative? PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY : PersistentProjectileEntity.PickupPermission.ALLOWED;
            arrow.setPosition(player.getX(), player.getEyeY() - 0.1, player.getZ());
            world.spawnEntity(arrow);

            // Play Sound
            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F,
                    1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + pullProgress * 0.5F);

            // Update Inventory
            if (!creative) {
                int extraDamage = switch (stack.getOrDefault(BowComponents.MATERIAL, "undefined")) {
                    case "stone" -> 9;
                    case "iron" -> 3;
                    case "gold" -> 6;
                    case "diamond" -> 2;
                    // case "netherite" -> 1;
                    default -> 1;
                };
                stack.damage(extraDamage, player, (user.getActiveHand() == Hand.MAIN_HAND)? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
                stack.set(BowComponents.AMMO, stack.getOrDefault(BowComponents.AMMO, 0) - 1);
            }
        }

        player.incrementStat(Stats.USED.getOrCreateStat(this));
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canBeEnchantedWith(ItemStack stack, RegistryEntry<Enchantment> enchantment, EnchantingContext context) {
        return false;
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return false;
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        if (clickType == ClickType.RIGHT && !player.getWorld().isClient) {
            if (player.currentScreenHandler instanceof ScreenHandler handler) {
                if (handler.getCursorStack().isEmpty()) {
                    if (stack.getOrDefault(BowComponents.AMMO, 0) > 64) {
                        player.currentScreenHandler.setCursorStack(new ItemStack(Items.ARROW, 64));
                        stack.set(BowComponents.AMMO, stack.getOrDefault(BowComponents.AMMO, 0) - 64);
                    } else if (stack.getOrDefault(BowComponents.AMMO, 0) > 0) {
                        player.currentScreenHandler.setCursorStack(new ItemStack(Items.ARROW, stack.getOrDefault(BowComponents.AMMO, 0)));
                        stack.set(BowComponents.AMMO, 0);
                    }
                    handler.sendContentUpdates(); // sync changes to the client
                } else if (handler.getCursorStack().getItem() == Items.ARROW) {
                    stack.set(BowComponents.AMMO, stack.getOrDefault(BowComponents.AMMO, 0) + handler.getCursorStack().getCount());
                    player.currentScreenHandler.setCursorStack(ItemStack.EMPTY);
                }
            }
            return true; // cancel default item behavior
        }
        return false;
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = new ItemStack(this);
        stack.set(BowComponents.MATERIAL, "undefined");
        stack.set(BowComponents.ATTRIBUTE, "none");
        stack.set(BowComponents.AMMO, 0);
        stack.set(BowComponents.HAS_SCOPE, false);
        stack.set(BowComponents.DMG_BONUS, 0);
        stack.set(BowComponents.DRAW_TIME, 20.0F);
        return stack;

    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        String material = stack.getOrDefault(BowComponents.MATERIAL, "undefined");
        tooltip.add(Text.translatable("item.bows-reforged.material.info", material).formatted(Formatting.RED));
        String attribute = stack.getOrDefault(BowComponents.ATTRIBUTE, "none");
        tooltip.add(Text.translatable("item.bows-reforged.attribute.info", attribute).formatted(Formatting.GOLD));
        int dmg_bonus = stack.getOrDefault(BowComponents.DMG_BONUS, 0);
        tooltip.add(Text.translatable("item.bows-reforged.dmg_bonus.info", dmg_bonus).formatted(Formatting.GREEN, Formatting.ITALIC));
        float draw_time = stack.getOrDefault(BowComponents.DRAW_TIME, 20.0F);
        tooltip.add(Text.translatable("item.bows-reforged.draw_time.info", draw_time).formatted(Formatting.GREEN, Formatting.ITALIC));
        if (stack.getOrDefault(BowComponents.HAS_SCOPE, false)) {
            tooltip.add(Text.translatable("item.bows-reforged.has_scope.info").formatted(Formatting.GREEN, Formatting.ITALIC));
        }
        int ammo = stack.getOrDefault(BowComponents.AMMO, 0);
        tooltip.add(Text.translatable("item.bows-reforged.ammo.info", ammo).formatted(Formatting.BLUE, Formatting.ITALIC));
    }
}