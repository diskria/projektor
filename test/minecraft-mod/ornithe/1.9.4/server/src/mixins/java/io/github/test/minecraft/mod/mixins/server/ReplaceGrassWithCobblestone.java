package io.github.test.minecraft.mod.mixins.server;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
class ReplaceGrassWithCobblestone {

    @Inject(method = "onSteppedOn", remap = false, at = @At("HEAD"), cancellable = true)
    private void replaceGrassWithCobblestone(World world, BlockPos pos, Entity entity, CallbackInfo ci) {
        if (entity instanceof PlayerEntity && world.getBlockState(pos).getBlock() == Blocks.GRASS) {
            world.setBlockState(pos, Blocks.COBBLESTONE.defaultState());
            ci.cancel();
        }
    }
}
