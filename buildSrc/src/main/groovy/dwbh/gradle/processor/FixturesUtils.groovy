package dwbh.gradle.processor

import org.yaml.snakeyaml.Yaml

/**
 * All auxiliary functions
 *
 * @since 0.1.0
 */
class FixturesUtils {

    /**
     * Filters files with name ending with sql
     *
     * @since 0.1.0
     */
    static FilenameFilter onlySqlFiles = { File dir, String name ->
        return name ==~ /.*sql/
    } as FilenameFilter

    /**
     * Loads a given yaml file and returns a {@link Map}
     *
     * @param sqlConfig file containing the yaml
     * @return a map with all the yaml content
     * @since 0.2.0
     */
    static Map<String,?> loadYaml(File sqlConfig) {
        Yaml yaml = new Yaml()

        return yaml.load(new FileReader(sqlConfig))
    }
}
