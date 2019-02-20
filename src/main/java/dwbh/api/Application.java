package dwbh.api;

import io.micronaut.runtime.Micronaut;

/**
 * Application's entry point
 *
 * @since 0.1.0
 */
@SuppressWarnings("PMD.UseUtilityClass")
public class Application {

    /**
     * Executes the application
     *
     * @param args possible command line arguments
     * @since 0.1.0
     */
    public static void main(String[] args) {
        Micronaut.run(Application.class);
    }
}