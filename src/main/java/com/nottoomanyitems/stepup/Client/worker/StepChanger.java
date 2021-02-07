package com.nottoomanyitems.stepup.Client.worker;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.nottoomanyitems.stepup.Client.KeyBindings;
import com.nottoomanyitems.stepup.StepUp;
import com.nottoomanyitems.stepup.config.StepUpConfig;

import com.nottoomanyitems.stepup.config.HudMode;
import com.nottoomanyitems.stepup.util.DrawHelper;
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
    private static final int DYNAMIC_DRAW_MAX_OFFSET = 12; // 12px
    private static final int DYNAMIC_DRAW_ANIM_INC = 3; // 3px/t (4 ticks)

    private static final int DYNAMIC_DRAW_MAX_TIME = 80; // render ticks, 5sec

    private boolean lastStepUpState;
    private int dynDrawTime;
    private int dynDrawPosOffset = DYNAMIC_DRAW_MAX_OFFSET;

    private static final ResourceDrawer mapIcons = new ResourceDrawer("minecraft", "textures/map/map_icons.png", 128, 128, 8,8);
    private static final ResourceDrawer hudIcons = new ResourceDrawer(StepUp.MOD_ID, "textures/step.png", 20, 11, 10, 11);

    public StepChanger() { }

    public void init() {
        stepUpState = StepUpConfig.defaultStepUpState;
        modStepHeight = StepUpConfig.stepHeight;
    }

    public void onPlayerTick(PlayerTickEvent event) {
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

    public void onKeyInput() {
        if (KeyBindings.KEYBINDINGS[0].isPressed()) {
            stepUpState = !stepUpState;
        }
    }

    public boolean getStepUpState(PlayerEntity player) {
        return stepUpState && !player.isCrouching();
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
        int y = (window.getScaledHeight() - ForgeIngameGui.right_height) + 2 + StepUpConfig.hudYOffset;

        if (player.isCreative()) { // Place down a little bit if in creative.
            y += 15;
        }

        if (HudMode.isDynamic(StepUpConfig.hudMode)) {
            drawDynamicOverlay(player, matrixStack, x, y);
        } else {
            drawStatusIcon(player, matrixStack, x, y);
        }
    }

    public void drawDynamicOverlay(PlayerEntity player, MatrixStack matrixStack, int x, int y) {
        boolean curState = getStepUpState(player);

        if (curState != lastStepUpState) {
            lastStepUpState = curState;
            dynDrawTime = DYNAMIC_DRAW_MAX_TIME;
        }

        if (dynDrawTime >= 0 || dynDrawPosOffset < DYNAMIC_DRAW_MAX_OFFSET) { // If we need to render or if still on screen
            if (dynDrawPosOffset == 0 && dynDrawTime >= 0) { // If the overlay is fully open and there's time left
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
                dynDrawPosOffset = Math.min(dynDrawPosOffset + DYNAMIC_DRAW_ANIM_INC, DYNAMIC_DRAW_MAX_OFFSET);
            } else { // Else, decrement offset until 0
                dynDrawPosOffset = Math.max(dynDrawPosOffset - DYNAMIC_DRAW_ANIM_INC, 0);
            }

            // Render
            float alphaVal = 1.0F - ((float) dynDrawPosOffset / DYNAMIC_DRAW_MAX_OFFSET); // Fade in as we slide in.
            DrawHelper.enableAlpha(alphaVal);
            drawStatusIcon(player, matrixStack, x - dynDrawPosOffset, y);
            DrawHelper.disableAlpha(alphaVal);
        }



    }

    public void drawStatusIcon(PlayerEntity player, MatrixStack matrixStack, int x, int y) {
        // Render main icon
        hudIcons.bindTexture();
        hudIcons.drawResourceAtPos(matrixStack, x, y, 0, 0);
        hudIcons.drawResourceAtPos(matrixStack, x, y, 1, 0);

        // Render disabled "X"
        if (!getStepUpState(player)) {
            mapIcons.bindTexture();
            mapIcons.drawResourceAtPos(matrixStack, x + 4,y + 5, 10,1);
        }
    }
}
