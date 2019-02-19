package dwbh.api.services;

import dwbh.api.domain.Group;
import dwbh.api.repositories.GroupRepository;

import javax.inject.Singleton;
import java.util.Arrays;
import java.util.List;

/**
 * Business logic regarding {@link Group} domain
 *
 * @since 0.1.0
 */
@Singleton
public class GroupService {

    private final GroupRepository repository;

    public GroupService(GroupRepository repository) {
        this.repository = repository;
    }

    /**
     * Fetches the list of available groups in the system
     *
     * @return a list of {@link Group} instances
     * @since 0.1.0
     */
    public List<Group> listGroups() {
        return repository.listGroups();
    }
}
