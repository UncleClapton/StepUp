package com.nottoomanyitems.stepup.Client;

import com.nottoomanyitems.stepup.Client.worker.StepChanger;
import com.nottoomanyitems.stepup.StepUp;

import com.nottoomanyitems.stepup.config.StepUpConfig;
import com.nottoomanyitems.stepup.config.HudMode;
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

    @SubscribeEvent(receiveCanceled = true)
    public static void clientTickEvent(PlayerTickEvent event) {
        if (!init
                || event.player == null
        ) {
            return;
        }

        worker.onPlayerTick(event);
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

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onOverlayRender(RenderGameOverlayEvent.Post event) {
        if (!init
                || event.isCanceled()
                || StepUpConfig.hudMode == HudMode.NEVER
                || event.getType() != RenderGameOverlayEvent.ElementType.HOTBAR
        ) {
            return;
        }

        worker.drawOverlay(event);
    }
}
