package io.github.diskria.projektor.features.generation.tasks

import io.github.diskria.projektor.core.model.license.LicenseType
import io.github.diskria.projektor.core.model.license.mapToModel
import io.github.diskria.projektor.internal.git.CommitType
import io.github.diskria.projektor.internal.utils.DisabledCachingReasons.SIDE_EFFECTS
import io.github.diskria.projektor.internal.utils.Envs
import org.gradle.api.file.ProjectLayout
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.work.DisableCachingByDefault
import java.io.File
import javax.inject.Inject

@DisableCachingByDefault(because = SIDE_EFFECTS)
abstract class GenerateLicenseTask @Inject constructor(
    envs: Envs,
    layout: ProjectLayout,
) : AbstractGenerateFileTask("LICENSE", CommitType.DOCS, envs, layout) {

    @get:Input
    abstract val licenseType: Property<LicenseType>

    @get:Input
    abstract val developer: Property<String>

    override fun getFileText(repoDirectory: File, file: File): String =
        licenseType.get().mapToModel().getLicenseText(developer.get())
}
