plugins {
    id("org.sonarqube")
}

allprojects {
    tasks.withType {
        val task = this
        if (task.name == "check") {
            rootProject.tasks.sonarqube { dependsOn(task) }
        }
    }
}
