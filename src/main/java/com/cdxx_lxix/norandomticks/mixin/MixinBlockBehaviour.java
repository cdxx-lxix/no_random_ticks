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

    // Injects into randomTick method of the BlockBehaviour class and prevents it from happening if a block is in BLACKLIST.
    @Inject(method = "randomTick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;)V", at = @At("HEAD"), cancellable = true)
    private void randomTickInjection(ServerLevel pLevel, BlockPos pPos, RandomSource pRandom, CallbackInfo ci) {
        pLevel.getServer().getProfiler().push("norandomtickcheck_blocks");
        if (NoRandomticksMod.isRandomTickAllowed(this.getBlock())) {
            ci.cancel();
        }
        pLevel.getServer().getProfiler().pop();
    }

    @Inject(method= "tick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;)V", at = @At("HEAD"), cancellable = true)
    private void tickInjection(ServerLevel pLevel, BlockPos pPos, RandomSource pRandom, CallbackInfo ci) {

    }
}
