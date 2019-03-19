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

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Represents a given user's vote
 *
 * @since 0.1.0
 */
public class Vote {
  private final transient UUID id;
  private final transient Voting voting;
  private final transient User createdBy;
  private final transient OffsetDateTime createdAt;
  private final transient String comment;
  private final transient Integer score;

  private Vote(
      UUID id,
      Voting voting,
      User createdBy,
      OffsetDateTime createdAt,
      String comment,
      Integer score) {
    this.id = id;
    this.voting = voting;
    this.createdBy = createdBy;
    this.createdAt = createdAt;
    this.comment = comment;
    this.score = score;
  }

  /**
   * Returns vote's id
   *
   * @return vote's id
   * @since 0.1.0
   */
  public UUID getId() {
    return id;
  }

  /**
   * Return vote's voting record
   *
   * @return an instance of type {@link Voting}
   * @since 0.1.0
   */
  public Voting getVoting() {
    return voting;
  }

  /**
   * Returns the user who voted
   *
   * @return an instance of type {@link User}
   * @since 0.1.0
   */
  public User getCreatedBy() {
    return createdBy;
  }

  /**
   * Returns the moment the user voted
   *
   * @return an instance of type {@link OffsetDateTime}
   * @since 0.1.0
   */
  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  /**
   * Returns any comment that the user wanted to comment about the vote
   *
   * @return a simple {@link String} with the comment
   * @since 0.1.0
   */
  public String getComment() {
    return comment;
  }

  /**
   * Returns the score that the user wanted to vote
   *
   * @return a simple {@link Integer} with the score
   * @since 0.1.0
   */
  public Integer getScore() {
    return score;
  }

  /**
   * A builder to build an instance of type {@link Vote}
   *
   * @return an instance of {@link Builder}
   * @since 0.1.0
   */
  public static Builder newBuilder() {
    return new Builder();
  }

  /**
   * Builds an instance of type {@link Vote}
   *
   * @since 0.1.0
   */
  public static class Builder {

    private transient Vote vote = new Vote(null, null, null, null, null, null);

    /**
     * Sets the vote's id
     *
     * @param id vote's id
     * @return instance of the current builder
     * @since 0.1.0
     */
    public Builder withId(UUID id) {
      this.vote =
          new Vote(id, vote.voting, vote.createdBy, vote.createdAt, vote.comment, vote.score);
      return this;
    }

    /**
     * The {@link Voting} this vote belongs
     *
     * @param voting the {@link Voting} this vote belongs
     * @return instance of the current builder
     * @since 0.1.0
     */
    public Builder withVoting(Voting voting) {
      this.vote =
          new Vote(vote.id, voting, vote.createdBy, vote.createdAt, vote.comment, vote.score);
      return this;
    }

    /**
     * Sets the user who did the vote
     *
     * @param createdBy the user who did vote
     * @return instance of the current builder
     * @since 0.1.0
     */
    public Builder withCreatedBy(User createdBy) {
      this.vote =
          new Vote(vote.id, vote.voting, createdBy, vote.createdAt, vote.comment, vote.score);
      return this;
    }

    /**
     * Sets when the vote was created
     *
     * @param createdAt when the vote was created
     * @return instance of the current builder
     * @since 0.1.0
     */
    public Builder withCreatedAt(OffsetDateTime createdAt) {
      this.vote =
          new Vote(vote.id, vote.voting, vote.createdBy, createdAt, vote.comment, vote.score);
      return this;
    }

    /**
     * Sets any comment that the user considered important to complete the vote
     *
     * @param comment any comment that the user may want to add to the vote
     * @return instance of the current builder
     * @since 0.1.0
     */
    public Builder withComment(String comment) {
      this.vote =
          new Vote(vote.id, vote.voting, vote.createdBy, vote.createdAt, comment, vote.score);
      return this;
    }

    /**
     * Sets the score the user wants to vote
     *
     * @param score the score to vote
     * @return instance of the current builder
     * @since 0.1.0
     */
    public Builder withScore(Integer score) {
      this.vote =
          new Vote(vote.id, vote.voting, vote.createdBy, vote.createdAt, vote.comment, score);
      return this;
    }

    /**
     * Returns the instance of type {@link Vote} built with this builder
     *
     * @return an instance of {@link Vote}
     * @since 0.1.0
     */
    public Vote build() {
      return this.vote;
    }
  }
}
