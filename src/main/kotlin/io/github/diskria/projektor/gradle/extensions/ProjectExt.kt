package io.github.diskria.projektor.gradle.extensions

import com.github.gmazzo.buildconfig.BuildConfigExtension
import com.modrinth.minotaur.ModrinthExtension
import io.github.diskria.projektor.gradle.extensions.common.gradleError
import io.github.diskria.projektor.gradle.extensions.mappers.toInt
import io.github.diskria.projektor.gradle.extensions.mappers.toJvmTarget
import io.github.diskria.projektor.gradle.utils.VersionCatalogUtils
import io.github.diskria.projektor.licenses.License
import io.github.diskria.projektor.licenses.MitLicense
import io.github.diskria.projektor.minecraft.*
import io.github.diskria.projektor.minecraft.config.FabricModConfig
import io.github.diskria.projektor.minecraft.config.MixinsConfig
import io.github.diskria.projektor.minecraft.utils.ModrinthUtils
import io.github.diskria.projektor.minecraft.version.MinecraftVersion
import io.github.diskria.projektor.minecraft.version.getMinJavaVersion
import io.github.diskria.projektor.minecraft.version.getVersion
import io.github.diskria.projektor.owner.*
import io.github.diskria.projektor.owner.domain.AndroidDomain
import io.github.diskria.projektor.owner.domain.LibrariesDomain
import io.github.diskria.projektor.owner.domain.MinecraftDomain
import io.github.diskria.projektor.projekt.*
import io.github.diskria.utils.kotlin.BracketsType
import io.github.diskria.utils.kotlin.Constants
import io.github.diskria.utils.kotlin.extensions.*
import io.github.diskria.utils.kotlin.extensions.common.`Train-Case`
import io.github.diskria.utils.kotlin.extensions.common.className
import io.github.diskria.utils.kotlin.extensions.common.failWithUnsupportedType
import io.github.diskria.utils.kotlin.extensions.generics.joinBySpace
import io.github.diskria.utils.kotlin.extensions.generics.toNullIfEmpty
import io.github.diskria.utils.kotlin.poet.Property
import io.github.diskria.utils.kotlin.properties.toAutoNamedProperty
import io.github.diskria.utils.kotlin.words.*
import kotlinx.serialization.json.Json
import net.fabricmc.loom.api.LoomGradleExtensionAPI
import net.fabricmc.loom.api.fabricapi.FabricApiExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.file.Directory
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.file.RegularFile
import org.gradle.api.plugins.BasePluginExtension
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Provider
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.jvm.tasks.Jar
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JvmImplementation
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.gradle.kotlin.dsl.*
import org.gradle.language.jvm.tasks.ProcessResources
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension
import org.gradle.plugins.ide.idea.model.IdeaModel
import org.gradle.plugins.signing.SigningExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import kotlin.jvm.optionals.getOrNull
import kotlin.properties.ReadOnlyProperty
import kotlin.text.get

typealias BaseExt = BasePluginExtension
typealias JavaExt = JavaPluginExtension
typealias KotlinExt = KotlinProjectExtension
typealias SourceSetsExt = SourceSetContainer
typealias GradlePluginExt = GradlePluginDevelopmentExtension
typealias PublishingExt = PublishingExtension
typealias SigningExt = SigningExtension
typealias BuildConfigExt = BuildConfigExtension
typealias FabricExt = LoomGradleExtensionAPI
typealias FabricApiExt = FabricApiExtension
typealias ModrinthExt = ModrinthExtension

fun Project.getDirectory(path: String): Directory =
    layout.projectDirectory.dir(path)

fun Project.getFile(path: String): RegularFile =
    layout.projectDirectory.file(path)

fun Project.getBuildDirectory(path: String): Provider<Directory> =
    layout.buildDirectory.dir(path)

fun Project.getBuildFile(path: String): Provider<RegularFile> =
    layout.buildDirectory.file(path)

fun Project.requirePlugins(vararg ids: String) {
    val unknownPluginIds = ids.filterNot { id -> pluginManager.hasPlugin(id) }
    if (unknownPluginIds.isNotEmpty()) {
        gradleError("Plugins required but not applied: ${unknownPluginIds.joinBySpace()}")
    }
}

inline fun <reified T : Any> Project.getExtensionOrThrow(): T =
    extensions.findByType(T::class.java) ?: failWithUnsupportedType(T::class)

fun <R> Project.base(block: BaseExt.() -> R): R =
    getExtensionOrThrow<BaseExt>().block()

fun <R> Project.java(block: JavaExt.() -> R): R =
    getExtensionOrThrow<JavaExt>().block()

