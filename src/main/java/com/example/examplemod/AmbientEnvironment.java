package com.example.examplemod;

import net.minecraft.world.biome.BiomeColors;
import net.minecraft.world.gen.PerlinNoiseGenerator;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Random;

@Mod("ambientenvironment")
public class AmbientEnvironment {
    
    
    public AmbientEnvironment() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        
    }
    
    private void doClientStuff(final FMLClientSetupEvent event) {
        BiomeColors.IColorResolver grassColor = BiomeColors.GRASS_COLOR;
        BiomeColors.IColorResolver waterColor = BiomeColors.WATER_COLOR;
        BiomeColors.IColorResolver foliageColor = BiomeColors.FOLIAGE_COLOR;
        
        int levels = 7;
        PerlinNoiseGenerator NOISE_GRASS = new PerlinNoiseGenerator(new Random("NOISE_GRASS".hashCode()), levels);
        PerlinNoiseGenerator NOISE_WATER = new PerlinNoiseGenerator(new Random("NOISE_WATER".hashCode()), levels);
        PerlinNoiseGenerator NOISE_FOLIAGE = new PerlinNoiseGenerator(new Random("NOISE_FOLIAGE".hashCode()), levels);
        
        
        BiomeColors.GRASS_COLOR = (biome, pos) -> {
            int moddedBiomeGrassColor = grassColor.getColor(biome, pos);
            float scale = 8f;
            double value = ((NOISE_GRASS.getValue(pos.getX() / scale, pos.getZ() / scale)));
            value = remap(value, -((1 << levels) - 1), (1 << levels) - 1, 0, 1);
            double darkness = 1 / 16f;
            value = value * darkness;
            float[] argb = getARGB(moddedBiomeGrassColor);
            argb[1] *= ((1 - (darkness)) + (value));
            argb[2] *= ((1 - (darkness)) + (value));
            argb[3] *= ((1 - (darkness)) + (value));
            return toInt(argb);
        };
        BiomeColors.WATER_COLOR = (biome, pos) -> {
            int newColor = waterColor.getColor(biome, pos);
            float scale = 32f;
            double value = ((NOISE_WATER.getValue(pos.getX() / scale, pos.getZ() / scale)));
            double darkness = 0.12;
            value = value * darkness;
            float[] argb = getARGB(newColor);
            argb[1] *= (1 - darkness) + value;
            argb[2] *= (1 - darkness) + value;
            argb[3] *= (1 - darkness) + value;
            return toInt(argb);
        };
        BiomeColors.FOLIAGE_COLOR = (biome, pos) -> {
            int moddedBiomeFoliageColor = foliageColor.getColor(biome, pos);
            float scale = 32f;
            double value = ((NOISE_FOLIAGE.getValue(pos.getX() / scale, pos.getZ() / scale)));
            double darkness = 0.3;
            value = value * darkness;
            float[] argb = getARGB(moddedBiomeFoliageColor);
            argb[1] *= (1 - darkness) + value;
            argb[2] *= (1 - darkness) + value;
            argb[3] *= (1 - darkness) + value;
            return toInt(argb);
        };
    }
    
    public static double remap(double value, double currentLow, double currentHigh, double newLow, double newHigh) {
        return newLow + (value - currentLow) * (newHigh - newLow) / (currentHigh - currentLow);
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
        int r = Math.round(argb[1] * 255) & 0xFF;
        int g = Math.round(argb[2] * 255) & 0xFF;
        int b = Math.round(argb[3] * 255) & 0xFF;
        int a = Math.round(argb[0] * 255) & 0xFF;
        return (a << 24) + (r << 16) + (g << 8) + (b);
    }
    
}
