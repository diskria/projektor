package io.github.test.minecraft.mod.mixins.server;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
class ReplaceGrassWithCobblestone {

    @Inject(
            method = "method_437",
            at = @At("HEAD"),
            cancellable = true
    )
    private void replaceGrassWithCobblestone(World world, int x, int y, int z, Entity entity, CallbackInfo ci) {
        if (!world.isClient && entity instanceof PlayerEntity && world.getBlock(x, y, z) == Block.GRASS_BLOCK.id) {
            world.method_3690(x, y, z, Block.STONE_BRICKS.id);
            ci.cancel();
        }
    }
}
