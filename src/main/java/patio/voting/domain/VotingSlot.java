package patio.voting.domain;

import static patio.voting.domain.VotingSlotValidators.anonymousVoteAllowed;
import static patio.voting.domain.VotingSlotValidators.isSameUser;
import static patio.voting.domain.VotingSlotValidators.userAlreadyVoted;
import static patio.voting.domain.VotingSlotValidators.voteBelongsToVoting;
import static patio.voting.domain.VotingSlotValidators.votingHasExpired;

import java.util.UUID;
import patio.common.Builder;
import patio.common.Result;

/**
 * Represents the slot of a given user to vote in a given voting
 *
 * @see Vote
 */
public class VotingSlot {

  private VotingSlotID votingSlotID;
  private boolean anonymousAllowed;
  private Vote previousVote;
  private boolean expired;

  private VotingSlot() {
    /* empty */
  }

  /**
   * Creates a {@link Builder} to build instances of type {@link Vote}
   *
   * @return a {@link Builder} to build instances of type {@link Vote}
   * @see Builder
   */
  public static Builder<VotingSlot> builder() {
    return Builder.build(VotingSlot::new);
  }

  /**
   * Registers a given {@link Vote} in the current {@link VotingSlot}. It validates that the vote
   * complies with all the required constraints
   *
   * @param vote the {@link Vote} to register
   * @return a {@link Result} telling whether the registration went ok or not
   */
  public Result<Vote> registerVote(Vote vote) {
    return Result.<Vote>create()
        .thenCheck(() -> votingHasExpired(this))
        .thenCheck(() -> userAlreadyVoted(this))
        .thenCheck(() -> isSameUser(this, vote))
        .thenCheck(() -> voteBelongsToVoting(this, vote))
        .thenCheck(() -> anonymousVoteAllowed(this, vote))
        .then(() -> vote);
  }

  /**
   * Returns whether the current voting slot allows anonymous {@link Vote} instances or not
   *
   * @return whether the current voting slot allows anonymous {@link Vote} instances or not
   */
  public boolean isAnonymousAllowed() {
    return anonymousAllowed;
  }

  /**
   * Sets whether the current voting slot allows anonymous {@link Vote} instances or not
   *
   * @param anonymousAllowed whether the current voting slot allows anonymous {@link Vote} instances
   *     or not
   */
  public void setAnonymousAllowed(boolean anonymousAllowed) {
    this.anonymousAllowed = anonymousAllowed;
  }

  /**
   * Returns whether there was a previous vote for the current voting
   *
   * @return whether there was a previous vote for the current voting
   */
  public boolean previousVoteExists() {
    return this.getPreviousVote() != null;
  }

  /**
   * Returns a previous {@link Vote} if any existed
   *
   * @return a previous {@link Vote} if any existed
   */
  public Vote getPreviousVote() {
    return previousVote;
  }

  /**
   * Sets a previous {@link Vote} for this voting
   *
   * @param previousVote a previous {@link Vote} if any existed
   */
  public void setPreviousVote(Vote previousVote) {
    this.previousVote = previousVote;
  }

  /**
   * Returns whether the current {@link VotingSlot} has expired or not
   *
   * @return whether the current {@link VotingSlot} has expired or not
   */
  public boolean hasExpired() {
    return expired;
  }

  /**
   * Sets whether the current {@link VotingSlot} has expired or not
   *
   * @param expired whether the current {@link VotingSlot} has expired or not
   */
  public void setExpired(boolean expired) {
    this.expired = expired;
  }

  /**
   * Returns the {@linK VotingSlotID}
   *
   * @return the {@link VotingSlotID}
   */
  public VotingSlotID getVotingSlotID() {
    return votingSlotID;
  }

  /**
   * Sets the {@link VotingSlotID}
   *
   * @param votingSlotID the {@link VotingSlotID}
   */
  public void setVotingSlotID(VotingSlotID votingSlotID) {
    this.votingSlotID = votingSlotID;
  }

  /**
   * It's the {@link VotingSlot} identifier
   *
   * @see VotingSlot
   */
  public static class VotingSlotID {
    private final UUID userID;
    private final UUID votingID;

    /**
     * Initializes a {@link VotingSlotID}
     *
     * @param userID the user id
     * @param votingID the voting id
     */
    public VotingSlotID(UUID userID, UUID votingID) {
      this.userID = userID;
      this.votingID = votingID;
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
     * Returns the voting's id
     *
     * @return the voting's id
     */
    public UUID getVotingID() {
      return votingID;
    }
  }
}
