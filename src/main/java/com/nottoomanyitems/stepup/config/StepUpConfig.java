package com.nottoomanyitems.stepup.config;

import com.nottoomanyitems.stepup.util.HudMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.lang3.tuple.Pair;

public class StepUpConfig {
    public static boolean defaultJumpState;
    public static float stepHeight;
    public static HudMode hudMode;
    public static int hudXOffset;
    public static int hudYOffset;

    private static Pair<ClientConfig, ForgeConfigSpec> configSpecPair;

    public static void init() {
        FMLJavaModLoadingContext.get().getModEventBus().register(StepUpConfig.class);

        configSpecPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);

        ModLoadingContext modLoadingContext = ModLoadingContext.get();
        modLoadingContext.registerConfig(ModConfig.Type.CLIENT, configSpecPair.getRight());
    }

    @SubscribeEvent
    public static void reload(ModConfig.ModConfigEvent event) {
        ModConfig config = event.getConfig();

        if (config.getSpec() == configSpecPair.getRight()) {
            ClientConfig nextConf = configSpecPair.getLeft();

            defaultJumpState = nextConf.defaultJumpState.get();

            // Step height is converted to a float with 2 decimals because reasons
            // it is also bound to values between 0.6f and 255.0f
            stepHeight = Math.min(Math.max(Math.round(nextConf.stepHeight.get().floatValue() * 100.0f) / 100.0f, 0.6f), 255.0f);

            hudMode = nextConf.hudMode.get();

            hudXOffset = nextConf.hudXOffset.get();
            hudYOffset = nextConf.hudYOffset.get();
        }
    }

    private static class ClientConfig {
        private final ForgeConfigSpec.BooleanValue defaultJumpState;
        private final ForgeConfigSpec.DoubleValue stepHeight;
        private final ForgeConfigSpec.ConfigValue<HudMode> hudMode;
        private final ForgeConfigSpec.IntValue hudXOffset;
        private final ForgeConfigSpec.IntValue hudYOffset;

        private ClientConfig(ForgeConfigSpec.Builder builder) {
            defaultJumpState = builder
                    .comment("Default state of StepUp when joining a world. (Default: true)")
                    .define("default_state", true);

            stepHeight = builder
                    .comment("Block height the player is able to climb without jumping while StepUp is active. (Default: 1.25)")
                    .defineInRange("step_height",1.25, 0.6, 255.0);

            hudMode = builder
                    .comment(
                            "Display indicator on hud when StepUp is enabled/disabled. (Default: \"ALWAYS\")",
                            " - \"ALWAYS\" Always display the indicator.",
                            " - \"CHANGE\" Display the indicator temporarily when StepUp is toggled.",
                            " - \"ON_ONLY\" Display the indicator when StepUp is enabled.",
                            " - \"OFF_ONLY\" Display the indicator when StepUp is disabled.",
                            " - \"NEVER\" NEVER display the UI indicator."
                    ).defineEnum("overlay_mode", HudMode.ALWAYS, HudMode.values());

            hudXOffset = builder
                    .comment("Overlay x (horizontal) offset. In scaled pixels relative to it's normal position. (Default: 0)")
                    .defineInRange("overlay_offset_x", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);

            hudYOffset = builder
                    .comment("Overlay y (vertical) offset. In scaled pixels relative to it's normal position. (Default: 0)")
                    .defineInRange("overlay_offset_y", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);

        }
    }
}
