rootProject.name = "fancydoc-demo"

if (System.getProperty("useMavenLocal") != null) {
    pluginManagement {
        repositories {
            mavenLocal()
            gradlePluginPortal()
        }
    }
}