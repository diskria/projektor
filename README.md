# Projektor

Gradle plugin with reusable conventions and helpers for projects from my GitHub organizations.

[![GitHub Pages Maven](https://img.shields.io/github/v/release/diskria/projektor.svg?sort=semver&label=GitHub+Pages+Maven&style=for-the-badge)](https://diskria.github.io/projektor) [![License: MIT](https://img.shields.io/static/v1?message=MIT&color=yellow&label=License&style=for-the-badge)](https://spdx.org/licenses/MIT)

---

**Projektor** is a Gradle plugin for **unifying and centralizing project configuration**.  
Its main goal is to eliminate duplication and hide all the repetitive setup, so each project doesn’t need to redefine the same configuration again and again.

## Features

- 🔧 Unified configuration for multiple project types:
    - Minecraft mods
    - Libraries
    - Other Gradle plugins
    - Android applications

- 🚀 Unified publishing logic to:
    - GitHub Packages
    - Maven Central
    - Gradle Plugin Portal
    - Modrinth
    - Google Play

## Installation

Add to your `settings.gradle.kts` or `build.gradle.kts`:

```kotlin
plugins {
    id("io.github.diskria.projektor") version "2.+"
}
```

---

## License

This project is licensed under the [MIT License](https://spdx.org/licenses/MIT).
