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
package dwbh.api.domain.input;

import java.time.DayOfWeek;
import java.time.OffsetTime;
import java.util.List;
import java.util.Optional;

/**
 * Group input. It contains the fields for a Group
 *
 * @since 0.1.0
 */
public class GroupInput {
  private final String name;
  private final boolean visibleMemberList;
  private final boolean anonymousVote;
  private final List<DayOfWeek> votingDays;
  private final OffsetTime votingTime;

  /**
   * Initializes the input with the group's name, visibleMemberList and anonymousVote
   *
   * @param name groups's name
   * @param visibleMemberList indicates if the group allows the members to see the member list
   * @param anonymousVote indicates if the group allows anonymous votes
   * @param votingDays represents the day of the week
   * @param votingTime votingTime of the day where the reminder is going to be sent
   * @since 0.1.0
   */
  public GroupInput(
      String name,
      boolean visibleMemberList,
      boolean anonymousVote,
      List<DayOfWeek> votingDays,
      OffsetTime votingTime) {
    this.name = name;
    this.visibleMemberList = visibleMemberList;
    this.anonymousVote = anonymousVote;
    this.votingDays = Optional.ofNullable(votingDays).orElse(List.of());
    this.votingTime = votingTime;
  }

  /**
   * Returns the name of the group
   *
   * @return the name of the group
   * @since 0.1.0
   */
  public String getName() {
    return name;
  }

  /**
   * Returns whether the group allows the members to see the member list
   *
   * @return true if it's allowed, false otherwise
   * @since 0.1.0
   */
  public boolean isVisibleMemberList() {
    return visibleMemberList;
  }

  /**
   * Returns whether the vote is allowed to be anonymous in this group or not
   *
   * @return true if it's allowed false otherwise
   * @since 0.1.0
   */
  public boolean isAnonymousVote() {
    return anonymousVote;
  }

  /**
   * Returns the days of the week as an array of type {@link DayOfWeek}
   *
   * @return the days of the week
   * @since 0.1.0
   */
  public List<DayOfWeek> getVotingDays() {
    return this.votingDays;
  }

  /**
   * Returns the votingTime of the day when the reminder is sent
   *
   * @return an instance of type {@link OffsetTime}
   * @since 0.1.0
   */
  public OffsetTime getVotingTime() {
    return votingTime;
  }
}
