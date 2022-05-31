plugins {
    id("org.sonarqube") version "3.3"
}

allprojects {
    tasks.withType {
        val task = this
        if (task.name == "check") {
            rootProject.tasks.sonarqube { dependsOn(task) }
        }
    }
}
