package com.dnii.bows_reforged.mixin;

import com.dnii.bows_reforged.screen.FletchingScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.FletchingTableBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FletchingTableBlock.class)
public class FletchingTableBlockMixin {
	@Inject(at = @At("HEAD"), method = "onUse", cancellable = true)
	public void onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
		if (world.isClient) {
			// Prevents secondary behavior
			cir.setReturnValue(ActionResult.SUCCESS);
		}
		if (!world.isClient && player instanceof ServerPlayerEntity) {
			player.openHandledScreen(new SimpleNamedScreenHandlerFactory(
					(syncId, inv, p) -> new FletchingScreenHandler(syncId, inv),
					Text.literal("Modify Bow")
			));
			cir.setReturnValue(ActionResult.CONSUME);
		}
	}
}