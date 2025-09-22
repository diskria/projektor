package io.github.diskria.projektor.extensions

import io.github.diskria.projektor.licenses.License
import io.github.diskria.projektor.licenses.MitLicense
import io.github.diskria.projektor.owner.domain.AndroidDomain
import io.github.diskria.projektor.projekt.IProjekt
import io.github.diskria.projektor.projekt.Projekt
import io.github.diskria.projektor.projekt.PublishingTarget
import org.gradle.api.Project

private val androidPluginIds: Array<String> by lazy {
    arrayOf(
        "com.android.application",
        "com.android.library",
        "com.android.test",
        "com.android.dynamic-feature",
    )
}

fun Project.ifAndroid(block: () -> Unit) {
    androidPluginIds.forEach { pluginId ->
        plugins.withId(pluginId) { block() }
    }
}

fun Project.configureAndroidApp(license: License = MitLicense): IProjekt {
    val app = Projekt.of(this, AndroidDomain, license).toAndroidApp()
    configureProjekt(app)
    configurePublishing(app, PublishingTarget.GOOGLE_PLAY)
    return app
}

fun Project.configureGooglePlayPublishing(project: IProjekt) {
}
