package io.github.diskria.projektor.tasks.minecraft.test

import io.github.diskria.projektor.tasks.minecraft.test.common.AbstractModTestTask

abstract class TestServerModTask : AbstractModTestTask() {

    override fun isServerSide(): Boolean = true
}
