package com.blamejared.ambientenvironment;

import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.util.Mth;
import net.minecraftforge.fml.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmllegacy.network.FMLNetworkConstants;

@Mod("ambientenvironment")
public class AmbientEnvironment {
    
    public AmbientEnvironment() {
    
        ModLoadingContext.get()
                .registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> FMLNetworkConstants.IGNORESERVERONLY, (remote, isServer) -> true));
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
    }
    
    private void doClientStuff(final FMLClientSetupEvent event) {
        
        BiomeColors.GRASS_COLOR_RESOLVER = AmbientEnvironmentCommon.GRASS_RESOLVER;
        BiomeColors.WATER_COLOR_RESOLVER = AmbientEnvironmentCommon.WATER_RESOLVER;
    }
    
    
}
