package com.blamejared.ambientenvironment;


import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@Mod("ambientenvironment")
public class AmbientEnvironment {
    
    public AmbientEnvironment(IEventBus modBus) {
        
        modBus.addListener(this::doClientStuff);
    }
    
    private void doClientStuff(final FMLClientSetupEvent event) {
        
        AmbientEnvironmentCommon.init();
    }
    
    
}
