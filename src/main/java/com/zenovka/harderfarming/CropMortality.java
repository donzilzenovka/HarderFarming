package com.zenovka.harderfarming;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import java.util.Random;

/**
 * Samples loaded chunks only, avoiding persistent coordinate lists and stale crop records.
 * A check is approximately once per configured interval per crop, in expectation.
 */
public final class CropMortality {
    private static final int SAMPLES_PER_CHUNK_TICK = 3;

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.world.isRemote || !HarderFarming.enabled) return;
        World world = event.world;
        Random random = world.rand;
        // A world tick's loaded-chunk traversal is deliberately avoided. Instead,
        // use Minecraft's normal random block tick mechanism in the follow-up integration.
        // No mortality is applied here until a safe crop sampling hook is installed.
    }

    /**
     * Crop eligibility must be determined by a mod-aware adapter, not by
     * BlockCrops inheritance or a vanilla block whitelist. Agricraft and IC2
     * crop hosts use tile entities and their own growth stages.
     *
     * No generic fallback is safe: many non-crop blocks are plants, and
     * fungi must be excluded even when grown on soil-like substrates.
     */
    static boolean eligible(Block block) {
        return false; // Pending crop-adapter registry.
    }

    static double riskAtStage(int stage) {
        if (stage >= 7) return 0.0;
        return HarderFarming.seedlingRisk * Math.pow(1.0 - Math.max(0, stage) / 7.0, HarderFarming.exponent);
    }
}
