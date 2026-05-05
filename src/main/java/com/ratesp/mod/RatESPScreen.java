package com.ratesp.mod;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class RatESPScreen extends Screen {

    public static boolean isOpen = false;

    public RatESPScreen() {
        super(Text.literal("§6RatESP Settings"));
    }

    @Override
    protected void init() {
        isOpen = true;
        int x = width / 2 - 75;
        int y = height / 2 - 70;

        addDrawableChild(ButtonWidget.builder(
            Text.literal(toggle("ESP", RatESP.enabled)),
            btn -> {
                RatESP.enabled = !RatESP.enabled;
                btn.setMessage(Text.literal(toggle("ESP", RatESP.enabled)));
            }
        ).dimensions(x, y, 150, 20).build());

        addDrawableChild(ButtonWidget.builder(
            Text.literal(toggle("Tracers", RatESP.showTracers)),
            btn -> {
                RatESP.showTracers = !RatESP.showTracers;
                btn.setMessage(Text.literal(toggle("Tracers", RatESP.showTracers)));
            }
        ).dimensions(x, y + 25, 150, 20).build());

        addDrawableChild(ButtonWidget.builder(
            Text.literal(toggle("Box", RatESP.showBox)),
            btn -> {
                RatESP.showBox = !RatESP.showBox;
                btn.setMessage(Text.literal(toggle("Box", RatESP.showBox)));
            }
        ).dimensions(x, y + 50, 150, 20).build());

        addDrawableChild(ButtonWidget.builder(
            Text.literal(toggle("Name + Distance", RatESP.showName)),
            btn -> {
                RatESP.showName = !RatESP.showName;
                btn.setMessage(Text.literal(toggle("Name + Distance", RatESP.showName)));
            }
        ).dimensions(x, y + 75, 150, 20).build());

        addDrawableChild(ButtonWidget.builder(
            Text.literal(toggle("Radar", RatESP.showRadar)),
            btn -> {
                RatESP.showRadar = !RatESP.showRadar;
                btn.setMessage(Text.literal(toggle("Radar", RatESP.showRadar)));
            }
        ).dimensions(x, y + 100, 150, 20).build());

        addDrawableChild(ButtonWidget.builder(
            Text.literal("§cClose"),
            btn -> close()
        ).dimensions(x, y + 130, 150, 20).build());
    }

    private String toggle(String label, boolean state) {
        return label + ": " + (state ? "§aON" : "§cOFF");
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);

        // Panel background
        int px = width / 2 - 90;
        int py = height / 2 - 85;
        context.fill(px, py, px + 180, py + 175, 0xCC000000);

        // Title
        context.drawCenteredTextWithShadow(textRenderer, "§6§lRatESP §7Settings", width / 2, py + 6, 0xFFFFBF00);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        isOpen = false;
        super.close();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
