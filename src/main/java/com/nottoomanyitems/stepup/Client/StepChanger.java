package com.nottoomanyitems.stepup.Client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.nottoomanyitems.stepup.StepUp;
import com.nottoomanyitems.stepup.config.StepUpConfig;

import com.nottoomanyitems.stepup.util.HudMode;
import com.nottoomanyitems.stepup.util.ResourceDrawer;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;

public class StepChanger {
    private boolean stepUpState = true;
    private float modStepHeight = 1.25F;
    private static final float DEFAULT_STEP_HEIGHT = 0.6F;
    private static final int DYNAMIC_DRAW_MAX_OFFSET = 48; // 48px
    private static final int DYNAMIC_DRAW_ANIM_INC = 8; // 8px/t

    private static final int DYNAMIC_DRAW_MAX_TIME = 80; // render ticks, 5sec

    private boolean lastStepUpState;
    private int dynDrawTime;
    private int dynDrawYOffset = DYNAMIC_DRAW_MAX_OFFSET;

    private static final ResourceDrawer mapIcons = new ResourceDrawer("minecraft", "textures/map/map_icons.png", 128, 128, 8,8);
    private static final ResourceDrawer hudIcons = new ResourceDrawer(StepUp.MOD_ID, "textures/step.png", 20, 11, 10, 11);

    public StepChanger() { }

    public void TickEvent(PlayerTickEvent event) {
        PlayerEntity player = event.player;
        float currentStep = player.stepHeight;

		if (getStepUpState(player)) {
		    if (currentStep != modStepHeight) {
                player.stepHeight = modStepHeight;
            }
        } else {
		    if (currentStep != DEFAULT_STEP_HEIGHT) {
                player.stepHeight = DEFAULT_STEP_HEIGHT;
            }
        }
    }

    public void init() {
        stepUpState = StepUpConfig.defaultJumpState;
        modStepHeight = StepUpConfig.stepHeight;
    }

    public void onKeyInput() {
        if (KeyBindings.KEYBINDINGS[0].isPressed()) {
            stepUpState = !stepUpState;
        }
    }

    public void drawOverlay(RenderGameOverlayEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();

        PlayerEntity player = minecraft.player;
        if (player == null) {
            return;
        }

        MainWindow window = minecraft.getMainWindow();
        MatrixStack matrixStack = event.getMatrixStack();

        int x = (window.getScaledWidth() / 2) + 92 + StepUpConfig.hudXOffset;
        int y = (window.getScaledHeight() - ForgeIngameGui.right_height) + 4 + StepUpConfig.hudYOffset;

        if (StepUpConfig.hudMode == HudMode.ALWAYS) {
            drawStatusIcon(minecraft, player, matrixStack, x, y);
        } else {
            drawDynamicOverlay(minecraft, player, matrixStack, x, y);
        }
    }

    public boolean getStepUpState(PlayerEntity player) {
        return stepUpState && !player.isCrouching();
    }

    public void drawDynamicOverlay(Minecraft minecraft, PlayerEntity player, MatrixStack matrixStack, int x, int y) {
        boolean curState = getStepUpState(player);

        if (curState != lastStepUpState) {
            lastStepUpState = curState;
            dynDrawTime = DYNAMIC_DRAW_MAX_TIME;
        }

        // If we need to render or if still on screen
        if (dynDrawTime >= 0 || dynDrawYOffset < DYNAMIC_DRAW_MAX_OFFSET) {
            if (dynDrawYOffset == 0 && dynDrawTime >= 0) { // If the overlay is fully open and there's time left
                if (StepUpConfig.hudMode == HudMode.CHANGE) { // If the overlay is in "CHANGE" mode, count down unless crouching.
                    if (player.isCrouching()) { // If crouching, reset render timer.
                        dynDrawTime = DYNAMIC_DRAW_MAX_TIME;
                    } else {
                        dynDrawTime -= 1;
                    }
                }

                if (StepUpConfig.hudMode == HudMode.ON_ONLY && !curState) { // If overlay is in "OFF_ONLY" mode, close the overlay if StepUp is active.
                    dynDrawTime = -1;
                }

                if (StepUpConfig.hudMode == HudMode.OFF_ONLY && curState) { // If overlay is in "OFF_ONLY" mode, close the overlay if StepUp is active.
                    dynDrawTime = -1;
                }
            } else if (dynDrawTime < 0) { // If there's no time left, increment offset until max_offset.
                dynDrawYOffset = Math.min(dynDrawYOffset + DYNAMIC_DRAW_ANIM_INC, DYNAMIC_DRAW_MAX_OFFSET);
            } else { // Else, decrement offset until 0
                dynDrawYOffset = Math.max(dynDrawYOffset - DYNAMIC_DRAW_ANIM_INC, 0);
            }

            // Render
            drawStatusIcon(minecraft, player, matrixStack, x, y + dynDrawYOffset);
        }



    }

    public void drawStatusIcon(Minecraft minecraft, PlayerEntity player, MatrixStack matrixStack, int x, int y) {
        // Render main icon
        hudIcons.bindTexture(minecraft);
        hudIcons.drawResourceAtPos(matrixStack, x, y, 0, 0);
        hudIcons.drawResourceAtPos(matrixStack, x, y, 1, 0);

        // Render disabled "X"
        if (!getStepUpState(player)) {
            mapIcons.bindTexture(minecraft);
            mapIcons.drawResourceAtPos(matrixStack, x + 4,y + 5, 10,1);
        }
    }
}
