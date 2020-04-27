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

import dwbh.api.domain.Email;
import dwbh.api.domain.Group;
import dwbh.api.domain.User;
import dwbh.api.domain.Voting;
import dwbh.api.domain.input.CreateVotingInput;
import dwbh.api.repositories.GroupRepository;
import dwbh.api.repositories.UserGroupRepository;
import dwbh.api.repositories.VotingRepository;
import dwbh.api.services.EmailService;
import dwbh.api.services.VotingScheduling;
import dwbh.api.services.internal.templates.JadeTemplateService;
import dwbh.api.services.internal.templates.URLResolverService;
import io.micronaut.context.annotation.Value;
import io.micronaut.scheduling.annotation.Scheduled;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.TextStyle;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.UUID;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation to create new voting and send notifications to their members
 *
 * @since 0.1.0
 */
@Singleton
public class VotingSchedulingService implements VotingScheduling {

  private static final Logger LOG = LoggerFactory.getLogger(VotingSchedulingService.class);
  private static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

  private final transient GroupRepository groupRepository;
  private final transient UserGroupRepository userGroupRepository;
  private final transient VotingRepository votingRepository;
  private final transient EmailService emailService;
  private final transient JadeTemplateService jadeTemplateService;
  private final transient ResourceBundle resourceBundle;
  private final transient URLResolverService urlResolverService;
  private final transient Locale locale;

  /**
   * Requires the {@link DefaultVotingService} to get group voting information and {@link
   * EmailService} to be able to send notification to group members
   *
   * @param groupRepository to be able to get group details
   * @param userGroupRepository to be able to list all groups and group's members
   * @param votingRepository to be able to create a new {@link dwbh.api.domain.Voting}
   * @param emailService to be able to send notifications to group members
   * @param jadeTemplateService service to render email template
   * @param resourceBundle bundle to get i18n messages from
   * @param urlResolverService to resolve possible link urls for emails
   * @param locale from configuration to internationalize dates
   * @since 0.1.0
   */
  public VotingSchedulingService(
      GroupRepository groupRepository,
      UserGroupRepository userGroupRepository,
      VotingRepository votingRepository,
      EmailService emailService,
      JadeTemplateService jadeTemplateService,
      ResourceBundle resourceBundle,
      URLResolverService urlResolverService,
      @Value("${locale}") Optional<String> locale) {
    this.groupRepository = groupRepository;
    this.userGroupRepository = userGroupRepository;
    this.votingRepository = votingRepository;
    this.emailService = emailService;
    this.jadeTemplateService = jadeTemplateService;
    this.resourceBundle = resourceBundle;
    this.urlResolverService = urlResolverService;
    this.locale =
        locale.map((String localeFromConf) -> new Locale(localeFromConf)).orElse(DEFAULT_LOCALE);
  }

  @Override
  @Scheduled(fixedRate = "30s", initialDelay = "30s")
  public void scheduleVoting() {
    LOG.info("checking voting creation");
    votingRepository.listGroupsToCreateVotingFrom().stream()
        .map(this::createVoting)
        .forEach(this::notifyMembers);
  }

  private Voting createVoting(UUID groupId) {
    LOG.info(String.format("creating new voting for group %s", groupId));

    CreateVotingInput input = CreateVotingInput.newBuilder().withGroupId(groupId).build();
    Voting voting = votingRepository.createVoting(null, input.getGroupId(), OffsetDateTime.now());

    LOG.info(String.format("created voting %s", voting.getId()));
    return voting;
  }

  private void notifyMembers(Voting voting) {
    UUID groupId = voting.getGroupId();
    Group group = this.groupRepository.getGroup(groupId);

    LOG.info(String.format("notifying members or group %s", groupId));

    userGroupRepository.listUsersGroup(groupId).stream()
        .map(user -> createEmail(user, group, voting))
        .forEach(emailService::send);
  }

  @SuppressWarnings("PMD.UseConcurrentHashMap")
  private Email createEmail(User user, Group group, Voting voting) {
    String subject = this.formatMessage("voting.subject", this.getTodayAsString(), group.getName());
    String greetings = this.formatMessage("voting.greetings", user.getName());
    String template = this.resourceBundle.getString("voting.template");
    String thanks = this.resourceBundle.getString("voting.thanks");

    Map<String, Object> model = new HashMap<>();

    model.put("question", subject);
    model.put("greetings", greetings);
    model.put("groupName", group.getName());
    model.put("thanks", thanks);
    model.put("link", getVotingLink(group.getId(), voting.getId()));

    String body = jadeTemplateService.render(template, model);

    return Email.builder()
        .with(email -> email.setRecipient(user.getEmail()))
        .with(email -> email.setSubject(subject))
        .with(email -> email.setTextBody(body))
        .build();
  }

  private String getVotingLink(UUID groupId, UUID votingId) {
    return urlResolverService.resolve("/groups/{0}/votings/{1}/vote", groupId, votingId);
  }

  private String getDayOfTheWeek() {
    LocalDate today = LocalDate.now();
    DayOfWeek dayOfWeek = today.getDayOfWeek();

    return dayOfWeek.getDisplayName(TextStyle.FULL, this.locale);
  }

  private String getTodayAsString() {
    String dayOfTheWeek = this.getDayOfTheWeek();
    DateFormat formatter = DateFormat.getDateInstance(DateFormat.SHORT, this.locale);
    String today = formatter.format(new Date());

    return String.format("%s, %s", dayOfTheWeek, today);
  }

  private String formatMessage(String message, Object... values) {
    // TODO: this should be substituted with micronaut 1.2.x MessageSource
    String template = this.resourceBundle.getString(message);

    return MessageFormat.format(template, values);
  }
}
