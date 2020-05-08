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

import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;
import static io.github.benas.randombeans.api.EnhancedRandom.randomSetOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNotNull;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import dwbh.api.domain.Email;
import dwbh.api.domain.Group;
import dwbh.api.domain.User;
import dwbh.api.domain.UserGroup;
import dwbh.api.domain.Voting;
import dwbh.api.repositories.GroupRepository;
import dwbh.api.repositories.UserRepository;
import dwbh.api.repositories.VotingRepository;
import dwbh.api.services.EmailService;
import dwbh.api.services.internal.templates.JadeTemplateService;
import dwbh.api.services.internal.templates.URLResolverService;
import io.micronaut.context.MessageSource;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class VotingSchedulingServiceTests {

  @Test
  @SuppressWarnings("unchecked")
  void testNotifyNewVotingToMembers() {
    // given: mocked services
    var groupRepository = Mockito.mock(GroupRepository.class);
    var votingRepository = Mockito.mock(VotingRepository.class);
    var emailService = Mockito.mock(EmailService.class);
    var templateService = Mockito.mock(JadeTemplateService.class);
    var urlResolverService = Mockito.mock(URLResolverService.class);
    var messageSource = Mockito.mock(MessageSource.class);
    var userRepository = Mockito.mock(UserRepository.class);

    // and: mocking behaviors
    var group1Users = randomSetOf(2, UserGroup.class);
    var group1 =
        Group.builder().with(g -> g.setName("eligible")).with(g -> g.setUsers(group1Users)).build();
    var closedVotingGroups = randomListOf(4, Group.class);
    var groupsVotingToday = new ArrayList<Group>(closedVotingGroups);
    groupsVotingToday.add(group1);

    Mockito.when(groupRepository.findAllByDayOfWeekAndVotingTimeLessEq(any(), any()))
        .thenReturn(groupsVotingToday.stream());

    Mockito.when(groupRepository.findAllByVotingCreatedAtDateTimeBetween(any(), any()))
        .thenReturn(closedVotingGroups.stream());

    var voting = Voting.newBuilder().with(v -> v.setGroup(group1)).build();
    Mockito.when(votingRepository.save(any(Voting.class))).thenReturn(voting);

    Mockito.when(
            messageSource.getMessage(any(String.class), any(MessageSource.MessageContext.class)))
        .thenReturn(Optional.of(RandomStringUtils.randomAlphanumeric(12)));

    Mockito.when(
            messageSource.interpolate(any(String.class), any(MessageSource.MessageContext.class)))
        .thenReturn(RandomStringUtils.randomAlphanumeric(12));

    Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(User.builder().build()));

    User user = User.builder().with(u -> u.setName("john")).build();
    UserGroup userGroup = new UserGroup(user, group1);
    Group mockedGroup = Group.builder().with(g -> g.setUsers(Set.of(userGroup))).build();
    Mockito.when(groupRepository.findById(any(UUID.class))).thenReturn(Optional.of(mockedGroup));

    // and: creating an instance of scheduling service
    var schedulingService =
        new VotingSchedulingService(
            groupRepository,
            votingRepository,
            emailService,
            templateService,
            urlResolverService,
            messageSource,
            Optional.of("es"));

    // when: executing scheduling task
    schedulingService.scheduleVoting();

    // then: it lists the groups that should be notified
    verify(groupRepository, times(1)).findAllByVotingCreatedAtDateTimeBetween(any(), any());

    verify(groupRepository, times(1)).findAllByDayOfWeekAndVotingTimeLessEq(any(), any());

    // and: takes each group details
    verify(votingRepository, times(1)).save(any());

    // and: sends an email for each user
    verify(emailService, atLeast(2)).send(any(Email.class));

    // and: renders a body for each email
    verify(templateService, times(2)).render(any(String.class), any(Map.class));

    // and: with content taken from messages.properties (four calls per email: subject, greetings,
    // template and thanks)
    verify(messageSource, times(8))
        .getMessage(isNotNull(), any(MessageSource.MessageContext.class));

    verify(messageSource, times(8))
        .interpolate(isNotNull(), any(MessageSource.MessageContext.class));

    verify(templateService, atLeast(2)).render(any(String.class), any(Map.class));
  }
}
