package builder.envs_generator.tasks

import builder.envs_generator.models.Env
import builder.envs_generator.models.SecretEnv
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml

abstract class GenerateReusableWorkflowTask : DefaultTask() {

    @get:Input
    abstract val envs: ListProperty<Env>

    @get:Input
    abstract val taskNames: ListProperty<String>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    init {
        outputFile.set(project.layout.projectDirectory.file(".github/workflows/release.yml"))
    }

    @TaskAction
    fun generate() {
        val configMap = mapOf(
            "name" to "Release",
            "on" to mapOf(
                "workflow_dispatch" to emptyMap<Any, Any>(),
                "push" to mapOf("tags" to listOf("v*")),
                "workflow_call" to buildCallerBlock(),
            ),
            "permissions" to mapOf("contents" to "write", "packages" to "write"),
            "jobs" to mapOf("release" to buildReleaseJob()),
        )
        val yaml = Yaml(dumperOptions).dump(configMap).replace("'on':", "on:")
        val outputFile = outputFile.get().asFile
        outputFile.parentFile.mkdirs()
        outputFile.writeText(yaml)
    }

    private fun buildReleaseJob(): Map<Any, Any> {
        val checkoutStep = mapOf(
            "uses" to "actions/checkout@v5",
            "with" to mapOf(
                "ref" to "github.event.repository.default_branch".expression(),
                "fetch-depth" to 1,
            ),
        )
        val setupJavaStep = mapOf(
            "uses" to "actions/setup-java@v5",
            "with" to mapOf(
                "distribution" to "temurin",
                "java-version" to 25,
            ),
        )
        val releaseStep = taskNames.get().ifEmpty { null }?.joinToString(" ")?.let { tasks ->
            mapOf("shell" to "bash", "run" to "./gradlew $tasks --stacktrace")
        }
        return mapOf(
            "runs-on" to "ubuntu-latest",
            "env" to envs.get().associate { it.name to it.value.expression() },
            "steps" to listOfNotNull(checkoutStep, setupJavaStep, releaseStep),
        )
    }

    private fun buildCallerBlock(): Map<Any, Any> {
        val secretNames = envs.get().filterIsInstance<SecretEnv>().map { it.secretName }
        return mapOf("secrets" to secretNames.associateWith { mapOf("required" to true) })
    }

    companion object {
        private val dumperOptions = DumperOptions().apply {
            defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
            indicatorIndent = 2
            indentWithIndicator = true
        }
    }
}

private fun String.expression(): String = $$"${{ $$this }}"
