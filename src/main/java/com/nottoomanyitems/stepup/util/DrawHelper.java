package com.nottoomanyitems.stepup.util;

import com.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;

/*
 * Helper functions `enableAlpha` and `disableAlpha` are taken from AppleSkin to help us control transparency.
 *
 * color4f has been deprecated by Mojang, but there is no direct replacement yet.
 */
@SuppressWarnings("deprecation")
public class DrawHelper {
    public static void enableAlpha(float alpha) {
        RenderSystem.enableBlend();

        if (alpha == 1.0F) {
            return;
        }

        RenderSystem.color4f(1.0F, 1.0F,  1.0F, alpha);
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    public static void disableAlpha(float alpha) {
        RenderSystem.disableBlend();

        if (alpha == 1.0F) {
            return;
        }

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
