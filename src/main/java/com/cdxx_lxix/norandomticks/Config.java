package com.cdxx_lxix.norandomticks;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private enum Properties {
        DEBUG("debug"),
        BLOCK_RANDOM("randomticks_blocks"),
        BLOCK_RANDOM_REGEX("randomticks_blocks_regex"),
        BLOCK_TICKS("ticks_blocks"),
        BLOCK_TICKS_REGEX("ticks_blocks_regex"),
        FLUID_TICKS("ticks_fluids"),
        FLUID_TICKS_REGEX("ticks_fluids_regex"),
        FLUID_RANDOM("randomticks_fluids"),
        FLUID_RANDOM_REGEX("randomticks_fluids_regex");

        private final String key;

        Properties(String key) {
            this.key = key;
        }

        public String key() {
            return key;
        }
    }

    static {
        BUILDER.comment(
                "============================================================",
                "DISCLAIMER",
                "Keep in mind that Minecraft works on RandomTicks and Ticks. 20 times (default) per second blocks that use one or both of these methods execute them.",
                "For example grass spreads with RandomTicks, copper weathers with them too, fire burns with Ticks and spreads with RandomTicks.",
                "Most of such logic if you don't mind may be prevented to save some computational resources. Alternatively, it may be prevented for decorational purposes or specialized maps/packs.",
                "============================================================",
                ""
        );
    }

    public static final ModConfigSpec.ConfigValue<Boolean> DEBUG = BUILDER
            .comment("Option to receive comments of the mod's actions.")
            .comment("On load/reload it'll print blacklisted items and items that failed validation.")
            .define(Properties.DEBUG.key(), true);

    static {
        BUILDER.comment(
                "============================================================",
                "Ticks section",
                "USE WITH CAUTION!",
                "Preventing blocks from ticking may break the game. No reports of something serious like world corruption but it may crash or freeze.",
                "Both lists are merged on load/reload, so you can have mass exclusion via regex and granular control with manual per-block array",
                "============================================================",
                ""
        );
    }

    public static final ModConfigSpec.ConfigValue<List<? extends String>> BLOCK_STRINGS_TICKS = BUILDER
            .comment("A list of blocks (resource IDs) whose ticking will be prevented. USE WITH CAUTION!")
            .comment("Example: [\"minecraft:fire\"]")
            .defineListAllowEmpty(Properties.BLOCK_TICKS.key(), List.of(), () -> "", Config::validateBlockName);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> FLUID_STRINGS_TICKS = BUILDER
            .comment("A list of fluids (resource IDs) whose ticking will be prevented. USE WITH CAUTION!")
            .comment("Example: [\"minecraft:flowing_water\"]")
            .comment("Fluids won't spread but they will still interact with each other.")
            .defineListAllowEmpty(Properties.FLUID_TICKS.key(), List.of(), () -> "", Config::validateFluidName);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> BLOCK_STRINGS_TICKS_REGEX = BUILDER
            .comment("A list of regex patterns matching block IDs whose ticking will be prevented. USE WITH CAUTION!")
            .comment("Example: ['^minecraft:(?:fire|lava)$']")
            .comment("On config load/reload these are combined with ticks_blocks.")
            .defineListAllowEmpty(Properties.BLOCK_TICKS_REGEX.key(), List.of(), () -> "", Config::validateRegex);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> FLUID_STRINGS_TICKS_REGEX = BUILDER
            .comment("A list of regex patterns matching fluid IDs whose ticking will be prevented. USE WITH CAUTION!")
            .comment("Example: ['^minecraft:(?:water|flowing_water|lava|flowing_lava)$']")
            .comment("On config load/reload these are combined with ticks_fluids.")
            .defineListAllowEmpty(Properties.FLUID_TICKS_REGEX.key(), List.of(), () -> "", Config::validateRegex);

    static {
        BUILDER.comment(
                "============================================================",
                "Random Ticks section",
                "Random Tick prevention is generally safe unlike regular Ticks.",
                "Both lists are merged on load/reload, so you can have mass exclusion via regex and granular control with manual per-block array",
                "============================================================",
                ""
        );
    }

    public static final ModConfigSpec.ConfigValue<List<? extends String>> BLOCK_STRINGS_RANDOM = BUILDER
            .comment("A list of blocks (resource IDs) whose random ticking will be prevented.")
            .comment("Example: [\"minecraft:copper_block\", \"minecraft:bamboo\"]")
            .defineListAllowEmpty(Properties.BLOCK_RANDOM.key(), List.of(), () -> "", Config::validateBlockName);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> FLUID_STRINGS_RANDOM = BUILDER
            .comment("A list of fluids (resource IDs) whose random ticking will be prevented.")
            .comment("Remember that fluids often have both normal and flowing variants.")
            .comment("Example: [\"minecraft:lava\", \"minecraft:flowing_lava\"]")
            .comment("Also, some fluids may not implement randomTick; water does not, lava does.")
            .defineListAllowEmpty(Properties.FLUID_RANDOM.key(), List.of(), () -> "", Config::validateFluidName);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> BLOCK_STRINGS_RANDOM_REGEX = BUILDER
            .comment("A list of regex patterns matching block IDs whose random ticking will be prevented.")
            .comment("Example: ['^minecraft:(?!stripped_).*(?:_sapling|_leaves|_log)$']")
            .comment("On config load/reload these are combined with randomticks_blocks.")
            .defineListAllowEmpty(Properties.BLOCK_RANDOM_REGEX.key(), List.of(), () -> "", Config::validateRegex);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> FLUID_STRINGS_RANDOM_REGEX = BUILDER
            .comment("A list of regex patterns matching fluid IDs whose random ticking will be prevented.")
            .comment("Example: ['^minecraft:(?:lava|flowing_lava)$']")
            .comment("On config load/reload these are combined with randomticks_fluids.")
            .defineListAllowEmpty(Properties.FLUID_RANDOM_REGEX.key(), List.of(), () -> "", Config::validateRegex);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static final ObjectOpenHashSet<String> BLACKLIST_RANDOM = new ObjectOpenHashSet<>();
    public static final ObjectOpenHashSet<String> BLACKLIST_TICKS = new ObjectOpenHashSet<>();
    public static final ObjectOpenHashSet<String> BLACKLIST_FLUID_TICKS = new ObjectOpenHashSet<>();
    public static final ObjectOpenHashSet<String> BLACKLIST_FLUID_RANDOM = new ObjectOpenHashSet<>();

    public static final ObjectOpenHashSet<String> BLOCK_RANDOM_REGEX = new ObjectOpenHashSet<>();
    public static final ObjectOpenHashSet<String> BLOCK_TICKS_REGEX = new ObjectOpenHashSet<>();
    public static final ObjectOpenHashSet<String> FLUID_TICKS_REGEX = new ObjectOpenHashSet<>();
    public static final ObjectOpenHashSet<String> FLUID_RANDOM_REGEX = new ObjectOpenHashSet<>();

    public static final List<Pattern> BLOCK_RANDOM_PATTERNS = new ArrayList<>();
    public static final List<Pattern> BLOCK_TICKS_PATTERNS = new ArrayList<>();
    public static final List<Pattern> FLUID_TICKS_PATTERNS = new ArrayList<>();
    public static final List<Pattern> FLUID_RANDOM_PATTERNS = new ArrayList<>();

    public static final ObjectOpenHashSet<String> FAILED_BLACKLIST_BLOCKS = new ObjectOpenHashSet<>();
    public static final ObjectOpenHashSet<String> FAILED_BLACKLIST_FLUIDS = new ObjectOpenHashSet<>();

    private static boolean validateBlockName(final Object obj) {
        if (!(obj instanceof String name)) {
            FAILED_BLACKLIST_BLOCKS.add(String.valueOf(obj));
            return false;
        }

        ResourceLocation id = ResourceLocation.tryParse(name);
        if (id != null && BuiltInRegistries.BLOCK.containsKey(id)) {
            return true;
        }

        FAILED_BLACKLIST_BLOCKS.add(name);
        return false;
    }

    private static boolean validateFluidName(final Object obj) {
        if (!(obj instanceof String name)) {
            FAILED_BLACKLIST_FLUIDS.add(String.valueOf(obj));
            return false;
        }

        ResourceLocation id = ResourceLocation.tryParse(name);
        if (id != null && BuiltInRegistries.FLUID.containsKey(id)) {
            return true;
        }

        FAILED_BLACKLIST_FLUIDS.add(name);
        return false;
    }

    private static boolean validateRegex(final Object obj) {
        if (!(obj instanceof String pattern)) {
            NoRandomTicks.LOGGER.warn("Rejected non-string regex config entry: {}", obj);
            return false;
        }

        try {
            Pattern.compile(pattern);
            return true;
        } catch (PatternSyntaxException e) {
            NoRandomTicks.LOGGER.warn("Rejected invalid regex pattern '{}': {}", pattern, e.getMessage());
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

        clearRuntimeCaches();

        BLACKLIST_RANDOM.addAll(BLOCK_STRINGS_RANDOM.get());
        BLACKLIST_TICKS.addAll(BLOCK_STRINGS_TICKS.get());
        BLACKLIST_FLUID_TICKS.addAll(FLUID_STRINGS_TICKS.get());
        BLACKLIST_FLUID_RANDOM.addAll(FLUID_STRINGS_RANDOM.get());

        BLOCK_RANDOM_REGEX.addAll(BLOCK_STRINGS_RANDOM_REGEX.get());
        BLOCK_TICKS_REGEX.addAll(BLOCK_STRINGS_TICKS_REGEX.get());
        FLUID_TICKS_REGEX.addAll(FLUID_STRINGS_TICKS_REGEX.get());
        FLUID_RANDOM_REGEX.addAll(FLUID_STRINGS_RANDOM_REGEX.get());

        compileRegexes(BLOCK_RANDOM_REGEX, BLOCK_RANDOM_PATTERNS);
        compileRegexes(BLOCK_TICKS_REGEX, BLOCK_TICKS_PATTERNS);
        compileRegexes(FLUID_TICKS_REGEX, FLUID_TICKS_PATTERNS);
        compileRegexes(FLUID_RANDOM_REGEX, FLUID_RANDOM_PATTERNS);

        expandBlockRegexMatches(BLOCK_RANDOM_PATTERNS, BLACKLIST_RANDOM);
        expandBlockRegexMatches(BLOCK_TICKS_PATTERNS, BLACKLIST_TICKS);
        expandFluidRegexMatches(FLUID_TICKS_PATTERNS, BLACKLIST_FLUID_TICKS);
        expandFluidRegexMatches(FLUID_RANDOM_PATTERNS, BLACKLIST_FLUID_RANDOM);

        if (DEBUG.get()) {
            NoRandomTicks.LOGGER.info("NORANDOMTICKS DEBUG is ON. Set debug=false in the config to disable these messages.");
            NoRandomTicks.LOGGER.info("Block Random Ticks Blacklist (FULL): {}", BLACKLIST_RANDOM);
            NoRandomTicks.LOGGER.info("Block Random Ticks Regex: {}", BLOCK_RANDOM_REGEX);
            NoRandomTicks.LOGGER.info("Block Ticks Blacklist (FULL): {}", BLACKLIST_TICKS);
            NoRandomTicks.LOGGER.info("Block Ticks Regex: {}", BLOCK_TICKS_REGEX);
            NoRandomTicks.LOGGER.info("Fluid Ticks Blacklist (FULL): {}", BLACKLIST_FLUID_TICKS);
            NoRandomTicks.LOGGER.info("Fluid Ticks Regex: {}", FLUID_TICKS_REGEX);
            NoRandomTicks.LOGGER.info("Fluid Random Ticks Blacklist (FULL): {}", BLACKLIST_FLUID_RANDOM);
            NoRandomTicks.LOGGER.info("Fluid Random Ticks Regex: {}", FLUID_RANDOM_REGEX);
            NoRandomTicks.LOGGER.warn("Blocks failed to validate: {}", FAILED_BLACKLIST_BLOCKS);
            NoRandomTicks.LOGGER.warn("Fluids failed to validate: {}", FAILED_BLACKLIST_FLUIDS);
        }
    }

    private static void clearRuntimeCaches() {
        BLACKLIST_RANDOM.clear();
        BLACKLIST_TICKS.clear();
        BLACKLIST_FLUID_TICKS.clear();
        BLACKLIST_FLUID_RANDOM.clear();

        BLOCK_RANDOM_REGEX.clear();
        BLOCK_TICKS_REGEX.clear();
        FLUID_TICKS_REGEX.clear();
        FLUID_RANDOM_REGEX.clear();

        BLOCK_RANDOM_PATTERNS.clear();
        BLOCK_TICKS_PATTERNS.clear();
        FLUID_TICKS_PATTERNS.clear();
        FLUID_RANDOM_PATTERNS.clear();

        FAILED_BLACKLIST_BLOCKS.clear();
        FAILED_BLACKLIST_FLUIDS.clear();
    }

    private static void compileRegexes(ObjectOpenHashSet<String> source, List<Pattern> target) {
        for (String regex : source) {
            try {
                target.add(Pattern.compile(regex));
            } catch (PatternSyntaxException e) {
                NoRandomTicks.LOGGER.error("Failed to compile regex patterns: {}", e.getMessage());
            }
        }
    }

    private static void expandBlockRegexMatches(List<Pattern> patterns, ObjectOpenHashSet<String> target) {
        for (Block block : BuiltInRegistries.BLOCK) {
            ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);

            String blockId = id.toString();
            for (Pattern pattern : patterns) {
                if (pattern.matcher(blockId).matches()) {
                    target.add(blockId);
                    break;
                }
            }
        }
    }

    private static void expandFluidRegexMatches(List<Pattern> patterns, ObjectOpenHashSet<String> target) {
        for (var fluid : BuiltInRegistries.FLUID) {
            ResourceLocation id = BuiltInRegistries.FLUID.getKey(fluid);

            String fluidId = id.toString();
            for (Pattern pattern : patterns) {
                if (pattern.matcher(fluidId).matches()) {
                    target.add(fluidId);
                    break;
                }
            }
        }
    }
}