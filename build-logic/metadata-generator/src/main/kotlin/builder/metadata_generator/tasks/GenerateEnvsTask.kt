package builder.metadata_generator.tasks

import builder.metadata_generator.extensions.capitalized
import builder.metadata_generator.extensions.quoted
import io.github.diskria.poetesse.interop.generic
import io.github.diskria.poetesse.interop.xClass
import io.github.diskria.poetesse.interop.xType
import io.github.diskria.poetesse.kotlin.*
import io.github.diskria.poetesse.poetesse
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class GenerateEnvsTask : DefaultTask() {

    @get:Input
    abstract val actionBuiltinEnvs: MapProperty<String, String>

    @get:Input
    abstract val secretEnvNames: ListProperty<String>

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    init {
        actionBuiltinEnvs.set(
            mapOf(
                "GH_OWNER" to "github.repository_owner",
                "GH_REPO" to "github.event.repository.name",
            )
        )
        secretEnvNames.set(
            listOf(
                "GH_TOKEN",
                "GH_PACKAGES_TOKEN",
                "GPG_KEY",
                "GPG_PASSPHRASE",
                "SONATYPE_USERNAME",
                "SONATYPE_PASSWORD",
                "GRADLE_PUBLISH_KEY",
                "GRADLE_PUBLISH_SECRET",
            )
        )
    }

    @TaskAction
    fun generate() {
        poetesse {
            val getEnvOrNull by kotlin.function {
                private()
                val name by parameter<String>()
                returns<String?>()
                expression { "providers.environmentVariable($name).orNull" }
            }
            val getEnv by kotlin.function {
                private()
                val name by parameter<String>()
                returns<String>()
                expression {
                    val errorMessage = code { "Environment variable '$$name' is required but not set!".quoted() }
                    "$getEnvOrNull($name) ?: error(${L(errorMessage)})"
                }
            }
            kotlin.file("io.github.diskria.projektor.generated", "Envs") {
                class_(fileName) {
                    constructor(primary = true) { parameter<ProviderFactory>("providers").property { private() } }
                    property<Boolean>("isCI") {
                        getter { expression { "$getEnvOrNull(${S("CI")})?.toBoolean() == true" } }
                    }
                    val envNames = actionBuiltinEnvs.get().keys + secretEnvNames.get()
                    envNames.forEach { name ->
                        val propertyName = name.lowercase().split('_').withIndex().joinToString("") { (index, part) ->
                            when {
                                index > 0 -> part.capitalized()
                                part == "gh" -> "github"
                                else -> part
                            }
                        }
                        property<String>(propertyName) {
                            getter { expression { "$getEnv(${S(name)})" } }
                        }
                    }
                    +getEnvOrNull
                    +getEnv
                    companion_object {
                        property("actionBuiltins", xClass<Map<*, *>>().generic(xType<String>(), xType<String>())) {
                            initializer {
                                val pairs = code {
                                    actionBuiltinEnvs.get().entries.joinToString(", ") {
                                        "${S(it.key)} to ${S(it.value)}"
                                    }
                                }
                                "mapOf(${L(pairs)})"
                            }
                        }
                        property("secretNames", xClass<List<*>>().generic(xType<String>())) {
                            initializer { "listOf(${secretEnvNames.get().joinToString(", ") { S(it) }})" }
                        }
                    }
                }
            }
        }.writeTo(outputDirectory.get().asFile.toPath())
    }
}
