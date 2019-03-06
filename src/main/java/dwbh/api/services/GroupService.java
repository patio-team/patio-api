package dwbh.api.services;

import dwbh.api.domain.Group;
import dwbh.api.domain.GroupInput;
import dwbh.api.domain.User;
import dwbh.api.repositories.GroupRepository;
import dwbh.api.repositories.UserGroupRepository;

import javax.inject.Singleton;
import java.util.List;
import java.util.UUID;

/**
 * Business logic regarding {@link Group} domain
 *
 * @since 0.1.0
 */
@Singleton
public class GroupService {

  private final transient GroupRepository groupRepository;
  private final transient UserGroupRepository userGroupRepository;

  /**
   * Initializes service by using the database repository
   *
   * @param groupRepository an instance of {@link GroupRepository}
   * @param userGroupRepository an instance of {@link UserGroupRepository}
   * @since 0.1.0
   */
  public GroupService(GroupRepository groupRepository, UserGroupRepository userGroupRepository) {
    this.groupRepository = groupRepository;
    this.userGroupRepository = userGroupRepository;
  }

  /**
   * Fetches the list of available groups in the system
   *
   * @return a list of {@link Group} instances
   * @since 0.1.0
   */
  public List<Group> listGroups() {
    return groupRepository.listGroups();
  }

  /**
   * Get a specific group
   *
   * @param groupId group identifier
   * @return The requested {@link Group}
   * @since 0.1.0
   */
  public Group getGroup(UUID groupId) {
    return groupRepository.getGroup(groupId);
  }

  /**
   * Fetches the list of groups in which an user is a member
   *
   * @param userId user identifier
   * @return a list of {@link Group} instances
   * @since 0.1.0
   */
  public List<Group> listGroupsUser(UUID userId) {
    return userGroupRepository.listGroupsUser(userId);
  }

  /**
   * Creates a new Group
   *
   * @param admin User that creates the group, and will be the first member and admin
   * @param groupInput group information
   * @return The created {@link Group}
   * @since 0.1.0
   */
  public Group createGroup(User admin, GroupInput groupInput) {
    Group group =
        groupRepository.createGroup(
            groupInput.getName(), groupInput.isAnonymousVote(), groupInput.isVisibleMemberList());
    userGroupRepository.addUserToGroup(admin, group, true);
    return group;
  }
}
