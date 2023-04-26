plugins {
    id("org.sonarqube") version "4.0.0.2929"
}

allprojects {
    tasks.withType {
        val task = this
        if (task.name == "check") {
            rootProject.tasks.sonarqube { dependsOn(task) }
        }
    }
}
