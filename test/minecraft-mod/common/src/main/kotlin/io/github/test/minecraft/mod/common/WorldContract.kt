package io.github.test.minecraft.mod.common

interface WorldContract<B, P> {
    fun mod_isServerWorld(): Boolean
    fun mod_getBlock(pos: P): B
    fun mod_replaceWithCobblestone(pos: P)
}
