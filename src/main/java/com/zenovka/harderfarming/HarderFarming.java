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
    public static double seedlingRisk, exponent, targetLifetimeSurvival;
    public static boolean useLifetimeTarget;
    public static int expectedGrowthMinutes;
    public static double naturalRecoveryChance;
    public static int sicknessDurationMinutes;
    public static int checkInterval, samplesPerPlayer;
    public static WitheredCrop witheredCrop;

    @SidedProxy(clientSide = "com.zenovka.harderfarming.client.ClientProxy",
                serverSide = "com.zenovka.harderfarming.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Configuration cfg = new Configuration(event.getSuggestedConfigurationFile());
        cfg.load();
        enabled = cfg.getBoolean("enabled", "general", true, "Enable crop sickness and mortality.");
        wheat = cfg.getBoolean("wheat", "crops", true, "Allow wheat to die.");
        carrots = cfg.getBoolean("carrots", "crops", true, "Allow carrots to die.");
        potatoes = cfg.getBoolean("potatoes", "crops", true, "Allow potatoes to die.");
        moddedCrops = cfg.getBoolean("moddedBlockCrops", "crops", true,
                "Include modded crops extending BlockCrops when directly planted on vanilla farmland. Crop-stick hosts are not included.");
        useLifetimeTarget = cfg.getBoolean("useLifetimeTarget", "mortality", true,
                "Calibrate sickness frequency to target natural lifetime survival. Disable to use advanced seedlingRiskPerCheck (now illness risk).");
        targetLifetimeSurvival = cfg.getFloat("targetLifetimeSurvival", "mortality", .85f, 0f, 1f,
                "Approximate survival fraction from planting to maturity (0 to 1), for an average growing crop.");
        expectedGrowthMinutes = cfg.getInt("expectedGrowthMinutes", "mortality", 30, 1, 1440,
                "Assumed average minutes from planting to maturity, used for target calibration.");
        seedlingRisk = cfg.getFloat("seedlingRiskPerCheck", "mortality", 0.025f, 0f, 1f,
                "Advanced override: seedling sickness probability per check, used only when useLifetimeTarget=false.");
        naturalRecoveryChance = cfg.getFloat("naturalRecoveryChance", "sickness", .70f, 0f, 1f,
                "Chance a sick crop recovers naturally at the end of its illness. 1.0 prevents deaths from illness.");
        sicknessDurationMinutes = cfg.getInt("sicknessDurationMinutes", "sickness", 10, 1, 1440,
                "Elapsed loaded simulation minutes spent sick, with growth paused.");
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
