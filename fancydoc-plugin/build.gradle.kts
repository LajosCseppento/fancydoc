plugins {
    id("com.gradle.plugin-publish")
    id("dev.lajoscseppento.ruthless.java-gradle-plugin")
    signing
}

dependencies {
    implementation("dev.lajoscseppento.gradle:gradle-plugin-common:0.5.0")
    implementation("org.jsoup:jsoup:1.16.1")
    testImplementation("org.mockito:mockito-core")
    functionalTestImplementation("commons-io:commons-io:2.14.0")
}

val TAGS = listOf("fancydoc", "javadoc", "documentation", "docs")
val DESCRIPTION = "Fancy Javadoc for Gradle projects"
val VCS_URL = "https://github.com/LajosCseppento/fancydoc.git"
val WEBSITE = "https://github.com/LajosCseppento/fancydoc"

val PLUGIN_MAVEN_PUBLICATION_NAME = "Fancydoc"

val LICENSE_NAME = "Apache License, Version 2.0"
val LICENSE_URL = "https://www.apache.org/licenses/LICENSE-2.0"

val DEVELOPER_ID = "LajosCseppento"
val DEVELOPER_NAME = "Lajos Cseppentő"
val DEVELOPER_URL = "https://www.lajoscseppento.dev"

val POM_SCM_CONNECTION = "scm:git:git://github.com/LajosCseppento/fancydoc.git"
val POM_SCM_DEVELOPER_CONNECTION = "scm:git:ssh://git@github.com/LajosCseppento/fancydoc.git"
val POM_SCM_URL = "https://github.com/LajosCseppento/fancydoc"

gradlePlugin {
    website.set(WEBSITE)
    vcsUrl.set(VCS_URL)

    plugins {
        create("fancydoc") {
            id = "dev.lajoscseppento.fancydoc"
            implementationClass = "dev.lajoscseppento.fancydoc.plugin.FancydocPlugin"
            displayName = "Fancydoc"
            description = DESCRIPTION
            tags.set(TAGS)
        }
    }
}

if (hasProperty("ossrhUsername")) {
    publishing {
        repositories {
            val ossrhUsername: String by project
            val ossrhPassword: String by project

            maven {
                name = "snapshots"
                url = uri("https://oss.sonatype.org/content/repositories/snapshots")
                credentials {
                    username = ossrhUsername
                    password = ossrhPassword
                }
            }

            maven {
                name = "staging"
                url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2")
                credentials {
                    username = ossrhUsername
                    password = ossrhPassword
                }
            }
        }
    }
} else {
    logger.warn("Configure project without OSSRH publishing")
}

publishing.publications.withType<MavenPublication> {
    val publicationName = name

    pom {
        if (publicationName == "pluginMaven") {
            name.set(PLUGIN_MAVEN_PUBLICATION_NAME)
            description.set(DESCRIPTION)
        }

        url.set(WEBSITE)

        licenses {
            license {
                name.set(LICENSE_NAME)
                url.set(LICENSE_URL)
            }
        }

        developers {
            developer {
                id.set(DEVELOPER_ID)
                name.set(DEVELOPER_NAME)
                url.set(DEVELOPER_URL)
            }
        }

        scm {
            connection.set(POM_SCM_CONNECTION)
            developerConnection.set(POM_SCM_DEVELOPER_CONNECTION)
            url.set(POM_SCM_URL)
        }
    }
}

sonarqube {
    properties {
        @Suppress("UNCHECKED_CAST")
        val orig = properties["sonar.tests"] as Collection<Any>
        properties["sonar.tests"] = orig + sourceSets.functionalTest.get().allSource.srcDirs.filter { it.exists() }
    }
}
