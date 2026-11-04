package com.zenovka.harderfarming.client;
import com.zenovka.harderfarming.CommonProxy;
import net.minecraftforge.common.MinecraftForge;
public final class ClientProxy extends CommonProxy {
    @Override public void registerClientHandlers() {
        MinecraftForge.EVENT_BUS.register(new CropInspector());
    }
}
