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

    // a list of strings that are treated as resource locations for items
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> BLOCK_STRINGS = BUILDER
            .comment("A list of IDs to shut them up. Every element must be in \"\" and divided by , from another.")
            .worldRestart()
            .defineListAllowEmpty("items", List.of("minecraft:copper_block"), Config::validateItemName);

    static final ForgeConfigSpec SPEC = BUILDER.build();
    public static final ObjectOpenHashSet<Block> BLACKLIST = new ObjectOpenHashSet<>();

    private static boolean validateItemName(final Object obj) {
        return obj instanceof final String itemName && ForgeRegistries.BLOCKS.containsKey(new ResourceLocation(itemName));
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        NoRandomticksMod.LOG.debug("config reloaded");
        if (event.getConfig().getModId().equals(NoRandomticksMod.MODID)) {
            BLACKLIST.clear();
            BLOCK_STRINGS.get().forEach(s -> BLACKLIST.add(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(s))));
        }
    }
}
