package com.zenovka.harderfarming;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraftforge.common.config.Configuration;

@Mod(modid = HarderFarming.MODID, name = "Harder Farming", version = "0.1.0", acceptableRemoteVersions = "*")
public final class HarderFarming {
    public static final String MODID = "harderfarming";
    public static boolean enabled, wheat, carrots, potatoes, moddedCrops, debugHud;
    public static double seedlingRisk, exponent;
    public static int checkInterval, samplesPerPlayer;
    public static WitheredCrop witheredCrop;

    @SidedProxy(clientSide = "com.zenovka.harderfarming.client.ClientProxy",
                serverSide = "com.zenovka.harderfarming.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Configuration cfg = new Configuration(event.getSuggestedConfigurationFile());
        cfg.load();
        enabled = cfg.getBoolean("enabled", "general", true, "Enable mortality.");
        wheat = cfg.getBoolean("wheat", "crops", true, "Allow wheat to die.");
        carrots = cfg.getBoolean("carrots", "crops", true, "Allow carrots to die.");
        potatoes = cfg.getBoolean("potatoes", "crops", true, "Allow potatoes to die.");
        moddedCrops = cfg.getBoolean("moddedBlockCrops", "crops", true,
                "Include modded crops extending BlockCrops when directly planted on vanilla farmland. Crop-stick hosts are not included.");
        seedlingRisk = cfg.getFloat("seedlingRiskPerCheck", "mortality", 0.025f, 0f, 1f,
                "Death probability per sampled check at growth stage 0 (default 2.5%). Not lifetime mortality.");
        exponent = cfg.getFloat("declineExponent", "mortality", 2f, 0.1f, 10f,
                "Risk multiplier (1 - stage/7)^exponent.");
        checkInterval = cfg.getInt("checkIntervalTicks", "mortality", 20, 1, 72000,
                "Ticks between batches of random sampling near each player.");
        samplesPerPlayer = cfg.getInt("samplesPerPlayer", "mortality", 128, 1, 4096,
                "Random columns sampled per player per batch, within 16 blocks.");
        debugHud = cfg.getBoolean("debugHud", "diagnostics", true,
                "Show crop inspector when looking at a supported crop; client setting.");
        if (cfg.hasChanged()) cfg.save();
        witheredCrop = new WitheredCrop();
        GameRegistry.registerBlock(witheredCrop, "witheredCrop");
        GameRegistry.registerTileEntity(TileEntityPlantCondition.class, "harderfarming.plantCondition");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(new CropMortality());
        proxy.registerClientHandlers();
    }
}
