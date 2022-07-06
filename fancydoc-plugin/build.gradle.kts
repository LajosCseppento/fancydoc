plugins {
    id("com.gradle.plugin-publish") version "1.0.0-rc-2"
    id("dev.lajoscseppento.ruthless.java-gradle-plugin")
    signing
}

ruthless.lombok()

dependencies {
    implementation("dev.lajoscseppento.gradle:gradle-plugin-common:0.1.2")
    implementation("org.jsoup:jsoup:1.15.1")
    testImplementation("org.mockito:mockito-core:4.6.1")
    functionalTestImplementation("commons-io:commons-io:2.11.0")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }

    withJavadocJar()
    withSourcesJar()
}

gradlePlugin {
    plugins {
        create("fancydoc") {
            id = "dev.lajoscseppento.fancydoc"
            implementationClass = "dev.lajoscseppento.fancydoc.plugin.FancydocPlugin"
            displayName = "Fancydoc"
            description = "Fancydoc plugin"
        }
    }
}

val TAGS = listOf("fancydoc", "javadoc", "documentation", "docs")
val DESCRIPTION = "Fancy Javadoc for Gradle projects"
val VCS_URL = "https://github.com/LajosCseppento/fancydoc.git"
val WEBSITE = "https://github.com/LajosCseppento/fancydoc"

val POM_SCM_CONNECTION = "scm:git:git://github.com/LajosCseppento/fancydoc.git"
val POM_SCM_DEVELOPER_CONNECTION = "scm:git:ssh://git@github.com/LajosCseppento/fancydoc.git"
val POM_SCM_URL = "https://github.com/LajosCseppento/fancydoc"

pluginBundle {
    description = DESCRIPTION
    tags = TAGS
    vcsUrl = VCS_URL
    website = WEBSITE
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
            name.set("Fancydoc")
            description.set(DESCRIPTION)
        }

        url.set(WEBSITE)

        licenses {
            license {
                name.set("Apache License, Version 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0")
            }
        }

        developers {
            developer {
                id.set("LajosCseppento")
                name.set("Lajos Cseppentő")
                this.url.set("https://www.lajoscseppento.dev")
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
        val orig = properties["sonar.tests"] as MutableList<Any>
        properties["sonar.tests"] = orig + sourceSets.functionalTest.get().allSource.srcDirs.filter { it.exists() }
    }
}
