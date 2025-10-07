package io.github.diskria.projektor.projekt.common

import com.github.gmazzo.buildconfig.BuildConfigExtension
import io.github.diskria.gradle.utils.extensions.getBuildDirectory
import io.github.diskria.gradle.utils.extensions.requirePlugins
import io.github.diskria.gradle.utils.extensions.runExtension
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.*
import io.github.diskria.kotlin.utils.extensions.common.`dot․case`
import io.github.diskria.kotlin.utils.extensions.common.`kebab-case`
import io.github.diskria.kotlin.utils.extensions.common.modifyIf
import io.github.diskria.kotlin.utils.extensions.common.`path∕case`
import io.github.diskria.kotlin.utils.poet.Property
import io.github.diskria.kotlin.utils.words.PascalCase
import io.github.diskria.projektor.extensions.mappers.toInt
import io.github.diskria.projektor.extensions.mappers.toJvmTarget
import io.github.diskria.projektor.licenses.License
import io.github.diskria.projektor.publishing.PublishingTarget
import io.github.diskria.projektor.repo.host.GitHub
import io.github.diskria.projektor.repo.host.RepoHost
import io.ktor.http.*
import org.gradle.api.Project
import org.gradle.api.plugins.BasePluginExtension
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.tasks.Jar
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JvmImplementation
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

interface IProjekt {

    val owner: String
    val developer: String
    val email: String
    val repo: String
    val name: String
    val description: String
    val tags: Set<String>
    val version: String
    val license: License
    val publishingTarget: PublishingTarget?
    val javaVersion: Int
    val kotlinVersion: String

    val namespace: String
        get() = "io.github".appendPackageName(developer)

    val packageName: String
        get() = namespace.appendPackageName(repo.setCase(`kebab-case`, `dot․case`))

    val packagePath: String
        get() = packageName.setCase(`dot․case`, `path∕case`)

    val classNameBase: String
        get() = repo.setCase(`kebab-case`, PascalCase)

    val jvmTarget: JvmTarget
        get() = javaVersion.toJvmTarget()

    val jarVersion: String
        get() = version

    val repoHost: RepoHost
        get() = GitHub

    val githubPackagesUrl: String
        get() = buildGithubUrl(isPackages = true).toString()

    val githubIssuesUrl: String
        get() = buildGithubUrl { path("issues") }.toString()

    val configure: Project.() -> Unit
        get() = {}

    private val applyCommonConfiguration: Project.() -> Unit
        get() = {
            val projekt = this@IProjekt
            requirePlugins("kotlin")
            group = namespace
            version = jarVersion
            runExtension<BasePluginExtension> {
                archivesName.assign(repo)
            }
            runExtension<JavaPluginExtension> {
                toolchain {
                    languageVersion.set(JavaLanguageVersion.of(javaVersion))
                    vendor.set(JvmVendorSpec.ADOPTIUM)
                    implementation.set(JvmImplementation.VENDOR_SPECIFIC)
                }
                withSourcesJar()
                withJavadocJar()
            }
            runExtension<KotlinProjectExtension> {
                jvmToolchain(javaVersion)
            }
            tasks.withType<JavaCompile>().configureEach {
                with(options) {
                    release.set(jvmTarget.toInt())
                    encoding = Charsets.UTF_8.toString()
                }
            }
            tasks.withType<KotlinCompile>().configureEach {
                compilerOptions {
                    jvmTarget.set(projekt.jvmTarget)
                }
            }
            tasks.named<Jar>("jar") {
                from("LICENSE") {
                    rename { oldName ->
                        oldName + Constants.Char.UNDERSCORE + repo
                    }
                }
                archiveVersion.set(jarVersion)
            }
            val unpackJarTask = tasks.register<Sync>("unpackJar") {
                val jarTask = tasks.named<Jar>("jar")
                from(zipTree(jarTask.flatMap { it.archiveFile }))
                into(getBuildDirectory("unpacked"))
                dependsOn(jarTask)
            }
            tasks.named("build") {
                finalizedBy(unpackJarTask)
            }
            runExtension<SourceSetContainer> {
                named("main") {
                    val generatedDirectory = "src/main/generated"
                    resources.srcDirs(generatedDirectory)
                    java.srcDirs("$generatedDirectory/java")
                }
            }
            val metadata = getMetadata()
            if (metadata.isNotEmpty()) {
                requirePlugins("com.github.gmazzo.buildconfig")
                runExtension<BuildConfigExtension> {
                    packageName(projekt.packageName)
                    className("ProjektMetadata")
                    metadata.forEach { field ->
                        buildConfigField(field.name, field.value)
                    }
                    useKotlinOutput {
                        internalVisibility = false
                        topLevelConstants = false
                    }
                }
            }
            publishingTarget?.configure(this, projekt)
        }

    fun configure(project: Project) {
        with(project) {
            applyCommonConfiguration()
            configure()
        }
    }

    fun getRepoUrl(isVcs: Boolean = false): String =
        buildGithubUrl(isVcs).toString()

    fun getRepoPath(isVcs: Boolean = false): String =
        buildGithubUrl(isVcs = isVcs).encodedPath.removePrefix(Constants.Char.SLASH)

    fun getMetadata(): List<Property<String>> = emptyList()

    private fun buildGithubUrl(
        isVcs: Boolean = false,
        isPackages: Boolean = false,
        block: URLBuilder.() -> Unit = {}
    ): Url =
        URLBuilder().apply {
            protocol = URLProtocol.HTTPS
            host = repoHost.hostname.modifyIf(isPackages) { it.appendPrefix("maven.pkg.") }
            path(owner, repo.modifyIf(isVcs) { it.appendSuffix(".${repoHost.versionControlSystem.name}") })
            block()
        }.build()
}
