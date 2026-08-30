package io.github.diskria.projektor.features.release

import io.github.diskria.projektor.extensions.applyProjektorGroup
import io.github.diskria.projektor.internal.utils.DisabledCachingReasons.LIFECYCLE
import org.gradle.api.DefaultTask
import org.gradle.work.DisableCachingByDefault

@DisableCachingByDefault(because = LIFECYCLE)
abstract class ReleaseProjektTask : DefaultTask() {

    init {
        applyProjektorGroup()
    }
}
