package com.zenovka.harderfarming.client;

import com.zenovka.harderfarming.TileEntityPlantCondition;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.BufferUtils;
import java.nio.FloatBuffer;

/** Preserve the original icon and choose standard crop (#) or crossed-plant (X) layout. */
public final class RenderPlantCondition extends TileEntitySpecialRenderer {
    @Override public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks) {
        if (!(tile instanceof TileEntityPlantCondition)) return;
        TileEntityPlantCondition plant = (TileEntityPlantCondition)tile;
        Block original = plant.original();
        if (original == null) return;
        IIcon icon = original.getIcon(0, plant.originalMetadata);
        if (icon == null) return;
        bindTexture(TextureMap.locationBlocksTexture);
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        // Farmland is 15/16 high. Match the vanilla crop sprite's vertical inset;
        // do not alter the independently captured selection bounding box.
        if (plant.originalRenderType == 6 && plant.getWorldObj() != null
                && plant.getWorldObj().getBlock(plant.xCoord, plant.yCoord - 1, plant.zCoord) == Blocks.farmland) {
            GL11.glTranslated(0.0, -1.0 / 16.0, 0.0);
        }
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_CURRENT_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_TEXTURE_BIT);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        if (plant.condition == TileEntityPlantCondition.DEAD) {
            // Single-pass colour interpolation: 68% straw, 32% original RGB.
            // Keep the texture alpha untouched. No coincident overlay polygons.
            FloatBuffer constant = BufferUtils.createFloatBuffer(4);
            constant.put(new float[]{0f, 0f, 0f, .68f}).flip();
            GL11.glTexEnv(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_COLOR, constant);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL13.GL_COMBINE);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_COMBINE_RGB, GL13.GL_INTERPOLATE);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_SOURCE0_RGB, GL13.GL_PRIMARY_COLOR);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_SOURCE1_RGB, GL11.GL_TEXTURE);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_SOURCE2_RGB, GL13.GL_CONSTANT);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND2_RGB, GL11.GL_SRC_ALPHA);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_COMBINE_ALPHA, GL11.GL_REPLACE);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_SOURCE0_ALPHA, GL11.GL_TEXTURE);
            GL11.glColor4f(.38f, .00f, .01f, 1f);
        } else {
            // The same single-pass desaturation approach, but with straw coloration.
            FloatBuffer constant = BufferUtils.createFloatBuffer(4);
            constant.put(new float[]{0f, 0f, 0f, .68f}).flip();
            GL11.glTexEnv(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_COLOR, constant);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL13.GL_COMBINE);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_COMBINE_RGB, GL13.GL_INTERPOLATE);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_SOURCE0_RGB, GL13.GL_PRIMARY_COLOR);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_SOURCE1_RGB, GL11.GL_TEXTURE);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_SOURCE2_RGB, GL13.GL_CONSTANT);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND2_RGB, GL11.GL_SRC_ALPHA);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_COMBINE_ALPHA, GL11.GL_REPLACE);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_SOURCE0_ALPHA, GL11.GL_TEXTURE);
            if (plant.recoveryBoosted) GL11.glColor4f(.56f, .70f, .39f, 1f);
            else GL11.glColor4f(.72f, .64f, .40f, 1f);
        }
        Tessellator t = Tessellator.instance;
        double u0 = icon.getMinU(), u1 = icon.getMaxU();
        double v0 = icon.getMinV(), v1 = icon.getMaxV();
        t.startDrawingQuads();
        drawPlanes(t, plant.originalRenderType, u0, u1, v0, v1);
        t.draw();
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }
    private static void drawPlanes(Tessellator t, int renderType,
                                   double u0, double u1, double v0, double v1) {
        if (renderType == 6) {
            // Vanilla crop layout: two planes along X and two along Z, inset by 1/4 block.
            // Full-height quads preserve the original sprite's transparent padding.
            plane(t, .0, 0, .25, 1.0, 1.0, .25, u0, u1, v0, v1);
            plane(t, .0, 0, .75, 1.0, 1.0, .75, u0, u1, v0, v1);
            plane(t, .25, 0, .0, .25, 1.0, 1.0, u0, u1, v0, v1);
            plane(t, .75, 0, .0, .75, 1.0, 1.0, u0, u1, v0, v1);
        } else {
            // Render type 1 and unsupported custom types use crossed-plane fallback.
            plane(t, .0, 0, .0, 1.0, 1.0, 1.0, u0, u1, v0, v1);
            plane(t, 1.0, 0, .0, .0, 1.0, 1.0, u0, u1, v0, v1);
        }
    }
    private static void plane(Tessellator t, double x0, double y0, double z0,
                              double x1, double y1, double z1,
                              double u0, double u1, double v0, double v1) {
        t.addVertexWithUV(x0, y1, z0, u0, v0);
        t.addVertexWithUV(x0, y0, z0, u0, v1);
        t.addVertexWithUV(x1, y0, z1, u1, v1);
        t.addVertexWithUV(x1, y1, z1, u1, v0);
        t.addVertexWithUV(x1, y1, z1, u1, v0);
        t.addVertexWithUV(x1, y0, z1, u1, v1);
        t.addVertexWithUV(x0, y0, z0, u0, v1);
        t.addVertexWithUV(x0, y1, z0, u0, v0);
    }
}
