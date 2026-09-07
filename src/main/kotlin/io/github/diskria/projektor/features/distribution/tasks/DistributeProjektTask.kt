package io.github.diskria.projektor.features.distribution.tasks

import io.github.diskria.projektor.extensions.applyProjektorGroup
import io.github.diskria.projektor.internal.utils.DisabledCachingReasons.LIFECYCLE
import org.gradle.api.DefaultTask
import org.gradle.work.DisableCachingByDefault

@DisableCachingByDefault(because = LIFECYCLE)
abstract class DistributeProjektTask : DefaultTask() {

    init {
        applyProjektorGroup()
    }
}
