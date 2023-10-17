plugins {
    id("org.sonarqube") version "4.4.1.3373"
}

allprojects {
    tasks.withType {
        val task = this
        if (task.name == "check") {
            rootProject.tasks.sonarqube { dependsOn(task) }
        }
    }
}
