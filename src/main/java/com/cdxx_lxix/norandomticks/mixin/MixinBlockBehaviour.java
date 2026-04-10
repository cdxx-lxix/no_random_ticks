package com.cdxx_lxix.norandomticks.mixin;

import com.cdxx_lxix.norandomticks.NoRandomticksMod;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets="net.minecraft.world.level.block.state.BlockBehaviour$BlockStateBase")
public abstract class MixinBlockBehaviour {
    @Shadow public abstract Block getBlock();

    // Injects into randomTick() method of the BlockBehaviour class and prevents it from happening if a block is in BLACKLIST_RANDOM.
    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    private void randomTickInjection(ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        level.getServer().getProfiler().push("no_random_tick_check_blocks");
        if (NoRandomticksMod.isRandomTickAllowed(this.getBlock())) {
            ci.cancel();
        }
        level.getServer().getProfiler().pop();
    }

    // Injects into tick() method of the BlockBehaviour class and prevents it from happening if a block is in BLACKLIST_TICKS.
    @Inject(method= "tick", at = @At("HEAD"), cancellable = true)
    private void tickInjection(ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        level.getServer().getProfiler().push("no_tick_check_blocks");
        if (NoRandomticksMod.isTickingAllowed(this.getBlock())) {
            ci.cancel();
        }
        level.getServer().getProfiler().pop();
    }
}