fun <R> Project.kotlin(block: KotlinExt.() -> R): R =
    getExtensionOrThrow<KotlinExt>().block()

fun <R> Project.sourceSets(block: SourceSetsExt.() -> R): R =
    getExtensionOrThrow<SourceSetsExt>().block()

fun <R> Project.gradlePlugin(block: GradlePluginExt.() -> R): R =
    getExtensionOrThrow<GradlePluginExt>().block()

fun <R> Project.publishing(block: PublishingExt.() -> R): R =
    getExtensionOrThrow<PublishingExt>().block()

fun <R> Project.signing(block: SigningExt.() -> R): R =
    getExtensionOrThrow<SigningExt>().block()

fun <R> Project.buildConfig(block: BuildConfigExt.() -> R): R =
    getExtensionOrThrow<BuildConfigExt>().block()

fun <R> Project.fabricApi(block: FabricApiExt.() -> R): R =
    getExtensionOrThrow<FabricApiExt>().block()

fun <R> Project.fabric(block: FabricExt.() -> R): R =
    getExtensionOrThrow<FabricExt>().block()

fun <R> Project.modrinth(block: ModrinthExt.() -> R): R =
    getExtensionOrThrow<ModrinthExt>().block()

fun DependencyHandler.implementation(dependencyNotation: Any): Dependency? =
    add("implementation", dependencyNotation)

fun DependencyHandler.testImplementation(dependencyNotation: Any): Dependency? =
    add("testImplementation", dependencyNotation)

fun DependencyHandler.minecraft(dependencyNotation: Any): Dependency? =
    add("minecraft", dependencyNotation)

fun DependencyHandler.mappings(dependencyNotation: Any): Dependency? =
    add("mappings", dependencyNotation)

fun DependencyHandler.modImplementation(dependencyNotation: Any): Dependency? =
    add("modImplementation", dependencyNotation)

fun Project.versionCatalogs(): VersionCatalogsExtension =
    extensions.findByType(VersionCatalogsExtension::class.java)
        ?: gradleError("Gradle version catalogs not supported")

fun Project.getCatalogVersion(alias: String, catalog: String = VersionCatalogUtils.DEFAULT_CATALOG_NAME): String? =
    versionCatalogs().named(catalog).findVersion(alias).getOrNull()?.requiredVersion

fun Project.getCatalogVersionOrThrow(
    alias: String,
    catalog: String = VersionCatalogUtils.DEFAULT_CATALOG_NAME,
): String =
    getCatalogVersion(alias, catalog) ?: gradleError(
        "Missing alias ${alias.wrap(Constants.Char.SINGLE_QUOTE)} in " +
                "${VersionCatalogUtils.CATALOG_VERSIONS.wrapWithBrackets(BracketsType.SQUARE)} of " +
                VersionCatalogUtils.buildCatalogFileName(catalog)
    )

fun Project.resolveCatalogVersion(
    aliasShort: String,
    catalog: String = VersionCatalogUtils.DEFAULT_CATALOG_NAME,
    formatShort: (String) -> String,
): String =
    getCatalogVersion("$aliasShort-full", catalog) ?: formatShort(getCatalogVersionOrThrow(aliasShort, catalog))

fun Project.getProjectFileNamesFrom(path: String): List<String> =
    getDirectory(path).asFile.listFiles { it.isFile && !it.isHidden }?.map { it.nameWithoutExtension }.orEmpty()

fun Project.configureBuildConfig(packageName: String, className: String, fields: () -> List<Property<String>>) {
    buildConfig {
        packageName(packageName)
        className(className)
        fields().forEach { field ->
            buildConfigField(field.name, field.value)
        }
        useKotlinOutput {
            internalVisibility = false
            topLevelConstants = false
        }
    }
}

fun Project.gradlePropertyDelegate(): ReadOnlyProperty<Any?, String> =
    ReadOnlyProperty { _, property ->
        providers.gradleProperty(property.name.setCase(CamelCase, DotCase)).get()
    }

fun Project.projekt(owner: GithubOwner, license: License, jvmTarget: JvmTarget? = null): Projekt {
    val javaVersion = getCatalogVersionOrThrow("java").toInt()
    val kotlinVersion = getCatalogVersionOrThrow("kotlin")

    val projectName: String by gradlePropertyDelegate()
    val projectDescription: String by gradlePropertyDelegate()
    val projectVersion: String by gradlePropertyDelegate()
    return Projekt(
        owner = owner,
        license = license,
        name = projectName,
        description = projectDescription,
        version = projectVersion,
        slug = projectName.setCase(SpaceCase, KebabCase).lowercase(),
        packageName = owner.namespace + Constants.Char.DOT + projectName.setCase(SpaceCase, DotCase),
        javaVersion = javaVersion,
        jvmTarget = jvmTarget ?: javaVersion.toJvmTarget(),
        kotlinVersion = kotlinVersion,
    )
}

