package com.cdxx_lxix.norandomticks;

import net.minecraft.core.registries.BuiltInRegistries;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;

import static com.cdxx_lxix.norandomticks.Config.BLACKLIST_RANDOM;
import static com.cdxx_lxix.norandomticks.Config.BLACKLIST_TICKS;

@Mod(NoRandomTicks.MODID)
public class NoRandomTicks {
    public static final String MODID = "norandomticks";
    public static final Logger LOGGER = LogUtils.getLogger();

    public NoRandomTicks(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(Config::onConfigLoading);
        modEventBus.addListener(Config::onConfigReloading);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
    public static boolean isRandomTickAllowed(Block block) {
        return BLACKLIST_RANDOM.contains(BuiltInRegistries.BLOCK.getKey(block).toString());
    }

    public static boolean isTickingAllowed(Block block) {
        return BLACKLIST_TICKS.contains(BuiltInRegistries.BLOCK.getKey(block).toString());
    }
}