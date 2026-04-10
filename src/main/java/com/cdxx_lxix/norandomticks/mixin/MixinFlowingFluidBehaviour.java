package com.cdxx_lxix.norandomticks.mixin;

import com.cdxx_lxix.norandomticks.NoRandomticksMod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(FlowingFluid.class)
public abstract class MixinFlowingFluidBehaviour {

    @Shadow public abstract Fluid getFlowing();

    @Inject(method= "tick", at = @At("HEAD"), cancellable = true)
    private void tickFluidInjection(Level level, BlockPos pos, FluidState state, CallbackInfo ci) {
        Objects.requireNonNull(level.getServer()).getProfiler().push("no_tick_check_fluids");
        if (NoRandomticksMod.isTickingAllowedFluids(this.getFlowing())) {
            ci.cancel();
        }
        level.getServer().getProfiler().pop();
    }
}
