package io.github.diskria.projektor.internal.utils

object DisabledCachingReasons {
    const val SIDE_EFFECTS = "Produces external side effects"
    const val NON_DETERMINISTIC = "Process or inputs are non-deterministic"
    const val LIFECYCLE = "Lifecycle task with no outputs of its own"
}