fun Project.configureProjekt(
    projekt: IProjekt,
    baseArtifactName: String = projekt.slug,
    artifactVersion: String = projekt.version,
) {
    requirePlugins("kotlin")
    group = projekt.owner.namespace
    version = projekt.version
    base {
        archivesName = baseArtifactName
    }
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(projekt.javaVersion))
            vendor.set(JvmVendorSpec.ADOPTIUM)
            implementation.set(JvmImplementation.VENDOR_SPECIFIC)
        }
        withSourcesJar()
        withJavadocJar()
    }
    kotlin {
        jvmToolchain(projekt.javaVersion)
    }
    tasks.withType<JavaCompile>().configureEach {
        with(options) {
            release.set(projekt.jvmTarget.toInt())
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
                oldName + Constants.Char.UNDERSCORE + projekt.slug
            }
        }
        archiveVersion.set(artifactVersion)
    }
    sourceSets {
        named("main") {
            val generatedDirectory = "src/main/generated"

            resources.srcDirs(generatedDirectory)
            java.srcDirs("$generatedDirectory/java")
        }
    }
}

fun Project.configureGradlePlugin(
    owner: GithubOwner,
    publishingTarget: PublishingTarget?,
    isSettingsPlugin: Boolean = false,
    tags: Set<String> = emptySet(),
    license: License = MitLicense,
): GradlePlugin {
    requirePlugins("maven-publish")
    val plugin = projekt(owner, license).toGradlePlugin(isSettingsPlugin)
    gradlePlugin {
        website.set(owner.getRepositoryUrl(plugin.slug))
        vcsUrl.set(owner.getRepositoryUrl(plugin.slug, isVcsUrl = true))

        plugins {
            create(plugin.id) {
                id = plugin.id
                implementationClass = plugin.packageName + Constants.Char.DOT + plugin.className

                displayName = plugin.name
                description = plugin.description

                if (tags.isNotEmpty()) {
                    this.tags.set(tags)
                }
            }
        }
    }
    configureProjekt(plugin)
    configurePublishing(plugin, publishingTarget)
    return plugin
}

fun Project.configureLibrary(license: License = MitLicense): IProjekt {
    val library = projekt(LibrariesDomain, license, JvmTarget.JVM_1_8).toLibrary()
    dependencies {
        testImplementation(kotlin("test"))
        testImplementation("org.junit.jupiter:junit-jupiter:5.13.4")
    }
    tasks.named<Test>("test") {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
            exceptionFormat = TestExceptionFormat.FULL
            showStandardStreams = true
        }
    }
    configureProjekt(library)
    configurePublishing(library, PublishingTarget.MAVEN_CENTRAL)
    return library
}

fun Project.configureMinecraftMod(
    environment: ModEnvironment,
    isFabricApiRequired: Boolean,
    modrinthProjectId: String,
    license: License = MitLicense,
): IProjekt {
    requirePlugins(
        "org.jetbrains.kotlin.plugin.serialization",
        "com.github.gmazzo.buildconfig",
    )
    val minecraftVersion = MinecraftVersion.of(projectDir.name)
    val jvmTarget = minecraftVersion.getMinJavaVersion().toJvmTarget()
    val mod = projekt(MinecraftDomain, license, jvmTarget).toMinecraftMod(
        modLoader = projectDir.parentFile.name.toEnum<ModLoader>(),
        minecraftVersion = minecraftVersion,
        environment = environment,
        modrinthProjectUrl = ModrinthUtils.getProjectUrl(modrinthProjectId),
    )
    val artifactVersion = buildString {
        append(mod.modLoader.logicalName)
        append(Constants.Char.HYPHEN)
        append(mod.version)
        append(Constants.Char.PLUS)
        append(MinecraftDomain.suffix)
        append(minecraftVersion)
    }
    configureBuildConfig(mod.packageName, "MinecraftMod") {
        val modId by mod.id.toAutoNamedProperty(ScreamingSnakeCase)
        val modName by mod.name.toAutoNamedProperty(ScreamingSnakeCase)
        listOf(modId, modName)
    }
    if (mod.modLoader == ModLoader.FABRIC) {
        configureFabricModLoader(mod, isFabricApiRequired)
    }
    tasks.named<Jar>("jar") {
        manifest {
            val specificationVersion by 1.toString().toAutoNamedProperty(`Train-Case`)
            val specificationTitle by mod.id.toAutoNamedProperty(`Train-Case`)
            val specificationVendor by GithubProfile.username.toAutoNamedProperty(`Train-Case`)

            val implementationVersion by artifactVersion.toAutoNamedProperty(`Train-Case`)
            val implementationTitle by mod.name.toAutoNamedProperty(`Train-Case`)
            val implementationVendor by GithubProfile.username.toAutoNamedProperty(`Train-Case`)

            val mixinsConfig by mod.mixinsConfigFileName.toAutoNamedProperty(PascalCase)

            attributes(
                listOf(
                    specificationVersion,
                    specificationTitle,
                    specificationVendor,

                    implementationVersion,
                    implementationTitle,
                    implementationVendor,

                    mixinsConfig,
                ).associate { it.name to it.value }
            )
        }
    }
    configureProjekt(mod, artifactVersion = artifactVersion)
    configurePublishing(mod, PublishingTarget.MODRINTH)
    return mod
}

