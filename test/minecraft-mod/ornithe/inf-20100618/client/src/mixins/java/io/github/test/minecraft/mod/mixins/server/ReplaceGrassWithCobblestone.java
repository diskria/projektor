package io.github.test.minecraft.mod.mixins.server;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
class ReplaceGrassWithCobblestone {

    @Inject(method = "onSteppedOn", remap = false, at = @At("HEAD"), cancellable = true)
    private void replaceGrassWithCobblestone(World world, int x, int y, int z, Entity entity, CallbackInfo ci) {
        if (entity instanceof PlayerEntity && world.m_9893076(x, y, z) == Block.GRASS.id) {
            world.setBlock(x, y, z, Block.COBBLESTONE.id);
            ci.cancel();
        }
    }
}
