package io.github.diskria.projektor.features.distribution.tasks

import io.github.diskria.projektor.ProjektorGradlePlugin
import io.github.diskria.projektor.core.model.DistributableProjektModel
import io.github.diskria.projektor.extensions.applyProjektorGroup
import io.github.diskria.projektor.extensions.writeTextCreatingParent
import kotlinx.serialization.json.Json
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class GenerateDistributableProjektModelTask : DefaultTask() {

    @get:Input
    abstract val model: Property<DistributableProjektModel>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    init {
        applyProjektorGroup()
        outputFile.convention(
            project.layout.buildDirectory.file("intermediates/${ProjektorGradlePlugin.ID}/model.json")
        )
    }

    @TaskAction
    fun run() {
        outputFile.get().writeTextCreatingParent(Json.encodeToString(model.get()))
    }
}
