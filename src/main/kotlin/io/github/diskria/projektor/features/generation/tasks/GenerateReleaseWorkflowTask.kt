package io.github.diskria.projektor.features.generation.tasks

import io.github.diskria.projektor.core.model.ToolchainDefaults
import io.github.diskria.projektor.extensions.applyProjektorGroup
import io.github.diskria.projektor.extensions.defaultTaskName
import io.github.diskria.projektor.features.release.ReleaseProjektTask
import io.github.diskria.projektor.internal.git.CommitType
import io.github.diskria.projektor.internal.utils.ACTION_BUILTIN_ENVS
import io.github.diskria.projektor.internal.utils.Envs
import io.github.diskria.projektor.internal.utils.SECRET_ENV_NAMES
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.tasks.Input
import org.gradle.work.DisableCachingByDefault
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import java.io.File
import javax.inject.Inject

@DisableCachingByDefault
abstract class GenerateReleaseWorkflowTask @Inject internal constructor(
    envs: Envs,
) : AbstractGenerateFileTask(YML_PATH, CommitType.CI, envs) {

    @get:Input
    abstract val actionBuiltinEnvs: MapProperty<String, String>

    @get:Input
    abstract val secretEnvNames: ListProperty<String>

    init {
        applyProjektorGroup()
        actionBuiltinEnvs.set(ACTION_BUILTIN_ENVS)
        secretEnvNames.set(SECRET_ENV_NAMES)
    }

    override fun getFileText(repoDirectory: File, file: File): String {
        val isReusableWorkflow = repo.get().path == REUSABLE_WORKFLOW_REPO
        val callerContract = if (isReusableWorkflow) {
            "workflow_call" to mapOf("secrets" to secretEnvNames.get().associateWith { mapOf("required" to true) })
        } else null
        val workflow = mapOf(
            "name" to "Release",
            "on" to listOfNotNull(
                "workflow_dispatch" to emptyMap<Any, Any>(),
                "push" to mapOf("tags" to listOf("v*")),
                callerContract,
            ).toMap(),
            "permissions" to mapOf("contents" to "write", "packages" to "write"),
            "jobs" to mapOf("release" to buildReleaseJob(isReusableWorkflow)),
        )
        return Yaml(dumperOptions).dump(workflow).replace("'on':", "on:")
    }

    private fun buildReleaseJob(isReusableWorkflow: Boolean): Map<Any, Any> {
        val secretEnvs = secretEnvNames.get().associateWith { "secrets.$it".expression() }
        return if (isReusableWorkflow) {
            val checkoutStep = mapOf(
                "uses" to CHECKOUT_ACTION,
                "with" to mapOf(
                    "ref" to "github.event.repository.default_branch".expression(),
                    "fetch-depth" to 1,
                ),
            )
            val setupJavaStep = mapOf(
                "uses" to SETUP_JAVA_ACTION,
                "with" to mapOf(
                    "distribution" to ToolchainDefaults.JVM_VENDOR,
                    "java-version" to ToolchainDefaults.JAVA_VERSION,
                ),
            )
            val tasks = listOf("clean", defaultTaskName<ReleaseProjektTask>())
            val releaseStep = mapOf("shell" to "bash", "run" to "./gradlew ${tasks.joinToString(" ")} --stacktrace")
            mapOf(
                "runs-on" to "ubuntu-latest",
                "env" to actionBuiltinEnvs.get().mapValues { it.value.expression() } + secretEnvs,
                "steps" to listOf(checkoutStep, setupJavaStep, releaseStep),
            )
        } else {
            mapOf(
                "uses" to "$REUSABLE_WORKFLOW_REPO/$YML_PATH@main",
                "secrets" to secretEnvs,
            )
        }
    }

    companion object {
        private const val REUSABLE_WORKFLOW_REPO = "diskria/projektor"
        private const val YML_PATH = ".github/workflows/release.yml"

        private const val CHECKOUT_ACTION = "actions/checkout@v7"
        private const val SETUP_JAVA_ACTION = "actions/setup-java@v6"

        private val dumperOptions = DumperOptions().apply {
            defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
            indicatorIndent = 2
            indentWithIndicator = true
        }
    }
}

private fun String.expression(): String = $$"${{ $$this }}"
