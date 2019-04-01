/*
 * Copyright (C) 2019 Kaleidos Open Source SL
 *
 * This file is part of Don't Worry Be Happy (DWBH).
 * DWBH is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DWBH is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DWBH.  If not, see <https://www.gnu.org/licenses/>
 */
package dwbh.api.services;

import dwbh.api.domain.Group;
import dwbh.api.domain.User;
import dwbh.api.domain.input.GetGroupInput;
import dwbh.api.domain.input.GroupInput;
import dwbh.api.repositories.GroupRepository;
import dwbh.api.repositories.UserGroupRepository;
import dwbh.api.repositories.UserRepository;
import dwbh.api.repositories.VotingRepository;
import dwbh.api.util.Check;
import dwbh.api.util.Result;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.inject.Singleton;

/**
 * Business logic regarding {@link Group} domain
 *
 * @since 0.1.0
 */
@Singleton
public class GroupService extends BaseService {

  /**
   * Initializes service by using the database repositories
   *
   * @param groupRepository an instance of {@link GroupRepository}
   * @param userRepository an instance of {@link UserRepository}
   * @param userGroupRepository an instance of {@link UserGroupRepository}
   * @param votingRepository an instance of {@link VotingRepository}
   * @since 0.1.0
   */
  public GroupService(
      GroupRepository groupRepository,
      UserRepository userRepository,
      UserGroupRepository userGroupRepository,
      VotingRepository votingRepository) {
    super(groupRepository, userRepository, userGroupRepository, votingRepository);
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
            groupInput.getName(),
            groupInput.isAnonymousVote(),
            groupInput.isVisibleMemberList(),
            groupInput.getVotingDays(),
            groupInput.getVotingTime());

    userGroupRepository.addUserToGroup(admin.getId(), group.getId(), true);
    return group;
  }

  /**
   * Get a specific group
   *
   * @param input required data to retrieve a {@link Group}
   * @return The requested {@link Group}
   * @since 0.1.0
   */
  public Result<Group> getGroup(GetGroupInput input) {

    Optional<Group> group = Optional.ofNullable(groupRepository.getGroup(input.getGroupId()));

    Optional<Result<Group>> possibleErrors =
        Check.checkWith(
            input,
            List.of(
                this.createCheckGroupExists(group),
                this.createCheckUserIsInGroup(input.getCurrentUserId(), input.getGroupId())));

    return possibleErrors.orElseGet(() -> Result.result(group.get()));
  }
}
