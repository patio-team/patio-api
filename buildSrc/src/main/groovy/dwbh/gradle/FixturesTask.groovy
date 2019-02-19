package dwbh.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class FixturesTask extends DefaultTask {
    @TaskAction
    def createFixtures() {
        print("Taaaaaask")

    }
}