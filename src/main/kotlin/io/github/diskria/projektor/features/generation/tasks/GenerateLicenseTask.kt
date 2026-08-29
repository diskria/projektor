package io.github.diskria.projektor.features.generation.tasks

import io.github.diskria.projektor.core.model.license.LicenseType
import io.github.diskria.projektor.core.model.license.mapToModel
import io.github.diskria.projektor.internal.git.CommitType
import io.github.diskria.projektor.internal.utils.Envs
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.work.DisableCachingByDefault
import java.io.File
import javax.inject.Inject

@DisableCachingByDefault(because = "Generates files and performs Git push side effects")
abstract class GenerateLicenseTask @Inject constructor(envs: Envs) : AbstractGenerateFileTask(
    outputFileName = "LICENSE",
    commitType = CommitType.DOCS,
    envs = envs,
) {
    @get:Input
    abstract val licenseType: Property<LicenseType>

    @get:Input
    abstract val developer: Property<String>

    override fun getFileText(repoDirectory: File, file: File): String =
        licenseType.get().mapToModel().getLicenseText(developer.get())
}
