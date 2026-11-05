package com.zenovka.harderfarming.client;

import com.zenovka.harderfarming.HarderFarming;
import com.zenovka.harderfarming.CropMortality;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public final class CropInspector {
    @SubscribeEvent
    public void draw(RenderGameOverlayEvent.Text event) {
        if (!HarderFarming.debugHud) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null || mc.objectMouseOver == null ||
                mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) return;
        MovingObjectPosition hit = mc.objectMouseOver;
        World w = mc.theWorld;
        int x = hit.blockX, y = hit.blockY, z = hit.blockZ;
        Block b = w.getBlock(x, y, z);
        boolean dead = b == HarderFarming.witheredCrop;
        boolean eligible = !dead && b instanceof net.minecraft.block.BlockCrops &&
                w.getBlock(x, y - 1, z) == Blocks.farmland;
        if (!dead && !eligible) return;
        FontRenderer fr = mc.fontRenderer;
        int left = 6, top = 6, color = 0xDDCCAA;
        fr.drawStringWithShadow("HarderFarming inspector", left, top, color);
        fr.drawStringWithShadow("Block: " + Block.blockRegistry.getNameForObject(b), left, top + 11, 0xFFFFFF);
        if (dead) {
            net.minecraft.tileentity.TileEntity te = w.getTileEntity(x, y, z);
            if (te instanceof com.zenovka.harderfarming.TileEntityPlantCondition) {
                com.zenovka.harderfarming.TileEntityPlantCondition p = (com.zenovka.harderfarming.TileEntityPlantCondition)te;
                fr.drawStringWithShadow("Original: " + p.originalBlock + " @ " + p.originalMetadata, left, top + 22, 0xFFFFFF);
                fr.drawStringWithShadow("Condition: " + (p.condition == 2 ? "DEAD" : "SICK"), left, top + 33, 0xAA9988);
            } else {
                fr.drawStringWithShadow("Condition: WITHERED (legacy)", left, top + 22, 0xAA9988);
            }
            
        } else {
            int stage = Math.max(0, Math.min(7, w.getBlockMetadata(x, y, z)));
            double risk = CropMortality.riskAtStage(stage);
            fr.drawStringWithShadow("Growth: " + stage + "/7  Condition: alive", left, top + 22, 0xFFFFFF);
            fr.drawStringWithShadow(String.format(java.util.Locale.ROOT,
                    "Mortality: %.4f%% / sampled check", risk * 100), left, top + 33, 0xDDCCAA);
            fr.drawStringWithShadow("Cultivation: direct farmland", left, top + 44, 0xAAAAAA);
            fr.drawStringWithShadow(HarderFarming.useLifetimeTarget
                    ? String.format(java.util.Locale.ROOT, "Target lifetime survival: %.1f%% (%d min)",
                    HarderFarming.targetLifetimeSurvival * 100, HarderFarming.expectedGrowthMinutes)
                    : "Mortality mode: advanced per-check override", left, top + 55, 0xAAAAAA);
        }
    }
}
