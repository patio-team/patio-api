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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import patio.group.domain.Group;
import patio.group.domain.UserGroup;
import patio.group.repositories.GroupRepository;
import patio.group.repositories.UserGroupRepository;
import patio.group.services.internal.InvitationSchedulingService;
import patio.infrastructure.email.services.EmailService;
import patio.infrastructure.email.services.internal.EmailComposerService;
import patio.infrastructure.email.services.internal.templates.URLResolverService;
import patio.security.services.CryptoService;
import patio.user.domain.User;

public class InvitationSchedulingServiceTests {

  @Test
  public void testSendFirstTimeInvitations() {
    // setup: mocked repositories
    var groupRepository = mock(GroupRepository.class);
    var userGroupRepository = mock(UserGroupRepository.class);
    var emailService = mock(EmailService.class);
    var emailComposerService = mock(EmailComposerService.class);
    var urlResolverService = mock(URLResolverService.class);
    var cryptoService = mock(CryptoService.class);

    // and: mocked data (an inviting group admin)
    var group = random(Group.class, "users");
    var groupAdmin = random(User.class);

    // and: mocked data (pending user)
    var invitedEmail = random(String.class);
    var invitationOtp = random(String.class);
    var invitedUser = random(User.class);
    invitedUser.setEmail(invitedEmail);
    var invitedUserGroup = new UserGroup(invitedUser, group);
    invitedUserGroup.setAcceptancePending(true);
    invitedUserGroup.setInvitationOtp(invitationOtp);
    invitedUserGroup.setInvitedBy(groupAdmin);

    // and: mocked returns
    when(groupRepository.findAll()).thenReturn(List.of(group));
    when(userGroupRepository.findAllPendingUninvitedByGroup(group))
        .thenReturn(List.of(invitedUserGroup));

    // when: calling the service method
    var invitationSchedulingService =
        new InvitationSchedulingService(
            random(String.class),
            groupRepository,
            userGroupRepository,
            emailService,
            emailComposerService,
            urlResolverService,
            cryptoService);
    invitationSchedulingService.scheduleInvitations();

    // then: the behaviour is correct
    verify(userGroupRepository, times(1)).findAllPendingUninvitedByGroup(any());
    verify(emailService, times(1)).send(any());
    verify(emailComposerService, times(6)).getMessage(any());
    verify(emailComposerService, times(2)).getMessage(any(), any());
    verify(emailComposerService, times(1)).composeEmail(any(), any(), any(), any());
    verify(urlResolverService, times(1)).resolve(any(), any(), any(), any());
    verify(cryptoService, times(1)).hash(any());
    verify(userGroupRepository, times(1)).update(invitedUserGroup);
  }
}
