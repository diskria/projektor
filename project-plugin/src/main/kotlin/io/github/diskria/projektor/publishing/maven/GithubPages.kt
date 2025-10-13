package io.github.diskria.projektor.publishing.maven

import io.github.diskria.projektor.projekt.common.IProjekt
import io.github.diskria.projektor.readme.shields.common.ReadmeShield
import io.github.diskria.projektor.readme.shields.dynamic.GithubPagesShield
import org.gradle.api.Project

data object GithubPages : LocalMaven() {

    override fun publish(projekt: IProjekt, project: Project) {

    }

    override fun getReadmeShield(projekt: IProjekt): ReadmeShield =
        GithubPagesShield(projekt)
}
