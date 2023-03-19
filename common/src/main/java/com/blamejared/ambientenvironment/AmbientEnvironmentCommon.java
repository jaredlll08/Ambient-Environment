package com.blamejared.ambientenvironment;

import com.blamejared.ambientenvironment.mixin.BiomeColorsAccessor;
import net.minecraft.Util;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;

import java.util.List;
import java.util.stream.IntStream;

public class AmbientEnvironmentCommon {
    
    public static final int NOISE_OCTAVES = 2;
    public static final List<Integer> OCTAVES = IntStream.rangeClosed(0, NOISE_OCTAVES).boxed().toList();
    public static final PerlinSimplexNoise GRASS_NOISE = new PerlinSimplexNoise(new XoroshiroRandomSource("NOISE_GRASS".hashCode()), OCTAVES);
    public static final PerlinSimplexNoise WATER_NOISE = new PerlinSimplexNoise(new XoroshiroRandomSource("NOISE_WATER".hashCode()), OCTAVES);
    
    public static final ColorResolver GRASS_RESOLVER = Util.make(() -> {
        final var baseResolver = BiomeColors.GRASS_COLOR_RESOLVER;
        return (biome, x, z) -> modifyColour(GRASS_NOISE, baseResolver, biome, x, z, 8f, 0.25f);
    });
    
    public static final ColorResolver WATER_RESOLVER = Util.make(() -> {
        final var baseResolver = BiomeColors.WATER_COLOR_RESOLVER;
        return (biome, x, z) -> modifyColour(WATER_NOISE, baseResolver, biome, x, z, 16f, 0.3f);
    });
    
    public static void init() {
        
        BiomeColorsAccessor.ambientenvironment$setGrassColorResolver(AmbientEnvironmentCommon.GRASS_RESOLVER);
        BiomeColorsAccessor.ambientenvironment$setWaterColorResolver(AmbientEnvironmentCommon.WATER_RESOLVER);
    }
    
    
    private static int modifyColour(PerlinSimplexNoise generator, ColorResolver resolver, Biome biome, double x, double z, double scale, double darkness) {
        
        final int base = resolver.getColor(biome, x, z);
        double value = generator.getValue(x / scale, z / scale, false);
        value = curve(0, 1, remap(value, -((1 << NOISE_OCTAVES) - 1), (1 << NOISE_OCTAVES) - 1, 0, 1)) * darkness;
        return blend(base, 0, (float) (value));
    }
    
    public static double remap(final double value, final double currentLow, final double currentHigh, final double newLow, final double newHigh) {
        
        return newLow + (value - currentLow) * (newHigh - newLow) / (currentHigh - currentLow);
    }
    
    private static float getRed(final int hex) {
        
        return ((hex >> 16) & 0xFF) / 255f;
    }
    
    private static float getGreen(final int hex) {
        
        return ((hex >> 8) & 0xFF) / 255f;
    }
    
    private static float getBlue(final int hex) {
        
        return ((hex) & 0xFF) / 255f;
    }
    
    private static float getAlpha(final int hex) {
        
        return ((hex >> 24) & 0xff) / 255f;
    }
    
    private static float[] getARGB(final int hex) {
        
        return new float[] {getAlpha(hex), getRed(hex), getGreen(hex), getBlue(hex)};
    }
    
    private static int toInt(final float[] argb) {
        
        final int r = (int) Math.floor(argb[1] * 255) & 0xFF;
        final int g = (int) Math.floor(argb[2] * 255) & 0xFF;
        final int b = (int) Math.floor(argb[3] * 255) & 0xFF;
        final int a = (int) Math.floor(argb[0] * 255) & 0xFF;
        return (a << 24) + (r << 16) + (g << 8) + (b);
    }
    
    
    public static double curve(final double start, final double end, double amount) {
        
        amount = Mth.clamp(amount, 0, 1);
        amount = Mth.clamp((amount - start) / (end - start), 0, 1);
        return Mth.clamp(0.5 + 0.5 * Math.sin(Math.cos(Math.PI * Math.tan(90 * amount))) * Math.cos(Math.sin(Math.tan(amount))), 0, 1);
    }
    
    public static int blend(final int color1, final int color2, final float ratio) {
        
        final float ir = 1.0f - ratio;
        
        final float[] rgb1 = getARGB(color2);
        final float[] rgb2 = getARGB(color1);
        
        return toInt(new float[] {rgb1[0] * ratio + rgb2[0] * ir, rgb1[1] * ratio + rgb2[1] * ir, rgb1[2] * ratio + rgb2[2] * ir, rgb1[3] * ratio + rgb2[3] * ir});
    }
    
}
