package dwbh.api.graphql;

import dwbh.api.domain.Group;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

/**
 * Creates sample instances for tests in this package
 *
 * @since 0.1.0
 */
final class FixturesHelper {

    private FixturesHelper() {
        /* empty constructor */
    }

    /**
     * Creates a {@link Group} sample
     *
     * @return an instance of {@link Group}
     * @since 0.1.0
     */
    public static Group createGroup() {
        return new Group("john", UUID.randomUUID(), true, true);
    }

    /**
     * Creates a list of {@link Group}
     *
     * @param max number of instances
     * @return a list of {@link Group}
     * @since 0.1.0
     */
    public static List<Group> createGroupOf(Integer max) {
        return IntStream
                .range(1, max + 1)
                .mapToObj(i -> createGroup())
                .collect(toList());
    }
}
