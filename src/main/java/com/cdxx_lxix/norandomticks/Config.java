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
        BLOCK_RANDOM ("randomticks_blocks"),
        BLOCK_TICKS  ("ticks_blocks"),
        FLUID_TICKS ("ticks_fluids"),
        FLUID_RANDOM ("randomticks_fluids");

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
            .defineListAllowEmpty(Properties.BLOCK_RANDOM.key(), List.of(), () -> "", Config::validateBlockName);

    // List of block resource locations to blacklist ticking
    public static final ModConfigSpec.ConfigValue<List<? extends String>> BLOCK_STRINGS_TICKS = BUILDER
            .comment("A list of blocks (resource IDs) whose ticking will be prevented. USE WITH CAUTION! Example: [\"minecraft:fire\"]")
            .defineListAllowEmpty(Properties.BLOCK_TICKS.key(), List.of(), () -> "", Config::validateBlockName);

    // Debug option
    public static final ModConfigSpec.ConfigValue<Boolean> DEBUG = BUILDER
            .comment("Option to receive comments of the mod's actions. On load|reload it'll print blacklisted items and items failed to pass validation")
            .define(Properties.DEBUG.key(), true);

    // List of fluid resource locations to blacklist ticking
    public static final ModConfigSpec.ConfigValue<List<? extends String>> FLUID_STRINGS_TICKS = BUILDER
            .comment("A list of fluids (resource IDs) whose ticking will be prevented. USE WITH CAUTION! Example: [\"minecraft:flowing_water\"]")
            .comment("Fluids won't spread but they will interact with each other.")
            .defineListAllowEmpty(Properties.FLUID_TICKS.key(), List.of(), () -> "", Config::validateFluidName);

    // List of block resource locations to blacklist random ticks
    public static final ModConfigSpec.ConfigValue<List<? extends String>> FLUID_STRINGS_RANDOM = BUILDER
            .comment("A list of fluids (resource IDs) whose random ticking will be prevented.")
            .comment("Remember that fluids have two separate types 'flowing_' and normal. [\"minecraft:lava\", \"minecraft:flowing_lava\"]")
            .comment("Also fluids may not have randomTick method. Water doesn't have randomTick while Lava does.")
            .defineListAllowEmpty(Properties.FLUID_RANDOM.key(), List.of(), () -> "", Config::validateFluidName);

    static final ModConfigSpec SPEC = BUILDER.build();

    // ObjectOpenHashSet for ultra-fast lookup of blacklisted block strings
    public static final ObjectOpenHashSet<String> BLACKLIST_RANDOM = new ObjectOpenHashSet<>();
    public static final ObjectOpenHashSet<String> BLACKLIST_TICKS = new ObjectOpenHashSet<>();
    public static final ObjectOpenHashSet<String> BLACKLIST_FLUID_TICKS = new ObjectOpenHashSet<>();
    public static final ObjectOpenHashSet<String> BLACKLIST_FLUID_RANDOM = new ObjectOpenHashSet<>();

    public static final ObjectOpenHashSet<String> FAILED_BLACKLIST_BLOCKS = new ObjectOpenHashSet<>();
    public static final ObjectOpenHashSet<String> FAILED_BLACKLIST_FLUIDS = new ObjectOpenHashSet<>();

    // Validation function to ensure each string is a registered block or fluid resource location
    private static boolean validateBlockName(final Object obj) {
        if (obj instanceof String name && BuiltInRegistries.BLOCK.containsKey(ResourceLocation.parse(name))) {
            return true;
        } else {
            FAILED_BLACKLIST_BLOCKS.add(obj.toString());
            return false;
        }
    }

    private static boolean validateFluidName(final Object obj) {
        if (obj instanceof String name && BuiltInRegistries.FLUID.containsKey(ResourceLocation.parse(name))) {
            return true;
        } else {
            FAILED_BLACKLIST_FLUIDS.add(obj.toString());
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
        BLACKLIST_FLUID_TICKS.clear();
        BLACKLIST_FLUID_RANDOM.clear();

        BLACKLIST_RANDOM.addAll(BLOCK_STRINGS_RANDOM.get());
        BLACKLIST_TICKS.addAll(BLOCK_STRINGS_TICKS.get());
        BLACKLIST_FLUID_TICKS.addAll(FLUID_STRINGS_TICKS.get());
        BLACKLIST_FLUID_RANDOM.addAll(FLUID_STRINGS_RANDOM.get());

        if(DEBUG.get()) {
            NoRandomTicks.LOGGER.info("NORANDOMTICK DEBUG is ON. If you don't want this messages navigate to the config and change 'true' to 'false'");
            NoRandomTicks.LOGGER.info("Block Random Ticks Blacklist: {}", BLACKLIST_RANDOM);
            NoRandomTicks.LOGGER.info("Block Ticks Blacklist: {}", BLACKLIST_TICKS);
            NoRandomTicks.LOGGER.info("Fluid Ticks Blacklist: {}", BLACKLIST_FLUID_TICKS);
            NoRandomTicks.LOGGER.warn("Fluid Random Ticks Blacklist: {}", BLACKLIST_FLUID_RANDOM);
            NoRandomTicks.LOGGER.warn("Blocks failed to validate: {}", FAILED_BLACKLIST_BLOCKS);
            NoRandomTicks.LOGGER.warn("Fluids failed to validate: {}", FAILED_BLACKLIST_FLUIDS);
        }
    }
}
