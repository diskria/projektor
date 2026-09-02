package builder.metadata_generator.tasks

import io.github.diskria.poetesse.kotlin.file
import io.github.diskria.poetesse.kotlin.object_
import io.github.diskria.poetesse.kotlin.property
import io.github.diskria.poetesse.poetesse
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class GenerateBuildConfigTask : DefaultTask() {

    @get:Input
    abstract val pluginId: Property<String>

    @get:Input
    abstract val pluginVersion: Property<String>

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @TaskAction
    fun generate() {
        poetesse {
            kotlin.file("io.github.diskria.projektor.generated", "BuildConfig") {
                object_(fileName) {
                    property<String>("PLUGIN_ID") {
                        const()
                        initializer { S(pluginId.get()) }
                    }
                    property<String>("PLUGIN_VERSION") {
                        const()
                        initializer { S(pluginVersion.get()) }
                    }
                }
            }
        }.writeTo(outputDirectory.get().asFile.toPath())
    }
}
