package com.dnii.bows_reforged.mixin.client;

import com.dnii.bows_reforged.BowComponents;
import com.dnii.bows_reforged.ReforgedBowItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.client.render.GameRenderer;

import static com.dnii.bows_reforged.BowsReforgedClient.hasAmmo;

@Mixin(GameRenderer.class)
public class ChangeFOV {
	@Shadow
	@Final
	MinecraftClient client;

	@Inject(at = @At("RETURN"), method = "getFov", cancellable = true)
	private void modifyFov(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Double> cir) {
		ClientPlayerEntity player = client.player;
		if (player == null) return;

		ItemStack held = player.getActiveItem();
		if (held.getItem() instanceof ReforgedBowItem && player.isUsingItem() && hasAmmo(held, player)) {
			float progress = MathHelper.clamp(player.getItemUseTime() / held.getOrDefault(BowComponents.DRAW_TIME, 20.0f),
					0.0f, 1.0f);

			float ease = (progress * progress + progress * 2.0f) / 3.0f;
			ease = MathHelper.clamp(ease, 0.0f, 1.0f);

			float zoom;
			if (held.getOrDefault(BowComponents.HAS_SCOPE, false)) {
				zoom = MathHelper.lerp(ease, 1.0f, 0.2f);
			} else {
				zoom = MathHelper.lerp(ease, 1.0f, 0.9f);
			}

			cir.setReturnValue(cir.getReturnValue() * zoom);
		}
	}
}