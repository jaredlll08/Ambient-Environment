package com.blamejared.ambientenvironment;

import com.blamejared.ambientenvironment.mixin.BiomeColorsAccessor;
import net.fabricmc.api.*;

@Environment(EnvType.CLIENT)
public class AmbientEnvironment implements ClientModInitializer {
    
    @Override
    public void onInitializeClient() {
        
        BiomeColorsAccessor.setGrassColorResolver(AmbientEnvironmentCommon.GRASS_RESOLVER);
        BiomeColorsAccessor.setWaterColorResolver(AmbientEnvironmentCommon.WATER_RESOLVER);
    }
    
}