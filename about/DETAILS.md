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
