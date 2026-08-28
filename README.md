# Projektor

An opinionated Gradle convention plugin for Diskria projects. Automates setup and multi-target publishing for Kotlin libraries and Gradle plugins, with built-in mono-repo support.

[![Gradle Plugin Portal](https://img.shields.io/gradle-plugin-portal/v/io.github.diskria.projektor.svg?label=Gradle+Plugin+Portal&style=for-the-badge)](https://plugins.gradle.org/plugin/io.github.diskria.projektor) [![License: MIT](https://img.shields.io/static/v1?label=License&message=MIT&color=yellow&style=for-the-badge)](https://spdx.org/licenses/MIT)

---

### Key Capabilities

- Settings-Driven Declaration: Define project types (kotlinLibrary, gradlePlugin, or monorepo) and common metadata (version, license) directly in settings.gradle.kts.
- Automated Repositories: Auto-configures dependency resolution repositories (Maven Central mirrors, Gradle Plugin Portal) based on declared project types.
- Unified Build DSL: Applies zero-boilerplate configurations to subprojects via `alias(convention.plugins.projektor)`.
- Multi-Target Distribution: Configures publication pipelines for Maven Central, Gradle Plugin Portal, GitHub Packages, and GitHub Pages from a single DSL block.
- Validation & Safety: Enforces strict configuration checks (missing plugins, misconfigured subprojects, duplicate monorepo paths).

### Usage Example

`settings.gradle.kts`

```kotlin
plugins {
    id("io.github.diskria.projektor") version "<version>"
}

projekt {
    version = "0.1.0"
    licensing { mit() }
    
    // Single-module declaration (or use monorepo { ... } for multi-project setups)
    kotlinLibrary()
}
```

`build.gradle.kts`

```kotlin
plugins {
    alias(convention.plugins.projektor)
}

projekt {
    kotlinLibrary()
    
    distribute {
        mavenCentral()
    }
}
```

---

## License

This project is licensed under the [MIT License](https://spdx.org/licenses/MIT).
