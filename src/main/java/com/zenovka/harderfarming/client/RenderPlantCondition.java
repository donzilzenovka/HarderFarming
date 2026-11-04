package com.zenovka.harderfarming.client;

import com.zenovka.harderfarming.TileEntityPlantCondition;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import org.lwjgl.opengl.GL11;

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
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_CURRENT_BIT | GL11.GL_COLOR_BUFFER_BIT);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glColor4f(plant.condition == TileEntityPlantCondition.DEAD ? .53f : .72f,
                plant.condition == TileEntityPlantCondition.DEAD ? .48f : .64f,
                plant.condition == TileEntityPlantCondition.DEAD ? .41f : .40f, 1f);
        Tessellator t = Tessellator.instance;
        t.startDrawingQuads();
        double u0 = icon.getMinU(), u1 = icon.getMaxU();
        double v0 = icon.getMinV(), v1 = icon.getMaxV();
        if (plant.originalRenderType == 6) {
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
        t.draw();
        GL11.glPopAttrib();
        GL11.glPopMatrix();
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
