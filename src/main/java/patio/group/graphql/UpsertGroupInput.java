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
package patio.group.graphql;

import java.time.DayOfWeek;
import java.time.OffsetTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * UpsertGroupInput input. It contains the fields for a Group
 *
 * @since 0.1.0
 */
public class UpsertGroupInput {

  private final UUID groupId;
  private final String name;
  private final boolean anonymousVote;
  private final List<DayOfWeek> votingDays;
  private final OffsetTime votingTime;
  private final UUID currentUserId;

  /**
   * Initializes the input
   *
   * @param groupId groups's id
   * @param name groups's name
   * @param anonymousVote indicates if the group allows anonymous votes
   * @param votingDays represents the day of the week
   * @param votingTime votingTime of the day where the reminder is going to be sent
   * @param currentUserId current user's id
   * @since 0.1.0
   */
  public UpsertGroupInput(
      UUID groupId,
      String name,
      boolean anonymousVote,
      List<DayOfWeek> votingDays,
      OffsetTime votingTime,
      UUID currentUserId) {
    this.groupId = groupId;
    this.name = name;
    this.anonymousVote = anonymousVote;
    this.votingDays = Optional.ofNullable(votingDays).orElse(List.of());
    this.votingTime = votingTime;
    this.currentUserId = currentUserId;
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

  /**
   * Gets currentUserId.
   *
   * @return Value of currentUserId.
   */
  public UUID getCurrentUserId() {
    return currentUserId;
  }

  /**
   * Gets groupId.
   *
   * @return Value of groupId.
   */
  public UUID getGroupId() {
    return groupId;
  }

  /**
   * Creates a new builder to create a new instance of type {@link UpsertGroupInput}
   *
   * @return an instance of {@link Builder}
   * @since 0.1.0
   */
  public static Builder newBuilder() {
    return new Builder();
  }

  /**
   * Builds an instance of type {@link UpsertGroupInput}
   *
   * @since 0.1.0
   */
  public static class Builder {

    private transient UpsertGroupInput input =
        new UpsertGroupInput(null, null, false, null, null, null);

    private Builder() {
      /* empty */
    }

    /**
     * Sets the groupId
     *
     * @param groupId the group's id
     * @return current builder instance
     * @since 0.1.0
     */
    public Builder withGroupId(UUID groupId) {
      this.input =
          new UpsertGroupInput(
              groupId,
              input.getName(),
              input.isAnonymousVote(),
              input.getVotingDays(),
              input.getVotingTime(),
              input.getCurrentUserId());
      return this;
    }

    /**
     * Sets the name
     *
     * @param name the group's name
     * @return current builder instance
     * @since 0.1.0
     */
    public Builder withName(String name) {
      this.input =
          new UpsertGroupInput(
              input.getGroupId(),
              name,
              input.isAnonymousVote(),
              input.getVotingDays(),
              input.getVotingTime(),
              input.getCurrentUserId());
      return this;
    }

    /**
     * Sets the anonymousVote
     *
     * @param anonymousVote the group's anonymousVote
     * @return current builder instance
     * @since 0.1.0
     */
    public Builder withAnonymousVote(boolean anonymousVote) {
      this.input =
          new UpsertGroupInput(
              input.getGroupId(),
              input.getName(),
              anonymousVote,
              input.getVotingDays(),
              input.getVotingTime(),
              input.getCurrentUserId());
      return this;
    }

    /**
     * Sets the votingDays
     *
     * @param votingDays the group's votingDays
     * @return current builder instance
     * @since 0.1.0
     */
    public Builder withVotingDays(List<DayOfWeek> votingDays) {
      this.input =
          new UpsertGroupInput(
              input.getGroupId(),
              input.getName(),
              input.isAnonymousVote(),
              votingDays,
              input.getVotingTime(),
              input.getCurrentUserId());
      return this;
    }

    /**
     * Sets the votingTime
     *
     * @param votingTime the group's votingTime
     * @return current builder instance
     * @since 0.1.0
     */
    public Builder withVotingTime(OffsetTime votingTime) {
      this.input =
          new UpsertGroupInput(
              input.getGroupId(),
              input.getName(),
              input.isAnonymousVote(),
              input.getVotingDays(),
              votingTime,
              input.getCurrentUserId());
      return this;
    }

    /**
     * Sets the currentUserId
     *
     * @param currentUserId the currentUserId
     * @return current builder instance
     * @since 0.1.0
     */
    public Builder withCurrentUserId(UUID currentUserId) {
      this.input =
          new UpsertGroupInput(
              input.getGroupId(),
              input.getName(),
              input.isAnonymousVote(),
              input.getVotingDays(),
              input.getVotingTime(),
              currentUserId);
      return this;
    }

    /**
     * Returns the instance built with this builder
     *
     * @return an instance of type {@link UpsertGroupInput}
     * @since 0.1.0
     */
    public UpsertGroupInput build() {
      return this.input;
    }
  }
}