fun Project.configureFabricModLoader(mod: MinecraftMod, isFabricApiRequired: Boolean) {
    requirePlugins("fabric-loom")
    val loaderVersion = getCatalogVersionOrThrow("fabric-loader")
    val mixins = mod.environment
        .getSourceSets()
        .mapNotNull { sourceSet ->
            val pathBase = "src/${sourceSet.logicalName}/java/${mod.packagePath}/mixins"
            getProjectFileNamesFrom(
                if (sourceSet == SourceSet.MAIN) pathBase
                else "$pathBase/${sourceSet.logicalName}"
            ).toNullIfEmpty()?.let { sourceSet to it }
        }
        .toMap()
    val datagenClasses = getProjectFileNamesFrom("src/datagen/kotlin/${mod.packagePath}").map {
        mod.packageName + Constants.Char.DOT + it
    }
    val minecraftVersion = mod.minecraftVersion.getVersion()
    dependencies {
        minecraft("com.mojang:minecraft:$minecraftVersion")
        modImplementation("net.fabricmc:fabric-loader:$loaderVersion")

        val yarnVersion = resolveCatalogVersion("fabric-yarn") { "$minecraftVersion+build.$it" }
        mappings("net.fabricmc:yarn:$yarnVersion:v2")

        val kotlinModVersion = resolveCatalogVersion("fabric-kotlin") { "$it+kotlin.${mod.kotlinVersion}" }
        modImplementation("net.fabricmc:fabric-language-kotlin:$kotlinModVersion")

        if (isFabricApiRequired) {
            val apiVersion = resolveCatalogVersion("fabric-api") { "$it+$minecraftVersion" }
            modImplementation("net.fabricmc.fabric-api:fabric-api:$apiVersion")
        }
    }
    fabric {
        splitEnvironmentSourceSets()
        mods {
            create(mod.id) {
                sourceSets {
                    mod.environment.getSourceSets().forEach { sourceSet ->
                        sourceSet(getByName(sourceSet.logicalName))
                    }
                }
            }
        }
        runs {
            ModSide.entries.forEach { side ->
                named(side.logicalName) {
                    val hasSide = mod.environment.sides.contains(side)
                    ideConfigGenerated(hasSide)

                    if (hasSide) {
                        name = side.logicalName.capitalizeFirstChar()
                        runDir = "run/${side.logicalName}"
                        when (side) {
                            ModSide.CLIENT -> client()
                            ModSide.SERVER -> server()
                        }
                        programArgs("--username", "${GithubProfile.username}-${side.logicalName}")
                        vmArgs("-Xms2G", side.getMaxMemoryJvmArgument())
                    }
                }
            }
        }
        accessWidenerPath.set(file("src/main/resources/${mod.id}.accesswidener"))
    }
    if (datagenClasses.isNotEmpty()) {
        fabric {
            runs {
                create("data") {
                    name = "Datagen"
                    runDir = "datagen"
                    environment("server")
                    vmArgs(
                        "-Dfabric-api.datagen",
                        "-Dfabric-api.datagen.output-dir=${file("src/main/generated")}",
                        "-Dfabric-api.datagen.modid=${mod.id}",
                        "-Xms2G",
                        ModSide.SERVER.getMaxMemoryJvmArgument(),
                    )
                }
            }
        }
        fabricApi {
            configureDataGeneration {
                client = true
            }
        }
    }
    val generateFabricConfigTask by tasks.registering {
        val modConfigFile = getBuildFile("generated/resources/${ModLoader.FABRIC.getConfigFilePath()}")
        val mixinsConfigFile = getBuildFile("generated/resources/${mod.mixinsConfigFileName}")
        outputs.files(modConfigFile, mixinsConfigFile)
        doLast {
            modConfigFile.get().asFile.apply {
                parentFile.mkdirs()
                writeText(
                    Json { prettyPrint = true }.encodeToString(
                        FabricModConfig.of(
                            mod = mod,
                            minecraftVersion = mod.minecraftVersion,
                            loaderVersion = loaderVersion,
                            isApiRequired = isFabricApiRequired,
                            datagenClasses = datagenClasses,
                        )
                    )
                )
            }
            mixinsConfigFile.get().asFile.apply {
                parentFile.mkdirs()
                writeText(
                    Json { prettyPrint = true }.encodeToString(
                        MixinsConfig.of(
                            mod = mod,
                            mixins = mixins,
                        )
                    )
                )
            }
        }
    }
    tasks.named<ProcessResources>("processResources") {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        from(generateFabricConfigTask)
    }
}

