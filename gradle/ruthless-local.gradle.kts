beforeSettings {
    pluginManagement {
        resolutionStrategy {
            eachPlugin {
                if (requested.id.id.startsWith("dev.lajoscseppento.ruthless")) {
                    useVersion("+")
                }
            }
        }

        repositories {
            mavenLocal()
            gradlePluginPortal()
        }
    }
}
