package com.cdxx_lxix.norandomticks;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(NoRandomticksMod.MODID)
public class NoRandomticksMod {
    public static final String MODID = "norandomticks";
    public static final Logger LOG = LogManager.getLogger(NoRandomticksMod.class.getSimpleName());

    public NoRandomticksMod() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    public static boolean notickcheck(BlockState blockState) {
        return Config.BLACKLIST.contains(blockState.getBlock());
    }
}
