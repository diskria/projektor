package io.github.test.minecraft.mod.mixins.extensions;

import io.github.test.minecraft.mod.common.WorldContract;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(World.class)
abstract class WorldExtension implements WorldContract<Block, BlockPos> {

    @Override
    @Unique
    public boolean mod_isServerWorld() {
        World world = (World) (Object) this;
        return !world.isClient();
    }

    @Override
    @Unique
    public Block mod_getBlock(BlockPos pos) {
        return getTopBlockState(pos).getBlock();
    }

    @Override
    @Unique
    public void mod_replaceWithCobblestone(@NotNull BlockPos pos) {
        World world = (World) (Object) this;
        world.setBlockState(pos, Blocks.COBBLESTONE.defaultState());
    }

    @Shadow
    public abstract BlockState getTopBlockState(BlockPos par1);
}
