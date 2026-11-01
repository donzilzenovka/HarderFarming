package com.zenovka.harderfarming;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraftforge.common.config.Configuration;

@Mod(modid = HarderFarming.MODID, name = "Harder Farming", version = "0.1.0", acceptableRemoteVersions = "*")
public final class HarderFarming {
    public static final String MODID = "harderfarming";
    static boolean enabled, wheat, carrots, potatoes;
    static double seedlingRisk, exponent;
    static int checkInterval;
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Configuration cfg = new Configuration(event.getSuggestedConfigurationFile());
        cfg.load();
        enabled = cfg.getBoolean("enabled", "general", true, "Enable vanilla crop mortality.");
        wheat = cfg.getBoolean("wheat", "crops", true, "Allow wheat to die.");
        carrots = cfg.getBoolean("carrots", "crops", true, "Allow carrots to die.");
        potatoes = cfg.getBoolean("potatoes", "crops", true, "Allow potatoes to die.");
        seedlingRisk = cfg.getFloat("seedlingRiskPerCheck", "mortality", 0.0025f, 0f, 1f, "Death probability per check at growth stage 0. Default 0.25%. This is not lifetime mortality.");
        exponent = cfg.getFloat("declineExponent", "mortality", 2f, 0.1f, 10f, "Risk multiplier is (1 - stage/7)^exponent.");
        checkInterval = cfg.getInt("checkIntervalTicks", "mortality", 100, 1, 72000, "Average ticks between checks per crop. Checks use random sampling; no world scanning.");
        if (cfg.hasChanged()) cfg.save();
    }
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(new CropMortality());
    }
}
