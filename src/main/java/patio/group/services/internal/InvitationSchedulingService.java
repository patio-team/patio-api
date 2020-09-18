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

import io.micronaut.context.annotation.Value;
import io.micronaut.scheduling.annotation.Scheduled;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import patio.group.domain.UserGroup;
import patio.group.repositories.GroupRepository;
import patio.group.repositories.UserGroupRepository;
import patio.group.services.InvitationsScheduling;
import patio.infrastructure.email.domain.Email;
import patio.infrastructure.email.services.EmailService;
import patio.infrastructure.email.services.internal.EmailComposerService;
import patio.infrastructure.email.services.internal.templates.URLResolverService;
import patio.security.services.CryptoService;
import patio.user.domain.User;

/**
 * Business logic regarding {@link UserGroup} domain
 *
 * @since 0.1.0
 */
@Singleton
public class InvitationSchedulingService implements InvitationsScheduling {
  private static final Logger LOG = LoggerFactory.getLogger(InvitationSchedulingService.class);

  private final transient String acceptGroupUrl;
  private final transient GroupRepository groupRepository;
  private final transient UserGroupRepository userGroupRepository;
  private final transient EmailComposerService emailComposerService;
  private final transient EmailService emailService;
  private final transient URLResolverService urlResolverService;
  private final transient CryptoService cryptoService;

  /**
   * Initializes service by using the database repositories
   *
   * @param acceptGroupUrl to get the link from configuration
   * @param groupRepository an instance of {@link GroupRepository}
   * @param userGroupRepository an instance of {@link UserGroupRepository}
   * @param emailService to be able to send notifications to group members
   * @param emailComposerService service to compose the {@link Email} notifications
   * @param urlResolverService to resolve possible link urls for emails
   * @param cryptoService an instance of {@link CryptoService}
   * @since 0.1.0
   */
  public InvitationSchedulingService(
      @Value("${front.urls.accept-group:none}") String acceptGroupUrl,
      GroupRepository groupRepository,
      UserGroupRepository userGroupRepository,
      EmailService emailService,
      EmailComposerService emailComposerService,
      URLResolverService urlResolverService,
      CryptoService cryptoService) {
    this.acceptGroupUrl = acceptGroupUrl;
    this.groupRepository = groupRepository;
    this.userGroupRepository = userGroupRepository;
    this.emailComposerService = emailComposerService;
    this.emailService = emailService;
    this.urlResolverService = urlResolverService;
    this.cryptoService = cryptoService;
  }

  /**
   * Checks whether exist any pending member without an invitation (OTP) to join each corresponding
   * group, and sends a notification with a new OTP inviting those potential members by theirs
   * emails account.
   *
   * @since 0.1.0
   */
  @Scheduled(fixedRate = "30s", initialDelay = "30s")
  @Override
  public void scheduleInvitations() {
    checkGroupInvitations();
  }

  /** Sends group's invitations to pending members */
  @Transactional
  private void checkGroupInvitations() {
    LOG.info("checking group invitations");
    groupRepository
        .findAll()
        .forEach(
            group ->
                userGroupRepository
                    .findAllPendingUninvitedByGroup(group)
                    .forEach(this::sendInvitation));
  }

  private UserGroup generateOtp(UserGroup uGroup) {
    final String randomOTP = cryptoService.hash(RandomStringUtils.randomAlphanumeric(17));
    uGroup.setInvitationOtp(randomOTP);
    uGroup.setOtpCreationDateTime(OffsetDateTime.now());
    return userGroupRepository.update(uGroup);
  }

  private UserGroup sendInvitation(UserGroup uGroup) {
    LOG.info(
        String.format(
            "notifying %s to join %s", uGroup.getUser().getEmail(), uGroup.getGroup().getName()));
    generateOtp(uGroup);
    emailService.send(this.composeInvitation(uGroup));

    return uGroup;
  }

  @SuppressWarnings("PMD.UseConcurrentHashMap")
  private Email composeInvitation(UserGroup uGroup) {
    var user = uGroup.getUser();
    var group = uGroup.getGroup();
    var invitationOtp = uGroup.getInvitationOtp();
    var invitingUser = uGroup.getInvitedBy();
    var emailRecipient = user.getEmail();

    Map<String, Object> subjectMessageVars =
        Map.of("invitingUser", invitingUser.getName(), "groupName", group.getName());
    String emailSubject =
        emailComposerService.getMessage("invitationToGroup.subject", subjectMessageVars);
    String disclaimerMessage = emailComposerService.getMessage("invitationToGroup.disclaimer");
    String emailBodyTemplate = emailComposerService.getMessage("invitationToGroup.bodyTemplate");

    Map<String, Object> greetingMessageVars =
        Map.of("invitingUser", invitingUser.getName(), "groupName", group.getName());
    String greetingsMessage =
        emailComposerService.getMessage("invitationToGroup.greetings", greetingMessageVars);
    String patioIntroMessage = emailComposerService.getMessage("invitationToGroup.patioIntro");
    String acceptMessage = emailComposerService.getMessage("invitationToGroup.acceptButton");
    String patioTeamMessage = emailComposerService.getMessage("invitationToGroup.patioTeam");
    String welcome = emailComposerService.getMessage("invitationToGroup.welcome");

    Map<String, Object> emailBodyVars = new HashMap<>();
    emailBodyVars.put("subject", emailSubject);
    emailBodyVars.put("greetings", greetingsMessage);
    emailBodyVars.put("patioIntro", patioIntroMessage);
    emailBodyVars.put("link", this.getInvitationToGroupLink(user, group.getId(), invitationOtp));
    emailBodyVars.put("accept", acceptMessage);
    emailBodyVars.put("disclaimer", disclaimerMessage);
    emailBodyVars.put("welcome", welcome);
    emailBodyVars.put("patioTeam", patioTeamMessage);

    return emailComposerService.composeEmail(
        emailRecipient, emailSubject, emailBodyTemplate, emailBodyVars);
  }

  private String getInvitationToGroupLink(User user, UUID groupId, String otp) {
    var isNewUser = user.isRegistrationPending();
    return urlResolverService.resolve(this.acceptGroupUrl, groupId, isNewUser, otp);
  }
}
