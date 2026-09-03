package builder.metadata_generator.extensions

import org.gradle.api.Task

internal fun Task.applyBuildLogicGroup() {
    group = "build logic"
}
