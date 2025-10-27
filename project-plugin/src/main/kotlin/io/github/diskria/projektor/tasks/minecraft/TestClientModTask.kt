package io.github.diskria.projektor.tasks.minecraft

abstract class TestClientModTask : AbstractModTestTask() {

    override fun isServerSide(): Boolean = false
}
