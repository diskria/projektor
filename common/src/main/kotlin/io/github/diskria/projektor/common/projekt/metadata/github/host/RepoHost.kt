package io.github.diskria.projektor.common.projekt.metadata.github.host

import io.github.diskria.projektor.common.projekt.metadata.github.vcs.VersionControlSystem

sealed class RepoHost(val hostname: String, val versionControlSystem: VersionControlSystem)
