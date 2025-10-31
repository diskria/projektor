package io.github.diskria.projektor.tasks.minecraft.test

import io.github.diskria.projektor.common.minecraft.ModSide
import io.github.diskria.projektor.tasks.minecraft.test.common.AbstractTestModTask

abstract class TestServerModTask : AbstractTestModTask() {
    override fun getSide(): ModSide = ModSide.SERVER
}
