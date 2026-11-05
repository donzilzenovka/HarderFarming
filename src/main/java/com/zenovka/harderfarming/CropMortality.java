package com.zenovka.harderfarming;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.init.Blocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import java.util.Random;

/**
 * Prototype: samples surface crops around players without scanning every loaded chunk.
 * Does not touch tile-entity crop sticks or non-BlockCrops plants.
 */
public final class CropMortality {
    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.world.isRemote || !HarderFarming.enabled) return;
        World world = event.world;
        if (world.getTotalWorldTime() % HarderFarming.checkInterval != 0) return;
        Random random = world.rand;
        for (Object obj : world.playerEntities) {
            if (!(obj instanceof EntityPlayer)) continue;
            EntityPlayer player = (EntityPlayer)obj;
            int cx = (int)Math.floor(player.posX), cz = (int)Math.floor(player.posZ);
            for (int i = 0; i < HarderFarming.samplesPerPlayer; i++) {
                int x = cx + random.nextInt(33) - 16;
                int z = cz + random.nextInt(33) - 16;
                // Each player samples the same square. Thin overlapping samples so
                // a crop's expected check frequency does not grow with player count.
                int coverage = 0;
                for (Object nearby : world.playerEntities) {
                    if (!(nearby instanceof EntityPlayer)) continue;
                    EntityPlayer other = (EntityPlayer)nearby;
                    int ox = (int)Math.floor(other.posX), oz = (int)Math.floor(other.posZ);
                    if (Math.abs(x - ox) <= 16 && Math.abs(z - oz) <= 16) coverage++;
                }
                if (coverage > 1 && random.nextInt(coverage) != 0) continue;
                if (!world.blockExists(x, 64, z)) continue;
                int top = world.getHeightValue(x, z);
                // Surface crops typically sit immediately above farmland. Examine
                // a shallow range to accommodate uneven terrain and foliage.
                for (int y = top; y >= Math.max(1, top - 4); y--) {
                    Block block = world.getBlock(x, y, z);
                    if (!eligible(world, x, y, z, block)) continue;
                    int stage = Math.max(0, Math.min(7, world.getBlockMetadata(x, y, z)));
                    if (stage < 7 && random.nextDouble() < riskAtStage(stage)) {
                        Block old = block;
                        float[] selection = TileEntityPlantCondition.snapshotSelection(world, x, y, z, old);
                        int oldMeta = world.getBlockMetadata(x, y, z);
                        if (world.setBlock(x, y, z, HarderFarming.witheredCrop, 0, 3)) {
                            net.minecraft.tileentity.TileEntity te = world.getTileEntity(x, y, z);
                            if (te instanceof TileEntityPlantCondition) {
                                ((TileEntityPlantCondition)te).capture(old, oldMeta);
                                ((TileEntityPlantCondition)te).setSelection(selection);
                                world.markBlockForUpdate(x, y, z);
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    static boolean eligible(World world, int x, int y, int z, Block block) {
        if (!(block instanceof BlockCrops) || block == Blocks.nether_wart) return false;
        if (block == Blocks.wheat && !HarderFarming.wheat) return false;
        if (block == Blocks.carrots && !HarderFarming.carrots) return false;
        if (block == Blocks.potatoes && !HarderFarming.potatoes) return false;
        if (block != Blocks.wheat && block != Blocks.carrots && block != Blocks.potatoes
                && !HarderFarming.moddedCrops) return false;
        return world.getBlock(x, y - 1, z) == Blocks.farmland;
    }

    /** Expected trials per plant during the configured average growth duration.
     *  Sampling a uniform 33x33 square gives samplesPerPlayer/1089 trials per batch.
     */
    public static double expectedChecksPerLifetime() {
        return (HarderFarming.expectedGrowthMinutes * 60.0 * 20.0
                / HarderFarming.checkInterval) * HarderFarming.samplesPerPlayer / 1089.0;
    }

    public static double calibratedSeedlingRisk() {
        if (!HarderFarming.useLifetimeTarget) return HarderFarming.seedlingRisk;
        double survival = HarderFarming.targetLifetimeSurvival;
        if (survival >= 1) return 0;
        if (survival <= 0) return 1;
        // Assume equal time in growth stages 0..6; maturity is immune.
        double meanWeight = 0;
        for (int stage = 0; stage < 7; stage++)
            meanWeight += Math.pow(1.0 - stage / 7.0, HarderFarming.exponent) / 7.0;
        double weightedChecks = expectedChecksPerLifetime() * meanWeight;
        return weightedChecks <= 0 ? 0 : 1.0 - Math.exp(Math.log(survival) / weightedChecks);
    }

    public static double riskAtStage(int stage) {
        if (stage >= 7) return 0.0;
        return calibratedSeedlingRisk() *
                Math.pow(1.0 - Math.max(0, stage) / 7.0, HarderFarming.exponent);
    }
}
