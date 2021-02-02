package com.nottoomanyitems.stepup.Client;

import com.nottoomanyitems.stepup.StepUp;

import com.nottoomanyitems.stepup.config.StepUpConfig;
import com.nottoomanyitems.stepup.util.HudMode;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = StepUp.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {
	public static boolean init = false;
	private static StepChanger worker;

    public static void init () {
        worker = new StepChanger();
    }

    @SubscribeEvent(priority= EventPriority.NORMAL, receiveCanceled=true)
    public static void clientTickEvent(final PlayerTickEvent event) {
        if (!init) {
            return;
        }

        if (event.player == null) {
            return;
        }

        worker.TickEvent(event);
    }
    
    @SubscribeEvent
    public static void onKeyInput(KeyInputEvent e) {
        if (!init) {
            return;
        }

        worker.onKeyInput();
    }

    @SubscribeEvent
    public static void onWorldJoin(WorldEvent.Load e) {
        init = true;
        worker.init();
    }

    @SubscribeEvent
    public static void onOverlayRender(RenderGameOverlayEvent.Post event) {
        if (!init || event.isCanceled()) {
            return;
        }

        if (StepUpConfig.hudMode == HudMode.NEVER) {
            return;
        }

        if (event.getType() != RenderGameOverlayEvent.ElementType.HOTBAR) {
            return;
        }

        worker.drawOverlay(event);
    }
}
