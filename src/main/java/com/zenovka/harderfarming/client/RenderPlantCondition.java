package com.zenovka.harderfarming.client;

import com.zenovka.harderfarming.TileEntityPlantCondition;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import org.lwjgl.opengl.GL11;

/** Draw the captured crop's own atlas icon as crossed plant planes. */
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
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_LIGHTING);
        float r = plant.condition == TileEntityPlantCondition.DEAD ? 0.53f : 0.72f;
        float g = plant.condition == TileEntityPlantCondition.DEAD ? 0.48f : 0.64f;
        float b = plant.condition == TileEntityPlantCondition.DEAD ? 0.41f : 0.40f;
        GL11.glColor4f(r, g, b, 1f);
        Tessellator t = Tessellator.instance;
        t.startDrawingQuads();
        double u0 = icon.getMinU(), u1 = icon.getMaxU();
        double v0 = icon.getMinV(), v1 = icon.getMaxV();
        double h = Math.max(.0625, plant.maxY - plant.minY);
        double base = plant.minY;
        double leftX = plant.minX, rightX = plant.maxX;
        double leftZ = plant.minZ, rightZ = plant.maxZ;
        quad(t, leftX, base, leftZ, rightX, base + h, rightZ, u0, u1, v0, v1);
        quad(t, rightX, base, leftZ, leftX, base + h, rightZ, u0, u1, v0, v1);
        t.draw();
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glPopMatrix();
    }
    private static void quad(Tessellator t, double x0, double y0, double z0,
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
