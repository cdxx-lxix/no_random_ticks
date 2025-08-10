package com.cdxx_lxix.norandomticks.mixin;

import com.cdxx_lxix.norandomticks.NoRandomTicks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(FluidState.class)
public abstract class MixinFluidBehaviour {
    @Shadow public abstract Fluid getType();

    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    private void randomTickFluidInjection(Level level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        Objects.requireNonNull(level.getServer()).getProfiler().push("no_random_tick_check_fluids");
        if (NoRandomTicks.isRandomTickingAllowedFluids(this.getType())) {
            ci.cancel();
        }
        level.getServer().getProfiler().pop();
    }
}
