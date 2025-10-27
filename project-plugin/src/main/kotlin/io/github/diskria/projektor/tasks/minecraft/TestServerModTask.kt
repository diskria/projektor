package io.github.diskria.projektor.tasks.minecraft

abstract class TestServerModTask : AbstractModTestTask() {

    override fun isServerSide(): Boolean = true
}
