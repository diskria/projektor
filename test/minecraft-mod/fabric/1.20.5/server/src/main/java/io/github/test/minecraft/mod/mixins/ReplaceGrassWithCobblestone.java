package io.github.test.minecraft.mod.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
class ReplaceGrassWithCobblestone {

    @Inject(
            method = "onSteppedOn",
            at = @At("HEAD"),
            cancellable = true
    )
    private void replaceGrassWithCobblestone(
            @NotNull World world,
            BlockPos blockPos,
            BlockState state,
            Entity entity,
            CallbackInfo ci
    ) {
        if (!world.isClient() &&
                entity instanceof PlayerEntity &&
                world.getBlockState(blockPos).getBlock() == Blocks.GRASS_BLOCK
        ) {
            world.setBlockState(blockPos, Blocks.COBBLESTONE.getDefaultState());
            ci.cancel();
        }
    }
}
