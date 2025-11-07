package io.github.diskria.projektor.tasks.minecraft.test

import io.github.diskria.projektor.common.minecraft.sides.ModSide
import io.github.diskria.projektor.tasks.minecraft.test.common.AbstractTestModTask

abstract class TestClientModTask : AbstractTestModTask() {
    override fun getSide(): ModSide = ModSide.CLIENT
}
