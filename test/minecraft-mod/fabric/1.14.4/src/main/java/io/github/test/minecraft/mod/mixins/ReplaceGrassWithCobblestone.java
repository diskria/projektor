package io.github.test.minecraft.mod.mixins;

import io.github.test.minecraft.mod.common.BlockContract;
import io.github.test.minecraft.mod.common.EntityContract;
import io.github.test.minecraft.mod.common.WorldContract;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
    private void replaceGrassWithCobblestone(World world, BlockPos blockPos, Entity entity, CallbackInfo ci) {
        WorldContract<Block, BlockPos> worldContract = (WorldContract<Block, BlockPos>) world;
        EntityContract entityContract = (EntityContract) entity;
        if (worldContract.mod_isServerWorld() && entityContract.mod_isPlayer()) {
            BlockContract blockContract = (BlockContract) worldContract.mod_getBlock(blockPos);
            if (blockContract.mod_isGrassBlock()) {
                worldContract.mod_replaceWithCobblestone(blockPos);
                ci.cancel();
            }
        }
    }
}
