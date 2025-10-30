package io.github.test.minecraft.mod.mixins.extensions;

import io.github.test.minecraft.mod.common.BlockContract;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Block.class)
class BlockExtension implements BlockContract {

    @Override
    @Unique
    public boolean mod_isGrassBlock() {
        Block block = (Block) (Object) this;
        return block == Blocks.GRASS;
    }
}
