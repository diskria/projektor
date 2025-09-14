apply(from = "../../bisectrix/settings/project.settings.gradle.kts")
apply(from = "../../bisectrix/settings/android-app.settings.gradle.kts")
apply(from = "../../bisectrix/settings/minecraft-mod.settings.gradle.kts")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}
