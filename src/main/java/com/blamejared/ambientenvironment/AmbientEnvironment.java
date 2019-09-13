package com.blamejared.ambientenvironment;

import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.relauncher.*;

@Mod(modid = "ambientenvironment", name = "Ambient Environment", version = "1.0.1", clientSideOnly = true)
public class AmbientEnvironment {
    
    public static OpenSimplexNoise NOISE_GRASS = new OpenSimplexNoise("NOISE_GRASS".hashCode());
    public static OpenSimplexNoise NOISE_WATER = new OpenSimplexNoise("NOISE_WATER".hashCode());
    public static OpenSimplexNoise NOISE_FOLIAGE = new OpenSimplexNoise("NOISE_FOLIAGE".hashCode());
    
    @Mod.EventHandler
    @SideOnly(Side.CLIENT)
    public void onFMLInitialization(FMLInitializationEvent event) {
        BiomeColorHelper.ColorResolver grassColor = BiomeColorHelper.GRASS_COLOR;
        BiomeColorHelper.ColorResolver waterColor = BiomeColorHelper.WATER_COLOR;
        BiomeColorHelper.ColorResolver foliageColor = BiomeColorHelper.FOLIAGE_COLOR;
        
        BiomeColorHelper.GRASS_COLOR = (biome, pos) -> {
            float scale = 5f;
            double value = ((NOISE_GRASS.eval(pos.getX() / scale, pos.getZ() / scale)));
            value = remap(value, -1, 1, 0, 0.15);
            double val = value;
            int moddedBiomeGrassColor = grassColor.getColorAtPos(biome, pos);
            float[] argb = getARGB(moddedBiomeGrassColor);
            argb[1] *= 0.9 + val;
            argb[2] *= 0.9 + val;
            argb[3] *= 0.9 + val;
            return toInt(argb);
        };
        BiomeColorHelper.WATER_COLOR = (biome, pos) -> {
            int newColor = waterColor.getColorAtPos(biome, pos);
            float scale = 8f;
            double value = ((NOISE_WATER.eval(pos.getX() / scale, pos.getZ() / scale)));
            value = remap(value, -1, 1, 0, 0.12);
            double val = value;
            float[] argb = getARGB(newColor);
            argb[1] *= 0.88 + val;
            argb[2] *= 0.88 + val;
            argb[3] *= 0.88 + val;
            return toInt(argb);
        };
        BiomeColorHelper.FOLIAGE_COLOR = (biome, pos) -> {
            int moddedBiomeFoliageColor = foliageColor.getColorAtPos(biome, pos);
            float scale = 5f;
            double value = ((NOISE_FOLIAGE.eval(pos.getX() / scale, pos.getZ() / scale)));
            value = remap(value, -1, 1, 0, 0.3);
            double val = value;
            float[] argb = getARGB(moddedBiomeFoliageColor);
            argb[1] *= 0.7 + val;
            argb[2] *= 0.7 + val;
            argb[3] *= 0.7 + val;
            return toInt(argb);
        };
    }
    
    private static float getRed(int hex) {
        return ((hex >> 16) & 0xFF) / 255f;
    }
    
    private static float getGreen(int hex) {
        return ((hex >> 8) & 0xFF) / 255f;
    }
    
    private static float getBlue(int hex) {
        return ((hex) & 0xFF) / 255f;
    }
    
    private static float getAlpha(int hex) {
        return ((hex >> 24) & 0xff) / 255f;
    }
    
    
    private static float[] getARGB(int hex) {
        return new float[] {getAlpha(hex), getRed(hex), getGreen(hex), getBlue(hex)};
    }
    
    private static int toInt(float[] argb) {
        int r = Math.round(Math.min(1f, argb[1]) * 255) & 0xFF;
        int g = Math.round(Math.min(1f, argb[2]) * 255) & 0xFF;
        int b = Math.round(Math.min(1f, argb[3]) * 255) & 0xFF;
        int a = Math.round(argb[0] * 255) & 0xFF;
        return (a << 24) + (r << 16) + (g << 8) + (b);
    }
    
    public static double remap(double value, double currentLow, double currentHigh, double newLow, double newHigh) {
        return newLow + (value - currentLow) * (newHigh - newLow) / (currentHigh - currentLow);
    }
}
