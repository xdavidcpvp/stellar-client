
package dev.stellar;

import dev.stellar.gui.StellarScreen;
import dev.stellar.module.Module;
import dev.stellar.module.ModuleManager;
import dev.stellar.spotify.SpotifyService;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class StellarClient implements ClientModInitializer {
    public static final String ID = "stellar";
    private static KeyBinding openGui;

    @Override
    public void onInitializeClient() {
        ModuleManager.init();
        SpotifyService.get().start();

        // NOTE: if Category.MISC does not exist in your mappings, create one with KeyBinding.Category.create(...)
        openGui = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.stellar.gui", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, KeyBinding.Category.MISC));

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            while (openGui.wasPressed()) mc.setScreen(new StellarScreen());
        });

        HudElementRegistry.attachElementAfter(VanillaHudElements.CHAT,
                Identifier.of(ID, "hud"), (ctx, tick) -> renderHud(ctx));
    }

    private static void renderHud(DrawContext ctx) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.options.hudHidden) return;
        for (Module m : ModuleManager.all()) if (m.enabled) m.render(ctx);
    }
}


