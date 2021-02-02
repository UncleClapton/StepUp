package com.nottoomanyitems.stepup.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;

public class ResourceDrawer {
    public ResourceLocation location;
    public Dimension2D textureSize;
    public Dimension2D uvSize;


    public ResourceDrawer (String namespace, String path, int textureWidth, int textureHeight, int uvWidth, int uvHeight) {
        location = new ResourceLocation(namespace, path);
        textureSize = new Dimension2D(textureWidth, textureHeight);
        uvSize = new Dimension2D(uvWidth, uvHeight);
    }

    public void bindTexture(Minecraft mc) {
        mc.getTextureManager().bindTexture(location);
    }

    public void drawResourceAtPos (MatrixStack matrixStack, int x, int y, int column, int row) {
        AbstractGui.blit(
                matrixStack,
                x,
                y,
                uvSize.width * column,
                uvSize.height * row,
                uvSize.width,
                uvSize.height,
                textureSize.width,
                textureSize.height
        );
    }

}
