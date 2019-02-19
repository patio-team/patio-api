package dwbh.gradle

/**
 * Extension available in project's build.gradle DSL to
 * configure the directory where the fixtures must be
 * located
 *
 * @since 0.1.0
 */
class FixturesExtension {

    /**
     * Directory where to find the sql files to load fixtures
     *
     * @since 0.1.0
     */
    String loadDir

    /**
     * Directory where to find the sql files to clean fixtures
     *
     * @since 0.1.0
     */
    String cleanDir

    /**
     * File to get the database connection from
     *
     * @since 0.1.0
     */
    String configFile
}
