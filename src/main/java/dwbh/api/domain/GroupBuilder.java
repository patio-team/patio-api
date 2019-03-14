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
package dwbh.api.domain;

import static java.util.Optional.ofNullable;

import java.time.DayOfWeek;
import java.time.OffsetTime;
import java.util.List;
import java.util.UUID;

/**
 * Class reponsible for building instances of type {@link Group}
 *
 * @since 0.1.0
 */
public class GroupBuilder {

  private transient String name;
  private transient UUID id;
  private transient boolean visibleMemberList;
  private transient boolean anonymousVote;
  private transient List<DayOfWeek> daysOfWeek;
  private transient OffsetTime time;

  /**
   * Creates an instance of {@link GroupBuilder}
   *
   * @return an empty instance of {@link GroupBuilder}
   */
  public static GroupBuilder builder() {
    return new GroupBuilder();
  }

  /**
   * Sets the name of the {@link Group}
   *
   * @param name the name of the group
   * @return the current builder instance
   */
  public GroupBuilder withName(String name) {
    ofNullable(name).ifPresent(st -> this.name = name);
    return this;
  }

  /**
   * Sets the identifier of the {@link Group}
   *
   * @param id the identifier
   * @return the current builder instance
   */
  public GroupBuilder withId(UUID id) {
    ofNullable(id).ifPresent(pid -> this.id = pid);
    return this;
  }

  /**
   * Sets the visibility of the member list
   *
   * @param visibleMemberList true if the member list is visible or false otherwise
   * @return the current builder instance
   */
  public GroupBuilder withVisibleMemberList(boolean visibleMemberList) {
    ofNullable(visibleMemberList).ifPresent(visible -> this.visibleMemberList = visible);
    return this;
  }

  /**
   * Sets whether the vote should be anonymous or not
   *
   * @param anonymousVote true if it's anonymous false otherwise
   * @return the current builder instance
   */
  public GroupBuilder withAnonymousVote(boolean anonymousVote) {
    this.anonymousVote = anonymousVote;
    return this;
  }

  /**
   * Sets the days of the week when reminders will be sent
   *
   * @param daysOfWeek instances of {@link DayOfWeek}
   * @return the current builder instance
   * @since 0.1.0
   */
  public GroupBuilder withDaysOfWeek(List<DayOfWeek> daysOfWeek) {
    this.daysOfWeek = daysOfWeek;
    return this;
  }

  /**
   * Sets the moment during the day where reminders will be sent
   *
   * @param offsetTime an instance of {@link OffsetTime}
   * @return the current builder instance
   * @since 0.1.0
   */
  public GroupBuilder withTime(OffsetTime offsetTime) {
    this.time = offsetTime;
    return this;
  }

  /**
   * Once the properties of the {@link Group} have been set you can call this method to return the
   * resulting {@link Group} instance
   *
   * @return the resulting {@link Group} instance
   */
  public Group build() {
    Group group = new Group();

    group.setAnonymousVote(anonymousVote);
    group.setName(name);
    group.setId(id);
    group.setVisibleMemberList(visibleMemberList);
    group.setVotingDays(daysOfWeek);
    group.setVotingTime(time);

    return group;
  }
}
