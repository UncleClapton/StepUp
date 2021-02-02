package com.nottoomanyitems.stepup.Client;

import com.nottoomanyitems.stepup.StepUp;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = StepUp.MOD_ID, bus = Bus.MOD, value = Dist.CLIENT)
public class KeyBindings {

    public static final KeyBinding[] KEYBINDINGS = {
        new KeyBinding("key.stepupplus.desc", GLFW.GLFW_KEY_H, "key.categories.stepupplus")
    };

    @SubscribeEvent
    public static void registerKeyBindings(final FMLClientSetupEvent event) {
        for (final KeyBinding bind : KEYBINDINGS) {
            ClientRegistry.registerKeyBinding(bind);
        }
    }

}