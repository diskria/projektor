package builder.envs_generator.tasks

import builder.envs_generator.extensions.capitalized
import builder.envs_generator.extensions.quoted
import io.github.diskria.poetesse.interop.XParameter
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
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault

@DisableCachingByDefault
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
                expression { "providers.environmentVariable(${N(name)}).orNull" }
            }
            val getEnv by kotlin.function {
                private()
                val name by parameter<String>()
                returns<String>()
                expression {
                    val errorMessage = code { "Environment variable '$${N(name)}' is required but not set!".quoted() }
                    "${N(getEnvOrNull)}(${N(name)}) ?: error(${L(errorMessage)})"
                }
            }
            kotlin.file("io.github.diskria.projektor.internal.utils", "Envs") {
                class_(fileName) {
                    constructor(primary = true) { parameter<ProviderFactory>("providers").property { private() } }
                    property<Boolean>("isCI") {
                        getter { expression { "${N(getEnvOrNull)}(${S("CI")})?.toBoolean() == true" } }
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
                            getter { expression { "${N(getEnv)}(${S(name)})" } }
                        }
                    }
                    +getEnvOrNull
                    +getEnv
                    companion {
                        property("actionBuiltins", xClass<Map<*, *>>().generic(xType<String>(), xType<String>())) {
                            initializer {
                                val pairs = code {
                                    actionBuiltinEnvs.get().entries.joinToString(", ") { (name, value) ->
                                        "${S(name)} to ${S(value)}"
                                    }
                                }
                                "mapOf(${L(pairs)})"
                            }
                        }
                        property("secretNames", xClass<List<*>>().generic(xType<String>())) {
                            initializer {
                                val elements = code { secretEnvNames.get().joinToString(", ") { S(it) } }
                                "listOf(${L(elements)})"
                            }
                        }
                    }
                }
            }
        }.writeTo(outputDirectory.get().asFile.toPath())
    }
}

fun KotlinCodeScope.N(value: KotlinFunctionRef): String = argument('L', value.name)
fun KotlinCodeScope.N(value: XParameter): String = argument('L', value.name)
