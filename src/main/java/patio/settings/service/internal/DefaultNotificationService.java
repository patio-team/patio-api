/*
 * Copyright (C) 2019 Kaleidos Open Source SL
 *
 * This file is part of PATIO.
 * PATIO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PATIO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PATIO.  If not, see <https://www.gnu.org/licenses/>
 */
package patio.settings.service.internal;

import java.util.List;
import java.util.Optional;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import patio.common.domain.utils.NotPresent;
import patio.common.domain.utils.Result;
import patio.group.domain.Group;
import patio.group.domain.UserGroup;
import patio.group.repositories.GroupRepository;
import patio.group.repositories.UserGroupRepository;
import patio.group.services.internal.UserIsInGroups;
import patio.settings.domain.Notification;
import patio.settings.graphql.ActivatePollNotifInput;
import patio.settings.repositories.NotificationRepository;
import patio.settings.service.NotificationService;
import patio.user.domain.User;
import patio.user.repositories.UserRepository;

/**
 * Business logic regarding {@link Group} domain
 *
 * @since 0.1.0
 */
@Singleton
@Transactional
public class DefaultNotificationService implements NotificationService {

  private final transient GroupRepository groupRepository;
  private final transient UserGroupRepository userGroupRepository;
  private final transient UserRepository userRepository;
  private final transient NotificationRepository notifRepository;

  /**
   * Initializes service by using the database repositories
   *
   * @param groupRepository an instance of {@link GroupRepository}
   * @param userRepository an instance of {@link UserRepository}
   * @param userGroupRepository an instance of {@link UserGroupRepository}
   * @param notifRepository an instance of {@link NotificationRepository}
   * @since 0.1.0
   */
  public DefaultNotificationService(
      GroupRepository groupRepository,
      UserRepository userRepository,
      UserGroupRepository userGroupRepository,
      NotificationRepository notifRepository) {
    this.groupRepository = groupRepository;
    this.userRepository = userRepository;
    this.userGroupRepository = userGroupRepository;
    this.notifRepository = notifRepository;
  }

  /**
   * List the logged user's notifications for all of the groups she belongs to
   *
   * @param user the {@link User}
   * @return the list of {@link Notification}
   */
  @Override
  public List<Notification> listGroupNotifications(User user) {
    return userRepository
        .findById(user.getId())
        .map(notifRepository::findAllGroupNotifByUser)
        .orElseGet(List::of);
  }

  /**
   * Set as active the specified group's notifications, and as inactive the rest for the rest of
   * user's groups
   *
   * @param input the {@link ActivatePollNotifInput} required to perform the operation
   * @return the list of {@link Notification}
   */
  @Override
  public Result<List<Notification>> activateNotificationsToGroups(ActivatePollNotifInput input) {
    var currentUser = input.getCurrentUser();
    var groupsToActivate = groupRepository.findByIdInList(input.getGroupIds());
    var groupsToDeactivate =
        groupRepository.findByUserAndIdNotInList(currentUser, input.getGroupIds());

    NotPresent notPresent = new NotPresent();
    UserIsInGroups userIsInGroups = new UserIsInGroups();

    return Result.<List<Notification>>create()
        .thenCheck(() -> notPresent.check(Optional.of(currentUser)))
        .thenCheck(() -> userIsInGroups.check(currentUser, groupsToActivate))
        .then(
            () -> {
              activateNotifToGroups(groupsToActivate, currentUser);
              deactivateNotifToGroups(groupsToDeactivate, currentUser);
              return notifRepository.findAllGroupNotifByUser(currentUser);
            });
  }

  private void activateNotifToGroups(List<Group> groupsToActivate, User currentUser) {
    updatePollNotification(currentUser, groupsToActivate, true);
  }

  private void deactivateNotifToGroups(List<Group> groupsToDeactivate, User currentUser) {
    updatePollNotification(currentUser, groupsToDeactivate, false);
  }

  private void updatePollNotification(User currentUser, List<Group> groups, boolean isActive) {
    groups.stream()
        .flatMap(group -> userGroupRepository.findByUserAndGroup(currentUser, group).stream())
        .map(UserGroup::getPollNotification)
        .forEach(
            n -> {
              n.setActive(isActive);
              notifRepository.save(n);
            });
  }
}
