package com.ratesp.mod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class RatESP implements ClientModInitializer {

    public static boolean enabled = true;
    public static boolean showTracers = true;
    public static boolean showBox = true;
    public static boolean showName = true;
    public static boolean showRadar = true;
    public static KeyBinding toggleKey;

    // Deep yellow ARGB
    public static final int COLOR = 0xFFFFBF00;

    @Override
    public void onInitializeClient() {
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.ratesp.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_INSERT,
                "category.ratesp"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.wasPressed()) {
                enabled = !enabled;
                if (client.player != null) {
                    client.player.sendMessage(
                        Text.literal("[RatESP] " + (enabled ? "§aEnabled" : "§cDisabled")), true
                    );
                }
            }
        });

        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            if (!enabled) return;
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.world == null || client.player == null) return;
            if (RatESPScreen.isOpen) return;

            float delta = tickDelta.getTickDelta(true);
            RatRenderer.render(drawContext, delta, client);
        });
    }

    public static boolean isRat(net.minecraft.entity.Entity entity) {
        if (!(entity instanceof BatEntity)) return false;
        if (!entity.hasCustomName()) return false;
        String name = entity.getCustomName().getString().toLowerCase();
        return name.contains("rat");
    }
}
