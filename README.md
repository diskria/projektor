# Projektor

[![GitHub Packages](https://img.shields.io/github/v/release/diskria/projektor?sort=semver&label=GitHub%20Packages)](https://github.com/diskria/projektor/packages)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

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
    id("io.github.diskria.projektor") version "<latest_version>"
}
```

---

## License

Projektor is distributed under the [MIT License](LICENSE).
