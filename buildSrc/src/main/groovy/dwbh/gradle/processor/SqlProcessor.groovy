package dwbh.gradle.processor

import groovy.sql.Sql
import groovy.transform.TupleConstructor
import org.gradle.api.logging.Logger

/**
 * Responsible for loading sql statements found in the
 * files located at the fixtures directory
 *
 * @since 0.1.0
 */
@TupleConstructor
class SqlProcessor {

    /**
     * Database configuration
     *
     * @since 0.1.0
     */
    Map<String,?> config

    /**
     * Sql files to load
     *
     * @since 0.1.0
     */
    File[] sqlFiles

    /**
     * Gradle logger to trace the process
     *
     * @since 0.1.0
     */
    Logger logger

    /**
     * Loads all fixtures found
     *
     * @since 0.1.0
     */
    void process() {
        logger.debug '> Fixtures: configuring sql access'
        Sql sql = Sql.newInstance(config.dataSource)

        logger.debug '> Fixtures: reading fixtures files'

        sqlFiles.each { File sqlFile ->
            logger.lifecycle "> Fixtures: applying: ${sqlFile.name}"

            String[] queries = sqlFile.readLines()

            logger.debug "> Fixtures: processing ${queries.size()} lines"

            sql.withBatch(queries.size()) { stmt ->
                queries.each { String query ->
                    logger.debug "> Fixtures: query: $query"
                    stmt.addBatch(query)
                }
            }
        }

        logger.lifecycle '-------------------------------------------------'
        logger.lifecycle "> Fixtures: applied: ${sqlFiles.size()} sql files"
    }
}
