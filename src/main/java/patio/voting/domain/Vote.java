package patio.voting.domain;

import java.time.OffsetDateTime;
import java.util.UUID;
import patio.common.Builder;

/**
 * Represents the vote of a given user in a {@link VotingSlot}
 *
 * @see VotingSlot
 */
public class Vote {

  private VoteID voteID;
  private int score;
  private String comment;
  private boolean anonymous;
  private OffsetDateTime createdAtDateTime;

  private Vote() {
    /* empty */
  }

  /**
   * Creates a new {@link Builder} to build instances of type {@link Vote}
   *
   * @return a {@link Builder} of type {@link Vote}
   * @see Builder
   */
  public static Builder<Vote> builder() {
    return Builder.build(Vote::new);
  }

  /**
   * Returns the vote's score {@link Integer}
   *
   * @return the vote's score {@link Integer}
   */
  public int getScore() {
    return score;
  }

  /**
   * Sets the {@link Integer} value of the vote
   *
   * @param score the {@link Integer} value of the vote
   */
  public void setScore(int score) {
    this.score = score;
  }

  /**
   * Returns the vote's comment {@link String}
   *
   * @return the vote's comment {@link String}
   */
  public String getComment() {
    return comment;
  }

  /**
   * Returns whether the vote is anonymous or not
   *
   * @return whether the vote is anonymous or not
   */
  public boolean isAnonymous() {
    return anonymous;
  }

  /**
   * Sets whether this vote wants to be registered as anonymous or not
   *
   * @param anonymous true if the vote wants to be registered as anonymous, false otherwise
   */
  public void setAnonymous(boolean anonymous) {
    this.anonymous = anonymous;
  }

  /**
   * Sets the vote's comment {@link String}
   *
   * @param comment the vote's comment {@link String}
   */
  public void setComment(String comment) {
    this.comment = comment;
  }

  /**
   * Returns the vote's unique identifier
   *
   * @return the vote's unique identifier
   * @see VoteID
   */
  public VoteID getVoteID() {
    return voteID;
  }

  /**
   * Sets the vote's unique identifier
   *
   * @param voteID the vote's unique identifier
   */
  public void setVoteID(VoteID voteID) {
    this.voteID = voteID;
  }

  /**
   * Returns when was created the vote
   *
   * @return an {@link OffsetDateTime} telling when was created the vote
   */
  public OffsetDateTime getCreatedAtDateTime() {
    return createdAtDateTime;
  }

  /**
   * Sets when was created the vote
   *
   * @param createdAtDateTime an {@link OffsetDateTime} telling when was created the vote
   */
  public void setCreatedAtDateTime(OffsetDateTime createdAtDateTime) {
    this.createdAtDateTime = createdAtDateTime;
  }

  /**
   * Identifies a unique {@link Vote}
   *
   * @see {@link Vote}
   */
  public static class VoteID {
    private final UUID userID;
    private final UUID votingID;
    private final UUID voteID;

    /**
     * Initializes a {@link VoteID}
     *
     * @param userID the id of the user who voted
     * @param votingID the group's voting id
     * @param voteID the id of the vote
     */
    public VoteID(UUID userID, UUID votingID, UUID voteID) {
      this.userID = userID;
      this.votingID = votingID;
      this.voteID = voteID;
    }

    /**
     * Returns the user's id
     *
     * @return the user's id
     */
    public UUID getUserID() {
      return userID;
    }

    /**
     * Returns the id of the voting
     *
     * @return the group's voting id
     */
    public UUID getVotingID() {
      return votingID;
    }

    /**
     * Returns the id of the vote
     *
     * @return the id of the vote
     */
    public UUID getVoteID() {
      return voteID;
    }
  }
}
