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
package patio.group.services.internal;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import patio.group.domain.Group;
import patio.group.domain.UserGroup;
import patio.group.repositories.UserGroupRepository;
import patio.user.domain.User;
import patio.user.repositories.UserRepository;
import patio.user.services.internal.DefaultUserService;

/**
 * Business logic regarding {@link UserGroup} domain
 *
 * @since 0.1.0
 */
@Singleton
@Transactional
public class InvitationService {
  private final transient UserRepository userRepository;
  private final transient UserGroupRepository userGroupRepository;
  private final transient DefaultUserService defaultUserService;

  /**
   * Initializes service by using the database repositories
   *
   * @param userRepository an instance of {@link UserRepository}
   * @param userGroupRepository an instance of {@link UserGroupRepository}
   * @param defaultUserService to allow creating pending-registration users
   * @since 0.1.0
   */
  public InvitationService(
      UserRepository userRepository,
      UserGroupRepository userGroupRepository,
      DefaultUserService defaultUserService) {
    this.userRepository = userRepository;
    this.userGroupRepository = userGroupRepository;
    this.defaultUserService = defaultUserService;
  }

  /**
   * Sends group's invitations to the provided email List
   *
   * @param emailList the user's emails te be invited to join the group
   * @param group the group to became a member from
   * @param currentUser the user that is inviting
   * @return true
   */
  public Boolean inviteGroupMembers(List<String> emailList, Group group, User currentUser) {
    // pre-register in patio those emails that are not from users
    defaultUserService.createPendingUsers(emailList);

    // resend invitations to those ones explicitly indicated
    findAllToResendInvitation(group, emailList).forEach(ug -> this.setAsPending(ug, currentUser));

    // send invitations to those ones never invited before
    findAllToInviteFirstTime(group, emailList)
        .forEach(u -> this.addPendingMembersToGroup(u, group, currentUser));

    return true;
  }

  /**
   * Set a pending member in a group as fully active
   *
   * @param userGroup the {@link UserGroup} to activate
   * @return the {@link UserGroup}
   */
  public UserGroup activateMembership(UserGroup userGroup) {
    userGroup.setAcceptancePending(false);
    userGroup.setInvitationOtp(null);
    userGroup.setMemberFromDateTime(OffsetDateTime.now());
    userGroupRepository.save(userGroup);

    return userGroup;
  }

  private Stream<UserGroup> findAllToResendInvitation(Group group, List<String> emailList) {
    Stream<UserGroup> alreadyPending = userGroupRepository.findAllPendingByGroup(group);

    return alreadyPending.filter(ug -> emailList.contains(ug.getUser().getEmail()));
  }

  private Stream<User> findAllToInviteFirstTime(Group group, List<String> emailList) {
    List<User> alreadyInvited = userRepository.findAllByGroup(group);

    return userRepository
        .findAllByEmailInList(emailList)
        .filter(Predicate.not(alreadyInvited::contains));
  }

  private void setAsPending(UserGroup userGroup, User currentUser) {
    userGroup.setAcceptancePending(true);
    userGroup.setInvitationOtp(null);
    userGroup.setOtpCreationDateTime(null);
    userGroup.setInvitedBy(currentUser);
    userGroupRepository.save(userGroup);
  }

  private void addPendingMembersToGroup(User user, Group group, User currentUser) {
    UserGroup userGroup = new UserGroup(user, group);
    setAsPending(userGroup, currentUser);
  }
}
