package com.blamejared.ambientenvironment;

import net.minecraft.util.SharedSeedRandom;
import net.minecraft.world.biome.BiomeColors;
import net.minecraft.world.gen.PerlinNoiseGenerator;
import net.minecraft.world.level.ColorResolver;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.FMLNetworkConstants;

import java.util.stream.IntStream;

import org.apache.commons.lang3.tuple.Pair;

@Mod("ambientenvironment")
public class AmbientEnvironment {

    public AmbientEnvironment() {
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        final ColorResolver grassColor = BiomeColors.GRASS_COLOR;
        final ColorResolver waterColor = BiomeColors.WATER_COLOR;
//        final BiomeColors.IColorResolver foliageColor = BiomeColors.FOLIAGE_COLOR;

        final int levels = 2;
        final PerlinNoiseGenerator NOISE_GRASS = new PerlinNoiseGenerator(new SharedSeedRandom("NOISE_GRASS".hashCode()), IntStream.rangeClosed(0, levels));
        final PerlinNoiseGenerator NOISE_WATER = new PerlinNoiseGenerator(new SharedSeedRandom("NOISE_WATER".hashCode()), IntStream.rangeClosed(0, levels));
//        final PerlinNoiseGenerator NOISE_FOLIAGE = new PerlinNoiseGenerator(new Random("NOISE_FOLIAGE".hashCode()), levels);

        BiomeColors.GRASS_COLOR = (biome, posX, posZ) -> {
            final int newColor = grassColor.getColor(biome, posX, posZ);
            final float scale = 8f;
            double value = ((NOISE_GRASS.noiseAt(posX / scale, posZ / scale, false)));
            final double darkness = 0.25f;
            value = curve(0, 1, remap(value, -((1 << levels) - 1), (1 << levels) - 1, 0, 1)) * darkness;
            return blend(newColor, 0, (float) (value));
        };
        BiomeColors.WATER_COLOR = (biome, posX, posZ) -> {
            final int newColor = waterColor.getColor(biome, posX, posZ);
            final float scale = 16f;
            double value = ((NOISE_WATER.noiseAt(posX / scale, posZ / scale, false)));
            final double darkness = 0.3f;
            value = curve(0, 1, remap(value, -((1 << levels) - 1), (1 << levels) - 1, 0, 1)) * darkness;
            return blend(newColor, 0, (float) (value));
        };
//        BiomeColors.FOLIAGE_COLOR = (biome, pos) -> {
//            final int newColor = foliageColor.getColor(biome, pos);
//            final float scale = 8f;
//            double value = ((NOISE_FOLIAGE.getValue(pos.getX() / scale, pos.getZ() / scale)));
//            final double darkness = 1;
//            value = curve(0, 1, remap(value, -((1 << levels) - 1), (1 << levels) - 1, 0, 1), 1) * darkness;
//            return blend(newColor, 0, (float) (value));
//        };
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
        return new float[]{getAlpha(hex), getRed(hex), getGreen(hex), getBlue(hex)};
    }

    private static int toInt(final float[] argb) {
        final int r = (int) Math.floor(argb[1] * 255) & 0xFF;
        final int g = (int) Math.floor(argb[2] * 255) & 0xFF;
        final int b = (int) Math.floor(argb[3] * 255) & 0xFF;
        final int a = (int) Math.floor(argb[0] * 255) & 0xFF;
        return (a << 24) + (r << 16) + (g << 8) + (b);
    }

    public static double clamp(final double value, final double min, final double max) {
        return Math.max(min, Math.min(value, max));
    }

    public static double curve(final double start, final double end, double amount) {
        amount = clamp(amount, 0, 1);
        amount = clamp((amount - start) / (end - start), 0, 1);
        return clamp(0.5 + 0.5 * Math.sin(Math.cos(Math.PI * Math.tan(90 * amount))) * Math.cos(Math.sin(Math.tan(amount))), 0, 1);
    }

    public static int blend(final int color1, final int color2, final float ratio) {
        final float ir = 1.0f - ratio;

        final float[] rgb1 = getARGB(color2);
        final float[] rgb2 = getARGB(color1);

        return toInt(new float[]{rgb1[0] * ratio + rgb2[0] * ir, rgb1[1] * ratio + rgb2[1] * ir, rgb1[2] * ratio + rgb2[2] * ir, rgb1[3] * ratio + rgb2[3] * ir});

    }

}
