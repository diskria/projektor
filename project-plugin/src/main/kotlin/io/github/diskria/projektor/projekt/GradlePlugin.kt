package io.github.diskria.projektor.projekt

open class GradlePlugin(private val delegate: IProjekt) : IProjekt by delegate {
    var isSettingsPlugin: Boolean = false
    var tags: Set<String> = emptySet()
}
