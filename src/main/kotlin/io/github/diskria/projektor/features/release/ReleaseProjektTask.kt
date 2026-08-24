package io.github.diskria.projektor.features.release

import io.github.diskria.projektor.extensions.applyProjektorGroup
import org.gradle.api.DefaultTask
import org.gradle.work.DisableCachingByDefault

@DisableCachingByDefault(because = "Releasing involves external side effects like Git commits and tags")
internal abstract class ReleaseProjektTask : DefaultTask() {

    init {
        applyProjektorGroup()
    }
}