fun Project.configureAndroidApp(license: License = MitLicense): IProjekt {
    val app = projekt(AndroidDomain, license).toAndroidApp()
    configureProjekt(app)
    configurePublishing(app, PublishingTarget.GOOGLE_PLAY)
    return app
}

fun Project.configurePublishing(projekt: IProjekt, target: PublishingTarget?) {
    if (target == null) {
        println("Publishing target is null, skip.")
        return
    }
    when (target) {
        PublishingTarget.GITHUB_PACKAGES -> configureGithubPackagesPublishing(projekt)
        PublishingTarget.MAVEN_CENTRAL -> configureMavenCentralPublishing(projekt)
        PublishingTarget.MODRINTH -> configureModrinthPublishing(projekt)
        PublishingTarget.GRADLE_PLUGIN_PORTAL -> {}
        PublishingTarget.GOOGLE_PLAY -> {}
    }
}

private fun Project.configureGithubPackagesPublishing(project: IProjekt) {
    val githubOwner = project.owner as? GithubOwner ?: gradleError(
        "Attempted to configure publishing to GitHub Packages, " +
                "but the owner type is ${project.owner::class.className()}"
    )
    val githubPackagesToken = Secrets.githubPackagesToken
    if (githubPackagesToken == null) {
        println("Skipping Github Packages publishing configuration: token is missing")
        return
    }
    publishing {
        publications.withType<MavenPublication> {
            artifactId = project.slug
        }
        repositories {
            maven(githubOwner.getPackagesMavenUrl(project.slug)) {
                credentials {
                    username = GithubProfile.username
                    password = githubPackagesToken
                }
            }
        }
    }
}

private fun Project.configureMavenCentralPublishing(project: IProjekt) {
    val gpgKey = Secrets.gpgKey
    val gpgPassphrase = Secrets.gpgPassphrase
    if (gpgKey == null || gpgPassphrase == null) {
        println("Skipping Maven Central publishing configuration: GPG keys are missing")
        return
    }
    publishing {
        repositories {
            maven(getBuildDirectory("staging-repo").get().asFile)
        }
    }
    val publication = publishing {
        publications.create<MavenPublication>(project.slug) {
            artifactId = project.slug
            from(components["java"])
            pom {
                name.set(project.name)
                description.set(project.description)
                url.set(project.owner.getRepositoryUrl(project.slug))
                licenses {
                    license {
                        project.license.let {
                            name.set(it.displayName)
                            url.set(it.getUrl())
                        }
                    }
                }
                developers {
                    developer {
                        project.owner.name.let {
                            id.set(it)
                            name.set(it)
                        }
                        email.set(project.owner.email)
                    }
                }
                scm {
                    url.set(project.owner.getRepositoryUrl(project.slug))
                    connection.set(
                        project.scm.buildUri(project.owner.getRepositoryUrl(project.slug, isVcsUrl = true))
                    )
                    developerConnection.set(
                        project.scm.buildUri(
                            project.softwareForge.getSshAuthority(),
                            project.owner.getRepositoryPath(project.slug, isVcsUrl = true)
                        )
                    )
                }
            }
        }
    }
    signing {
        useInMemoryPgpKeys(gpgKey, gpgPassphrase)
        sign(publication)
    }
}

fun Project.configureModrinthPublishing(project: IProjekt) {
    requirePlugins("com.modrinth.minotaur")
    modrinth {
        projectId.set(project.slug)
    }
}
