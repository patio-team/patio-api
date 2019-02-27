package dwbh.gradle

import dwbh.gradle.processor.FixturesUtils
import dwbh.gradle.processor.SqlProcessor
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction

/**
 * Task responsible for loading configured fixtures
 *
 * @since 0.1.0
 */
class FixturesTask extends DefaultTask {

    /**
     * Group the task belongs to
     *
     * @since 0.1.0
     */
    String group = 'dwbh'

    /**
     * Where to put the fixtures SQL files
     *
     * @since 0.1.0
     */
    @InputDirectory
    File inputDir

    /**
     * Database configuration file
     *
     * @since 0.1.0
     */
    @InputFile
    File configFile

    /**
     * Indicates if it is a clean operation
     *
     * @since 0.1.0
     */
    boolean isClean = false


    /**
     * Task entry point
     *
     * @since 0.1.0
     */
    @TaskAction
    void createFixtures() {
        logger.lifecycle "------------------${this.name.toUpperCase()}-----------------"

        File[] sqlFiles = inputDir
            .listFiles(FixturesUtils.onlySqlFiles)
            .sort()

        if (isClean) {
            sqlFiles = sqlFiles.reverse()
        }

        Map<String, ?> dbConfig = FixturesUtils.loadYaml(configFile)
        SqlProcessor processor = new SqlProcessor(dbConfig, sqlFiles, logger)

        processor.process()
    }
}