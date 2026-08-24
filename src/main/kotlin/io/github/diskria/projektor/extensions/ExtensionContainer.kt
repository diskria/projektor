package io.github.diskria.projektor.extensions

import org.gradle.api.invocation.Gradle

internal var Gradle.isProjektorSettingsApplied: Boolean
    get() = extensions.extraProperties.has("isProjektorSettingsApplied") &&
        extensions.extraProperties.get("isProjektorSettingsApplied") == true
    set(value) {
        extensions.extraProperties.set("isProjektorSettingsApplied", value)
    }
