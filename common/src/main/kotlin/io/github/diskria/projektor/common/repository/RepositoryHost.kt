package io.github.diskria.projektor.common.repository

enum class RepositoryHost(val shortName: String, val hostname: String, val versionControlSystem: VersionControlSystem) {
    GITHUB("github", "github.com", VersionControlSystem.GIT)
}
