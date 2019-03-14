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

import java.util.UUID;

/**
 * Class containing the input to create a new vote
 *
 * @since 0.1.0
 */
public class CreateVoteInput {
  private final transient UUID userId;
  private final transient UUID votingId;
  private final transient String comment;
  private final transient Integer score;

  private CreateVoteInput(UUID userId, UUID votingId, String comment, Integer score) {
    this.userId = userId;
    this.votingId = votingId;
    this.comment = comment;
    this.score = score;
  }

  /**
   * Creates a new builder to create a new instance of type {@link CreateVoteInput}
   *
   * @return an instance of {@link Builder}
   * @since 0.1.0
   */
  public static Builder newBuilder() {
    return new Builder();
  }

  /**
   * Returns the vote user's id
   *
   * @return the user's id
   * @since 0.1.0
   */
  public UUID getUserId() {
    return userId;
  }

  /**
   * Returns the voting slot id
   *
   * @return the voting slot id
   * @since 0.1.0
   */
  public UUID getVotingId() {
    return votingId;
  }

  /**
   * Returns the vote's comment if the user added it
   *
   * @return the vote's comment if any
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
   * Builds an instance of type {@link CreateVoteInput}
   *
   * @since 0.1.0
   */
  public static class Builder {

    private transient CreateVoteInput input = new CreateVoteInput(null, null, null, null);

    private Builder() {
      /* empty */
    }

    /**
     * Sets the vote user's id
     *
     * @param userId the user's id
     * @return current builder instance
     * @since 0.1.0
     */
    public Builder withUserId(UUID userId) {
      this.input =
          new CreateVoteInput(userId, input.getVotingId(), input.getComment(), input.getScore());
      return this;
    }

    /**
     * Sets the voting slot id
     *
     * @param votingId voting slot's id
     * @return current builder instance
     * @since 0.1.0
     */
    public Builder withVotingId(UUID votingId) {
      this.input =
          new CreateVoteInput(input.getUserId(), votingId, input.getComment(), input.getScore());
      return this;
    }

    /**
     * Adds a comment to the vote
     *
     * @param comment extra comment to the user's vote
     * @return current builder instance
     * @since 0.1.0
     */
    public Builder withComment(String comment) {
      this.input =
          new CreateVoteInput(input.getUserId(), input.getVotingId(), comment, input.getScore());
      return this;
    }

    /**
     * Adds score to the vote
     *
     * @param score the score to vote
     * @return current builder instance
     * @since 0.1.0
     */
    public Builder withScore(Integer score) {
      this.input =
          new CreateVoteInput(input.getUserId(), input.getVotingId(), input.getComment(), score);
      return this;
    }

    /**
     * Returns the instance built with this builder
     *
     * @return an instance of type {@link CreateVoteInput}
     * @since 0.1.0
     */
    public CreateVoteInput build() {
      return this.input;
    }
  }
}
