package builder.envs_generator.tasks

import builder.envs_generator.extensions.capitalized
import builder.envs_generator.extensions.quoted
import builder.envs_generator.models.Env
import io.github.diskria.poetesse.interop.XParameter
import io.github.diskria.poetesse.interop.xClass
import io.github.diskria.poetesse.kotlin.*
import io.github.diskria.poetesse.poetesse
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

abstract class GenerateEnvsClassTask : DefaultTask() {

    @get:Input
    abstract val envs: ListProperty<Env>

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    init {
        outputDirectory.set(project.layout.buildDirectory.dir("generated/sources/kotlin/main"))
    }

    @TaskAction
    fun generate() {
        poetesse {
            val packageName = "io.github.diskria.projektor.internal.utils"
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
            kotlin.file(packageName, "Envs") {
                class_(fileName) {
                    constructor(primary = true) { parameter<ProviderFactory>("providers").property { private() } }
                    property<Boolean>("isCI") {
                        getter { expression { "${N(getEnvOrNull)}(${S("CI")})?.toBoolean() == true" } }
                    }
                    envs.get().forEach { env ->
                        property<String>(env.name.toCamelCase()) {
                            getter { expression { "${N(getEnv)}(${S(env.name)})" } }
                        }
                    }
                    +getEnvOrNull
                    +getEnv
                }
            }
        }.writeTo(outputDirectory.get().asFile.toPath())
    }
}

fun KotlinCodeScope.N(value: KotlinFunctionRef): String = argument('L', value.name)
fun KotlinCodeScope.N(value: XParameter): String = argument('L', value.name)

private fun String.toCamelCase(): String =
    lowercase().split('_').withIndex().joinToString("") { (index, part) ->
        if (index == 0) part else part.capitalized()
    }
