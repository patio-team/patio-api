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
package patio.group.services;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import patio.group.domain.Group;
import patio.group.domain.UserGroup;
import patio.group.domain.UserGroupKey;
import patio.group.repositories.UserGroupRepository;
import patio.group.services.internal.InvitationService;
import patio.user.domain.User;
import patio.user.repositories.UserRepository;
import patio.user.services.internal.DefaultUserService;

public class InvitationServiceTests {

  @Test
  @SuppressFBWarnings(
      value = "UC_USELESS_OBJECT",
      justification = "It is required for the test to pass")
  public void testSendFirstTimeInvitations() {
    // setup: mocked repositories
    var userRepository = mock(UserRepository.class);
    var userGroupRepository = mock(UserGroupRepository.class);
    var defaultUserService = Mockito.mock(DefaultUserService.class);

    // and: a user pending to be invited
    var group = random(Group.class, "users");
    var invitedEmail = random(String.class);
    var invitedEmailList = List.of(invitedEmail);
    var invitedUser = random(User.class);
    invitedUser.setEmail(invitedEmail);
    var invitedUserGroup = new UserGroup(invitedUser, group);
    invitedUserGroup.setAcceptancePending(true);

    // and: a group admin (that invite the previous user)
    var currentUser = random(User.class);
    var userGroupAdmin = new UserGroup(currentUser, group);
    userGroupAdmin.setAdmin(true);

    // and: mocked returns
    when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.of(currentUser));
    when(userRepository.findAllByEmailInList(invitedEmailList)).thenReturn(Stream.of(invitedUser));
    when(userRepository.findAllByGroup(group)).thenReturn(Collections.emptyList());
    when(userGroupRepository.findById(any(UserGroupKey.class)))
        .thenReturn(Optional.of(userGroupAdmin))
        .thenReturn(Optional.empty());
    when(userGroupRepository.findAllPendingByGroup(any())).thenReturn(Stream.empty());

    // when: calling the service method
    // when: calling the service method
    var invitationService =
        new InvitationService(userRepository, userGroupRepository, defaultUserService);
    var result = invitationService.inviteGroupMembers(invitedEmailList, group, currentUser);

    // then: the result is successful
    assertTrue(result);

    // and: and the invocations are correct
    verify(defaultUserService, times(1)).createPendingUsers(invitedEmailList);
    verify(userGroupRepository, times(1)).findAllPendingByGroup(any());
    verify(userRepository, times(1)).findAllByGroup(any());
    verify(userRepository, times(1)).findAllByEmailInList(any());
    verify(userGroupRepository, times(1)).save(any());
  }

  @Test
  public void testResendInvitations() {
    // setup: mocked repositories
    var userRepository = mock(UserRepository.class);
    var userGroupRepository = mock(UserGroupRepository.class);
    var defaultUserService = Mockito.mock(DefaultUserService.class);

    // and: a pending user to be invited
    var group = random(Group.class, "users");
    var invitedEmail = random(String.class);
    var invitedEmailList = List.of(invitedEmail);
    var invitedUser = random(User.class);
    invitedUser.setEmail(invitedEmail);
    var invitedUserGroup = new UserGroup(invitedUser, group);
    invitedUserGroup.setAcceptancePending(true);

    // and: a group admin that invite the previous user
    var currentUser = random(User.class);
    var userGroupAdmin = new UserGroup(currentUser, group);
    userGroupAdmin.setAdmin(true);

    // and: mocked returns
    when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.of(currentUser));
    when(userGroupRepository.findById(any(UserGroupKey.class)))
        .thenReturn(Optional.of(userGroupAdmin))
        .thenReturn(Optional.empty());
    when(userGroupRepository.findAllPendingByGroup(any())).thenReturn(Stream.of(invitedUserGroup));

    // when: calling the service method
    var invitationService =
        new InvitationService(userRepository, userGroupRepository, defaultUserService);
    var result = invitationService.inviteGroupMembers(invitedEmailList, group, currentUser);

    // then: the result is successful
    assertTrue(result);

    // and: and the invocations are correct
    verify(defaultUserService, times(1)).createPendingUsers(invitedEmailList);
    verify(userGroupRepository, times(1)).findAllPendingByGroup(any());
    verify(userRepository, times(1)).findAllByGroup(any());
    verify(userGroupRepository, times(1)).save(any());
  }

  @Test
  public void testActivateMembership() {
    // setup: mocked repositories
    var userRepository = mock(UserRepository.class);
    var userGroupRepository = mock(UserGroupRepository.class);
    var defaultUserService = Mockito.mock(DefaultUserService.class);

    // and: mocked data
    var userGroup = random(UserGroup.class);
    userGroup.setAcceptancePending(true);
    userGroup.setInvitationOtp(random(String.class));

    // when: calling the service method
    var invitationService =
        new InvitationService(userRepository, userGroupRepository, defaultUserService);
    var result = invitationService.activateMembership(userGroup);

    // then: the result is the expected
    assertEquals(result, userGroup);
    assertEquals(result.getAcceptancePending(), false);
    assertEquals(result.getInvitationOtp(), null);

    // and: and the invocations are correct
    verify(userGroupRepository, times(1)).save(any());
  }
}
