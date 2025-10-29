package io.github.diskria.projektor.tasks.minecraft.test

import io.github.diskria.projektor.tasks.minecraft.test.common.AbstractModTestTask

abstract class TestClientModTask : AbstractModTestTask() {

    override fun isServerSide(): Boolean = false
}
