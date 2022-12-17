# Fancydoc

[![Release](https://img.shields.io/github/v/release/LajosCseppento/fancydoc)](https://github.com/LajosCseppento/fancydoc/releases)
[![Plugin Portal](https://img.shields.io/maven-metadata/v?label=Plugin%20Portal&metadataUrl=https://plugins.gradle.org/m2/dev/lajoscseppento/fancydoc/fancydoc-plugin/maven-metadata.xml)](https://plugins.gradle.org/plugin/dev.lajoscseppento.fancydoc)
[![Maven Central](https://img.shields.io/maven-central/v/dev.lajoscseppento.fancydoc/fancydoc-plugin)](https://search.maven.org/search?q=g:%22dev.lajoscseppento.fancydoc%22%20AND%20a:%22dev.lajoscseppento.fancydoc.gradle.plugin%22)
[![CI](https://github.com/LajosCseppento/fancydoc/workflows/CI/badge.svg)](https://github.com/LajosCseppento/fancydoc/actions)
[![License](https://img.shields.io/github/license/LajosCseppento/fancydoc)](https://github.com/LajosCseppento/fancydoc/blob/main/LICENSE)

Fancy Javadoc for Gradle projects.

Features:

* set UTF-8 encoding on all javadoc tasks
* enable `-linksource` on all javadoc tasks (this makes the generated javadoc to contain the complete source code)
* highlights generated Javadoc source code using [Prism](https://prismjs.com)

## Development Guide

### Release Procedure

1. Release commit: fix version, finalise change log (don't forget about the links in the bottom of
   the change log)
2. Check CI for success
3. Tag release on GitHub (draft)
4. Publish to Maven Central
    1. Run `./gradlew publishAllPublicationsToStagingRepository`
    2. Open https://oss.sonatype.org/#stagingRepositories
    3. Close staging repository
    4. Inspect contents
    5. Release
5. Publish to Gradle Plugin Portal using `./gradlew publishPlugins`
6. Publish release on GitHub
7. Bump version
