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
package dwbh.api.services.internal;

import dwbh.api.domain.Group;
import dwbh.api.domain.input.GetGroupInput;
import dwbh.api.domain.input.UpsertGroupInput;
import dwbh.api.repositories.GroupRepository;
import dwbh.api.repositories.UserGroupRepository;
import dwbh.api.repositories.internal.JooqGroupRepository;
import dwbh.api.repositories.internal.JooqUserGroupRepository;
import dwbh.api.services.GroupService;
import dwbh.api.services.internal.checkers.NotNull;
import dwbh.api.services.internal.checkers.UserIsGroupAdmin;
import dwbh.api.services.internal.checkers.UserIsInGroup;
import dwbh.api.util.Result;
import java.util.List;
import java.util.UUID;
import javax.inject.Singleton;

/**
 * Business logic regarding {@link Group} domain
 *
 * @since 0.1.0
 */
@Singleton
public class DefaultGroupService implements GroupService {

  private final transient GroupRepository groupRepository;
  private final transient UserGroupRepository userGroupRepository;

  /**
   * Initializes service by using the database repositories
   *
   * @param groupRepository an instance of {@link JooqGroupRepository}
   * @param userGroupRepository an instance of {@link JooqUserGroupRepository}
   * @since 0.1.0
   */
  public DefaultGroupService(
      GroupRepository groupRepository, UserGroupRepository userGroupRepository) {
    this.groupRepository = groupRepository;
    this.userGroupRepository = userGroupRepository;
  }

  @Override
  public List<Group> listGroups() {
    return groupRepository.listGroups();
  }

  @Override
  public List<Group> listGroupsUser(UUID userId) {
    return userGroupRepository.listGroupsUser(userId);
  }

  @Override
  public Group createGroup(UpsertGroupInput createGroupInput) {
    UUID id = UUID.randomUUID();
    Group group =
        groupRepository.upsertGroup(
            id,
            createGroupInput.getName(),
            createGroupInput.isAnonymousVote(),
            createGroupInput.isVisibleMemberList(),
            createGroupInput.getVotingDays(),
            createGroupInput.getVotingTime());

    userGroupRepository.addUserToGroup(createGroupInput.getCurrentUserId(), group.getId(), true);
    return group;
  }

  @Override
  public Result<Group> updateGroup(UpsertGroupInput input) {
    UserIsGroupAdmin userIsGroupAdmin = new UserIsGroupAdmin(userGroupRepository);

    return Result.<Group>create()
        .thenCheck(() -> userIsGroupAdmin.check(input.getCurrentUserId(), input.getGroupId()))
        .then(() -> updateGroupIfSuccess(input));
  }

  private Group updateGroupIfSuccess(UpsertGroupInput updateGroupInput) {
    return groupRepository.upsertGroup(
        updateGroupInput.getGroupId(),
        updateGroupInput.getName(),
        updateGroupInput.isVisibleMemberList(),
        updateGroupInput.isAnonymousVote(),
        updateGroupInput.getVotingDays(),
        updateGroupInput.getVotingTime());
  }

  @Override
  public Result<Group> getGroup(GetGroupInput input) {
    Group group = groupRepository.getGroup(input.getGroupId());

    NotNull notNull = new NotNull();
    UserIsInGroup userIsInGroup = new UserIsInGroup(userGroupRepository);

    return Result.<Group>create()
        .thenCheck(() -> notNull.check(group))
        .thenCheck(() -> userIsInGroup.check(input.getCurrentUserId(), input.getGroupId()))
        .then(() -> group);
  }
}
