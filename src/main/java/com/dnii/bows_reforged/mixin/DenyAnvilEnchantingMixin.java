package com.dnii.bows_reforged.mixin;

import com.dnii.bows_reforged.ReforgedBowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.AnvilScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreenHandler.class)
public class DenyAnvilEnchantingMixin {
	@Inject(at = @At("HEAD"), method = "updateResult", cancellable = true)
	private void cancelEnchanting(CallbackInfo ci) {
		AnvilScreenHandler self = (AnvilScreenHandler)(Object) this;
		ItemStack left = self.getSlot(0).getStack();
		ItemStack right = self.getSlot(1).getStack();

		if (left.getItem() instanceof ReforgedBowItem) {
			if (right.isOf(Items.ENCHANTED_BOOK) || right.hasEnchantments()) {
				ci.cancel();
				self.getSlot(2).setStack(ItemStack.EMPTY);
			}
		}
	}
}