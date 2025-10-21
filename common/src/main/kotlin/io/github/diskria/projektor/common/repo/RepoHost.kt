package io.github.diskria.projektor.common.repo

enum class RepoHost(val shortName: String, val hostName: String, val vcs: VCS) {
    GITHUB("github", "github.com", VCS.GIT)
}
