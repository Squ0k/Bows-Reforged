package com.dnii.bows_reforged;

import com.dnii.bows_reforged.screen.FletchingScreen;
import com.dnii.bows_reforged.screen.ModScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.entity.ArrowEntityRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;


public class BowsReforgedClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		HandledScreens.register(ModScreenHandlers.FLETCHING_SCREEN_HANDLER, FletchingScreen::new);
		EntityRendererRegistry.register(BrEntities.ARCANE_ARROW, ArrowEntityRenderer::new);
		EntityRendererRegistry.register(BrEntities.ELECTRIC_ARROW, ArrowEntityRenderer::new);
		EntityRendererRegistry.register(BrEntities.EXPLOSION_ARROW, ArrowEntityRenderer::new);

		ModelPredicateProviderRegistry.register(BrItems.REFORGED_BOW, Identifier.ofVanilla("pulling"),
				(stack, world, entity, seed) ->
					(entity != null && entity.isUsingItem() && entity.getActiveItem() == stack && hasAmmo(stack, entity)) ? 1.0F : 0.0F
		);
		ModelPredicateProviderRegistry.register(BrItems.REFORGED_BOW, Identifier.ofVanilla("pull"),
				(stack, world, entity, seed) -> {
					if (entity == null || entity.getActiveItem() != stack || !hasAmmo(stack, entity)) return 0.0F;
					return (stack.getMaxUseTime(entity) - entity.getItemUseTimeLeft()) / stack.getOrDefault(BowComponents.DRAW_TIME, 20.0f);
				}
		);
		ModelPredicateProviderRegistry.register(BrItems.REFORGED_BOW, Identifier.of("bows-reforged", "material"),
				(stack, world, entity, seed) -> {
					String material = stack.getOrDefault(BowComponents.MATERIAL, "undefined");
					return switch (material) {
						case "stone" -> 0.1F;
						case "iron" -> 0.2F;
						case "gold" -> 0.3F;
						case "diamond" -> 0.4F;
						case "netherite" -> 0.5F;
						default -> 0.0F;
					};
				}
		);
		ModelPredicateProviderRegistry.register(BrItems.REFORGED_BOW, Identifier.of("bows-reforged", "attribute"),
				(stack, world, entity, seed) -> {
					String attribute = stack.getOrDefault(BowComponents.ATTRIBUTE, "none");
					return switch (attribute) {
						case "arcane" -> 0.1F;
						case "electric" -> 0.2F;
						case "explosive" -> 0.3F;
						default -> 0.0F;
					};
				}
		);
		ModelPredicateProviderRegistry.register(BrItems.REFORGED_BOW, Identifier.of("bows-reforged", "has_scope"),
				(stack, world, entity, seed) -> {
					boolean has_scope = stack.getOrDefault(BowComponents.HAS_SCOPE, false);
					return has_scope? 1.0F : 0.0F;
				}
		);
	}

	public static boolean hasAmmo(ItemStack stack, LivingEntity entity) {
		if (!(entity instanceof PlayerEntity player)) {return (stack.getOrDefault(BowComponents.AMMO, 0) > 0);}
		return (stack.getOrDefault(BowComponents.AMMO, 0) > 0 || player.getAbilities().creativeMode);
	}
}