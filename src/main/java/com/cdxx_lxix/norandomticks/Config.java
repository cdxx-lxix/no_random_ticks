package com.cdxx_lxix.norandomticks;

import java.util.List;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private enum Properties {
        DEBUG  ("debug"),
        RANDOM ("randomticks_blocks"),
        TICKS  ("ticks_blocks");

        private final String key;

        Properties(String key) {
            this.key = key;
        }

        public String key() {
            return key;
        }
    }

    // List of block resource locations to blacklist random ticks
    public static final ModConfigSpec.ConfigValue<List<? extends String>> BLOCK_STRINGS_RANDOM = BUILDER
            .comment("A list of blocks (resource IDs) whose random ticking will be prevented. Example: [\"minecraft:copper_block\", \"minecraft:bamboo\"]")
            .defineListAllowEmpty(Properties.RANDOM.key(), List.of(), () -> "", Config::validateBlockName);

    // List of block resource locations to blacklist ticking
    public static final ModConfigSpec.ConfigValue<List<? extends String>> BLOCK_STRINGS_TICKS = BUILDER
            .comment("A list of blocks (resource IDs) whose ticking will be prevented. USE WITH CAUTION! Example: [\"minecraft:fire\"]")
            .defineListAllowEmpty(Properties.TICKS.key(), List.of(), () -> "", Config::validateBlockName);

    // Debug option
    public static final ModConfigSpec.ConfigValue<Boolean> DEBUG = BUILDER
            .comment("Option to receive comments of the mod's actions. On load\reload it'll print blacklisted items and items failed to pass validation")
            .define(Properties.DEBUG.key(), true);

    static final ModConfigSpec SPEC = BUILDER.build();

    // ObjectOpenHashSet for ultra-fast lookup of blacklisted block strings
    public static final ObjectOpenHashSet<String> BLACKLIST_RANDOM = new ObjectOpenHashSet<>();
    public static final ObjectOpenHashSet<String> BLACKLIST_TICKS = new ObjectOpenHashSet<>();

    public static final ObjectOpenHashSet<String> FAILED_BLACKLIST = new ObjectOpenHashSet<>();

    // Validation function to ensure each string is a registered block resource location
    private static boolean validateBlockName(final Object obj) {
        if (obj instanceof String name && BuiltInRegistries.BLOCK.containsKey(ResourceLocation.parse(name))) {
            return true;
        } else {
            FAILED_BLACKLIST.add(obj.toString());
            return false;
        }
    }

    public static void onConfigLoading(final ModConfigEvent.Loading event) {
        if (event.getConfig().getModId().equals(NoRandomTicks.MODID)) {
            reloadConfigValues();
        }
    }

    public static void onConfigReloading(final ModConfigEvent.Reloading event) {
        if (event.getConfig().getModId().equals(NoRandomTicks.MODID)) {
            reloadConfigValues();
        }
    }

    private static void reloadConfigValues() {
        NoRandomTicks.LOGGER.debug("Config Reloaded!");
        BLACKLIST_RANDOM.clear();
        BLACKLIST_TICKS.clear();

        BLACKLIST_RANDOM.addAll(BLOCK_STRINGS_RANDOM.get());
        BLACKLIST_TICKS.addAll(BLOCK_STRINGS_TICKS.get());

        if(DEBUG.get()) {
            NoRandomTicks.LOGGER.info("NORANDOMTICK DEBUG is ON. If you don't want this messages navigate to the config and change 'true' to 'false'");
            NoRandomTicks.LOGGER.info("Random Ticks Blacklist: {}", BLACKLIST_RANDOM);
            NoRandomTicks.LOGGER.info("Ticks Blacklist: {}", BLACKLIST_TICKS);
            NoRandomTicks.LOGGER.warn("Failed to validate: {}", FAILED_BLACKLIST);
        }
    }
}
