package com.dnii.bows_reforged.screen;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class FletchingScreen extends HandledScreen<FletchingScreenHandler> {
    private static final Identifier TEXTURE = Identifier.of("bows-reforged", "textures/gui/fletch_ui.png");

    public FletchingScreen(FletchingScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 166;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta); // draw dark background
        super.render(context, mouseX, mouseY, delta); // handles slots, buttons, etc.
        this.drawMouseoverTooltip(context, mouseX, mouseY); // tooltips

        // Check if mouse is hovering over the icon
        int iconX = x + 134;
        int iconY = y + 12;
        if (mouseX >= iconX && mouseX <= iconX + 18 && mouseY >= iconY && mouseY <= iconY + 18) {
            context.drawTooltip(this.textRenderer, List.of(
                    Text.literal("To repair: insert bow, string & corresponding material."),
                    Text.literal("To modify: insert bow, material, fiber (optional) & spyglass (optional).")
                    ), mouseX, mouseY);
        }
    }
}