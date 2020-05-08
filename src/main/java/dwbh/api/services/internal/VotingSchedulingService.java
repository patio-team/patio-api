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
import dwbh.api.repositories.GroupRepository;
import dwbh.api.repositories.VotingRepository;
import dwbh.api.services.EmailService;
import dwbh.api.services.VotingScheduling;
import dwbh.api.services.internal.templates.JadeTemplateService;
import dwbh.api.services.internal.templates.URLResolverService;
import io.micronaut.context.MessageSource;
import io.micronaut.context.annotation.Value;
import io.micronaut.scheduling.annotation.Scheduled;
import java.text.DateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.inject.Singleton;
import javax.transaction.Transactional;
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
  private final transient VotingRepository votingRepository;
  private final transient EmailService emailService;
  private final transient JadeTemplateService jadeTemplateService;
  private final transient URLResolverService urlResolverService;
  private final transient MessageSource messageSource;
  private final transient Locale locale;

  /**
   * Requires the {@link DefaultVotingService} to get group voting information and {@link
   * EmailService} to be able to send notification to group members
   *
   * @param groupRepository to be able to get group details
   * @param votingRepository to be able to create a new {@link dwbh.api.domain.Voting}
   * @param emailService to be able to send notifications to group members
   * @param jadeTemplateService service to render email template
   * @param urlResolverService to resolve possible link urls for emails
   * @param messageSource bundle to get i18n messages from
   * @param locale from configuration to internationalize dates
   * @since 0.1.0
   */
  public VotingSchedulingService(
      GroupRepository groupRepository,
      VotingRepository votingRepository,
      EmailService emailService,
      JadeTemplateService jadeTemplateService,
      URLResolverService urlResolverService,
      MessageSource messageSource,
      @Value("${locale}") Optional<String> locale) {
    this.groupRepository = groupRepository;
    this.votingRepository = votingRepository;
    this.emailService = emailService;
    this.jadeTemplateService = jadeTemplateService;
    this.urlResolverService = urlResolverService;
    this.messageSource = messageSource;
    this.locale = locale.map(Locale::new).orElse(DEFAULT_LOCALE);
  }

  @Override
  @Scheduled(fixedRate = "30s", initialDelay = "30s")
  public void scheduleVoting() {
    checkVoting();
  }

  @Transactional
  /* default */ void checkVoting() {
    LOG.info("checking voting creation");
    this.findAllToCreateVotingFrom().map(this::createVoting).forEach(this::notifyMembers);
  }

  private Stream<Group> findAllToCreateVotingFrom() {
    OffsetDateTime now = OffsetDateTime.now();
    DayOfWeek dayOfWeek = now.getDayOfWeek();
    OffsetTime votingTime = now.toOffsetTime();

    Stream<Group> eligible =
        groupRepository.findAllByDayOfWeekAndVotingTimeLessEq(dayOfWeek.toString(), votingTime);

    List<Group> closed =
        groupRepository
            .findAllByVotingCreatedAtDateTimeBetween(now.truncatedTo(ChronoUnit.DAYS), now)
            .collect(Collectors.toList());

    return eligible.filter(Predicate.not(closed::contains));
  }

  private Voting createVoting(Group group) {
    LOG.info(String.format("creating new voting for group %s", group.getId()));

    Voting voting =
        Voting.newBuilder()
            .with(v -> v.setGroup(group))
            .with(v -> v.setCreatedAtDateTime(OffsetDateTime.now()))
            .build();

    Voting savedVoting = votingRepository.save(voting);

    LOG.info(String.format("created voting %s", savedVoting.getId()));
    return voting;
  }

  private void notifyMembers(Voting voting) {
    Group group = voting.getGroup();

    LOG.info(String.format("notifying members or group %s", group.getId()));

    group.getUsers().stream()
        .map(ug -> createEmail(ug.getUser(), ug.getGroup(), voting))
        .forEach(emailService::send);
  }

  @SuppressWarnings("PMD.UseConcurrentHashMap")
  private Email createEmail(User user, Group group, Voting voting) {
    Map<String, Object> votingSubjectVars =
        Map.of(
            "today", this.getTodayAsString(),
            "groupName", group.getName());
    Map<String, Object> votingGreetingVars = Map.of("username", user.getName());

    String subject = this.getMessage("voting.subject", votingSubjectVars);
    String greetings = this.getMessage("voting.greetings", votingGreetingVars);
    String template = this.getMessage("voting.template");
    String thanks = this.getMessage("voting.thanks");

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

  private String getMessage(String key, Map<String, Object> variables) {
    MessageSource.MessageContext context = MessageSource.MessageContext.of(this.locale, variables);

    return getInterpolatedMessage(key, context);
  }

  private String getMessage(String key) {
    MessageSource.MessageContext context = MessageSource.MessageContext.of(this.locale);

    return getInterpolatedMessage(key, context);
  }

  private String getInterpolatedMessage(String key, MessageSource.MessageContext context) {
    String template = this.messageSource.getMessage(key, context).orElse("");

    return this.messageSource.interpolate(template, context);
  }
}
