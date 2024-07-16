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

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> BLOCK_STRINGS = BUILDER
            .comment("A list of IDs to shut them up. Every element must be in \"\" and divided by , from one another.")
            .comment("You can prevent RANDOM ticking of any block in the game. Be it vanilla or modded.")
            .defineListAllowEmpty("blocks", List.of("minecraft:copper_block"), Config::validateItemName);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    //OpenHashSet for speeeeeeeeed
    public static final ObjectOpenHashSet<Block> BLACKLIST = new ObjectOpenHashSet<>();

    public static final ObjectOpenHashSet<Block> TICKLIST = new ObjectOpenHashSet<>();

    private static boolean validateItemName(final Object obj) {
        // Prevent non-blocks or bad names
        return obj instanceof final String itemName && ForgeRegistries.BLOCKS.containsKey(new ResourceLocation(itemName));
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        NoRandomticksMod.LOG.debug("config reloaded");
        if (event.getConfig().getModId().equals(NoRandomticksMod.MODID)) {
            // Clears BLACKLIST just in case and adds one-by-one each blocked item.
            BLACKLIST.clear();
            BLOCK_STRINGS.get().forEach(s -> BLACKLIST.add(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(s))));
        }
    }
}
