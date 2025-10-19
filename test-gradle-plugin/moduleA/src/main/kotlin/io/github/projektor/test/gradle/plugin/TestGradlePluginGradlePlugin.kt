package io.github.projektor.test.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class TestGradlePluginGradlePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        println("Projektor test plugin applied!")
    }
}
