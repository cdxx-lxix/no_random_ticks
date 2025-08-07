package com.cdxx_lxix.norandomticks;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

@Mod.EventBusSubscriber(modid = NoRandomticksMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> BLOCK_STRINGS_RANDOM = BUILDER
            .comment("A list of IDs to shut them up. Every element must be in \"\" and divided by comma from one another.")
            .comment("Prevents RANDOM ticking of any block in the game. Be it vanilla or modded.")
            .comment("E.G. [\"minecraft:copper_block\", \"minecraft:bamboo\"]")
            .defineListAllowEmpty("randomticks_blocks", List.of(""), Config::validateItemName);

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> BLOCK_STRINGS_TICKS = BUILDER
            .comment("Prevents TICKING of any block in the game. Be it vanilla or modded. A list of IDs separated by comma just like with randoms.")
            .comment("USE AT YOUR OWN DISCRETION. YOU MUST KNOW WHAT YOU ARE DOING OR YOU CAN BREAK GAMEPLAY.")
            .comment("E.G. [\"minecraft:fire\"]")
            .defineListAllowEmpty("ticks_blocks", List.of(""), Config::validateItemName);

    private static final ForgeConfigSpec.BooleanValue DEBUG = BUILDER
            .comment("Set it to 'true' to allow console messages on loading and reloading that will list all of the blocks to be blocked from random and regular ticking")
            .comment("You can find them later in the console - 'NORANDOMTICKS | DEBUG' will appear at the start of the message")
            .define("debug", false);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    //OpenHashSet for speeeeeeeeed
    public static final ObjectOpenHashSet<Block> BLACKLIST_RANDOM = new ObjectOpenHashSet<>();

    public static final ObjectOpenHashSet<Block> BLACKLIST_TICKS = new ObjectOpenHashSet<>();

    private static boolean validateItemName(final Object obj) {
        // Prevent non-blocks or bad names
        return obj instanceof final String itemName && ForgeRegistries.BLOCKS.containsKey(new ResourceLocation(itemName));
    }

    private static boolean validateItemBoolean(ForgeConfigSpec.BooleanValue obj) {
        return obj instanceof ForgeConfigSpec.BooleanValue;
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        NoRandomticksMod.LOG.debug("config reloaded");
        if (event.getConfig().getModId().equals(NoRandomticksMod.MODID)) {
            // Clears lists just in case and adds one-by-one each blocked item.
            BLACKLIST_RANDOM.clear();
            BLACKLIST_TICKS.clear();
            BLOCK_STRINGS_RANDOM.get().forEach(s -> BLACKLIST_RANDOM.add(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(s))));
            BLOCK_STRINGS_TICKS.get().forEach(s -> BLACKLIST_TICKS.add(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(s))));
            if(validateItemBoolean(DEBUG)) {
                NoRandomticksMod.LOG.info("NORANDOMTICKS | DEBUG ");
                NoRandomticksMod.LOG.info("You have enabled debugging. To remove set it to 'false' in the config. ");
                NoRandomticksMod.LOG.debug("Random Blacklist: " + BLACKLIST_RANDOM);
                NoRandomticksMod.LOG.debug("Tick Blacklist: " + BLACKLIST_TICKS);
            }

        }
    }
}

