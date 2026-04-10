package com.cdxx_lxix.norandomticks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

import static com.cdxx_lxix.norandomticks.Config.BLACKLIST_FLUID_RANDOM;
import static com.cdxx_lxix.norandomticks.Config.BLACKLIST_FLUID_TICKS;


@Mod(NoRandomticksMod.MODID)
public class NoRandomticksMod {
    public static final String MODID = "norandomticks";
    public static final Logger LOG = LogManager.getLogger(NoRandomticksMod.class.getSimpleName());

    public NoRandomticksMod() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    public static boolean isRandomTickAllowed(Block block) { return Config.BLACKLIST_RANDOM.contains(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(block)).toString()); }

    public static boolean isTickingAllowed(Block block) {
        return Config.BLACKLIST_TICKS.contains(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(block)).toString());
    }

    public static boolean isTickingAllowedFluids(Fluid fluid) {
        return BLACKLIST_FLUID_TICKS.contains(Objects.requireNonNull(ForgeRegistries.FLUIDS.getKey(fluid)).toString());
    }

    public static boolean isRandomTickingAllowedFluids(Fluid fluid) {
        return BLACKLIST_FLUID_RANDOM.contains(Objects.requireNonNull(ForgeRegistries.FLUIDS.getKey(fluid)).toString());
    }
}
