package com.ratesp.mod.mixins;

import com.ratesp.mod.RatESP;
import com.ratesp.mod.RatESPScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// We open the screen via tick in RatESP, but if needed we can hook here
@Mixin(MinecraftClient.class)
public class ClientMixin {
    // Reserved for future hooks
}
