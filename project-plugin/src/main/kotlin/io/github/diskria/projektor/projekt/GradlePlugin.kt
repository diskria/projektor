package io.github.diskria.projektor.projekt

class GradlePlugin(
    val id: String,
    val className: String,
    val isSettingsPlugin: Boolean,
    private val delegate: IProjekt,
) : IProjekt by delegate
