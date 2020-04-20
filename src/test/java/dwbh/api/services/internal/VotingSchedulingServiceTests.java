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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import dwbh.api.domain.Email;
import dwbh.api.domain.Group;
import dwbh.api.domain.User;
import dwbh.api.domain.Voting;
import dwbh.api.repositories.VotingRepository;
import dwbh.api.repositories.internal.JooqGroupRepository;
import dwbh.api.repositories.internal.JooqUserGroupRepository;
import dwbh.api.services.EmailService;
import dwbh.api.services.internal.templates.JadeTemplateService;
import dwbh.api.services.internal.templates.URLResolverService;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class VotingSchedulingServiceTests {

  @Test
  void testNotifyNewVotingToMembers() {
    // given: mocked services
    var groupRepository = Mockito.mock(JooqGroupRepository.class);
    var userGroupRepository = Mockito.mock(JooqUserGroupRepository.class);
    var votingRepository = Mockito.mock(VotingRepository.class);
    var emailService = Mockito.mock(EmailService.class);
    var templateService = Mockito.mock(JadeTemplateService.class);
    var resourceBundle = ResourceBundle.getBundle("messages");
    var urlResolverService = Mockito.mock(URLResolverService.class);

    // and: mocking behaviors
    Mockito.when(votingRepository.listGroupsToCreateVotingFrom())
        .thenReturn(List.of(UUID.randomUUID(), UUID.randomUUID()));

    Mockito.when(
            votingRepository.createVoting(isNull(), any(UUID.class), any(OffsetDateTime.class)))
        .thenReturn(
            Voting.newBuilder().with(voting -> voting.setGroupId(UUID.randomUUID())).build());

    Mockito.when(userGroupRepository.listUsersGroup(any(UUID.class)))
        .thenReturn(List.of(User.builder().build()));

    Mockito.when(groupRepository.getGroup(any(UUID.class))).thenReturn(Group.builder().build());

    // and: creating an instance of scheduling service
    var schedulingService =
        new VotingSchedulingService(
            groupRepository,
            userGroupRepository,
            votingRepository,
            emailService,
            templateService,
            resourceBundle,
            urlResolverService);

    // when: executing scheduling task
    schedulingService.scheduleVoting();

    // then: it lists the groups that should be notified
    verify(votingRepository, times(1)).listGroupsToCreateVotingFrom();

    // and: it creates a voting entry for each of them
    verify(votingRepository, times(2))
        .createVoting(isNull(), any(UUID.class), any(OffsetDateTime.class));

    // and: takes each group details
    verify(groupRepository, times(2)).getGroup(any(UUID.class));

    // and: lists users of each group who need to be notified
    verify(userGroupRepository, times(2)).listUsersGroup(any(UUID.class));

    // and: sends an email for each user
    verify(emailService, times(2)).send(any(Email.class));

    // and: renders a body for each email
    verify(templateService, times(2)).render(any(String.class), any(Map.class));
  }
}
