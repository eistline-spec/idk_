package com.example.spawnerhighlight;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class SpawnerHighlightScreen extends Screen {

    private static final int PANEL_WIDTH  = 220;
    private static final int PANEL_HEIGHT = 120;

    private ButtonWidget toggleButton;
    private final Screen parent;

    public SpawnerHighlightScreen(Screen parent) {
        super(Text.literal("Spawner Highlight Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int cx = this.width  / 2;
        int cy = this.height / 2;

        toggleButton = ButtonWidget.builder(
                getToggleLabel(),
                btn -> {
                    SpawnerHighlightConfig cfg = SpawnerHighlightConfig.getInstance();
                    cfg.enabled = !cfg.enabled;
                    cfg.save();
                    btn.setMessage(getToggleLabel());
                }
        ).dimensions(cx - 80, cy - 14, 160, 24).build();
        this.addDrawableChild(toggleButton);

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Done"),
                btn -> this.close()
        ).dimensions(cx - 50, cy + 20, 100, 20).build());
    }

    private Text getToggleLabel() {
        boolean on = SpawnerHighlightConfig.getInstance().enabled;
        return Text.literal("Spawner Highlight: " + (on ? "§aON" : "§cOFF"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);

        int cx = this.width  / 2;
        int cy = this.height / 2;
        int x  = cx - PANEL_WIDTH  / 2;
        int y  = cy - PANEL_HEIGHT / 2;

        context.fill(x, y, x + PANEL_WIDTH, y + PANEL_HEIGHT, 0xCC000000);
        context.drawBorder(x, y, PANEL_WIDTH, PANEL_HEIGHT, 0xFFFF4444);

        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.literal("§c⚠ §lSpawner Highlight§r §c⚠"),
                cx, y + 12, 0xFFFFFF
        );

        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.literal("§7Press H to toggle this menu"),
                cx, y + 26, 0xAAAAAA
        );

        if (toggleButton != null) {
            toggleButton.setMessage(getToggleLabel());
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void close() {
        assert this.client != null;
        this.client.setScreen(parent);
    }
}
