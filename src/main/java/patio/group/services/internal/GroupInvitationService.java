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
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import org.apache.commons.lang3.RandomStringUtils;
import patio.group.domain.Group;
import patio.group.domain.UserGroup;
import patio.group.repositories.UserGroupRepository;
import patio.infrastructure.email.domain.Email;
import patio.infrastructure.email.services.EmailService;
import patio.infrastructure.email.services.internal.EmailComposerService;
import patio.infrastructure.email.services.internal.templates.URLResolverService;
import patio.security.services.CryptoService;
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
public class GroupInvitationService {

  private final transient String acceptGroupUrl;
  private final transient UserRepository userRepository;
  private final transient UserGroupRepository userGroupRepository;
  private final transient EmailComposerService emailComposerService;
  private final transient EmailService emailService;
  private final transient URLResolverService urlResolverService;
  private final transient DefaultUserService defaultUserService;
  private final transient CryptoService cryptoService;

  /**
   * Initializes service by using the database repositories
   *
   * @param userRepository an instance of {@link UserRepository}
   * @param userGroupRepository an instance of {@link UserGroupRepository}
   * @param emailService to be able to send notifications to group members
   * @param emailComposerService service to compose the {@link Email} notifications
   * @param urlResolverService to resolve possible link urls for emails
   * @param defaultUserService to allow creating pending-registration users
   * @since 0.1.0
   */
  public GroupInvitationService(
      @Value("${front.urls.accept-group:none}") String acceptGroupUrl,
      UserRepository userRepository,
      UserGroupRepository userGroupRepository,
      EmailService emailService,
      EmailComposerService emailComposerService,
      URLResolverService urlResolverService,
      DefaultUserService defaultUserService,
      CryptoService cryptoService) {
    this.acceptGroupUrl = acceptGroupUrl;
    this.userRepository = userRepository;
    this.userGroupRepository = userGroupRepository;
    this.emailComposerService = emailComposerService;
    this.emailService = emailService;
    this.urlResolverService = urlResolverService;
    this.defaultUserService = defaultUserService;
    this.cryptoService = cryptoService;
  }

  /**
   * Sends group's invitations to the provided email List
   *
   * @param emailList the user's emails te be invited to join the group
   * @param group the group to became a member from
   * @param currentUser the user that is inviting
   * @return true
   */
  public Boolean sendInvitationsToGroup(List<String> emailList, Group group, User currentUser) {
    // pre-register those emails that are not from users
    defaultUserService.createPendingUsers(emailList);

    // resend already sent invitations
    userGroupRepository
        .findAllPendingByGroup(group)
        .forEach(
            ug -> {
              emailService.send(
                  this.composeInvitationEmail(
                      ug.getUser(), group, ug.getInvitationOtp(), currentUser));
            });

    // invite to new members
    this.findAllToInviteForFirstTime(group, emailList)
        .forEach(
            member -> {
              var otp = this.addPendingMembersToGroup(member, group);
              emailService.send(this.composeInvitationEmail(member, group, otp, currentUser));
            });

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
    userGroupRepository.save(userGroup);

    return userGroup;
  }

  private Stream<User> findAllToInviteForFirstTime(Group group, List<String> emailList) {
    List<User> alreadyInvited = userRepository.findAllByGroup(group);

    return userRepository
        .findAllByEmailInList(emailList)
        .filter(Predicate.not(alreadyInvited::contains));
  }

  private String addPendingMembersToGroup(User user, Group group) {
    final String randomOTP = cryptoService.hash(RandomStringUtils.randomAlphanumeric(17));

    UserGroup userGroup = new UserGroup(user, group);
    userGroup.setAcceptancePending(true);
    userGroup.setInvitationOtp(randomOTP);
    userGroupRepository.save(userGroup);

    return randomOTP;
  }

  @SuppressWarnings("PMD.UseConcurrentHashMap")
  private Email composeInvitationEmail(User user, Group group, String otp, User currentUser) {
    String emailRecipient = user.getEmail();
    Map<String, Object> subjectMessageVars =
        Map.of("currentUser", currentUser.getName(), "groupName", group.getName());
    String emailSubject =
        emailComposerService.getMessage("invitationToGroup.subject", subjectMessageVars);
    String disclaimerMessage = emailComposerService.getMessage("invitationToGroup.disclaimer");
    String emailBodyTemplate = emailComposerService.getMessage("invitationToGroup.bodyTemplate");

    Map<String, Object> greetingMessageVars =
        Map.of("currentUser", currentUser.getName(), "groupName", group.getName());
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
    emailBodyVars.put("link", this.getInvitationToGroupLink(user, group.getId(), otp));
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
