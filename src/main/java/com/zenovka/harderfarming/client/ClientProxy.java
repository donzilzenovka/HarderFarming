package com.zenovka.harderfarming.client;
import com.zenovka.harderfarming.CommonProxy;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.registry.ClientRegistry;
import com.zenovka.harderfarming.TileEntityPlantCondition;
public final class ClientProxy extends CommonProxy {
    @Override public void registerClientHandlers() {
        MinecraftForge.EVENT_BUS.register(new CropInspector());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPlantCondition.class, new RenderPlantCondition());
    }
}
