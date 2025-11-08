package io.github.test.minecraft.mod.mixins.server;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
class ReplaceGrassWithCobblestone {

    @Shadow
    @Final
    public static int UPDATE_NEIGHBORS;

    @Shadow
    @Final
    public static int UPDATE_CLIENTS;

    @Inject(
            method = "stepOn",
            at = @At("HEAD"),
            cancellable = true
    )
    private void replaceGrassWithCobblestone(
            @NotNull Level world,
            BlockPos blockPos,
            BlockState state,
            Entity entity,
            CallbackInfo ci
    ) {
        if (!world.isClientSide() &&
                entity instanceof Player &&
                world.getBlockState(blockPos).getBlock() == Blocks.GRASS_BLOCK
        ) {
            world.canHaveWeather();
            world.setBlock(blockPos, Blocks.COBBLESTONE.defaultBlockState(), UPDATE_NEIGHBORS | UPDATE_CLIENTS);
            ci.cancel();
        }
    }
}
