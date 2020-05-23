package patio.voting.application.port.in;

import dwbh.api.util.Error;
import java.util.UUID;
import patio.common.Builder;
import patio.common.Result;
import patio.common.Validateable;
import patio.voting.domain.Vote;
import patio.voting.domain.VotingSlot;

/**
 * As a user I'd like to be able to vote in an active (not expired) {@link VotingSlot}. In order to
 * do that I need to provide the following data:
 *
 * <ul>
 *   <li><b>userId</b>: the {@link UUID} identifying me
 *   <li><b>votingID</b>: the {@link UUID} identifying the voting
 *   <li><b>comment</b>: (optional) some comment {@link String} to describe my vote
 *   <li><b>score</b>: the {@link Integer} value of my vote
 *   <li><b>anonymous</b>: whether I want my vote to be anonymous or not
 * </ul>
 */
public interface RegisterVoteUseCase {

  /**
   * Registers a new {@link Vote} in a given {@link VotingSlot}
   *
   * @param input required values to register a {@link Vote}
   * @return the result of the registration process. It's a {@link Result} of type {@link Vote}
   * @see Result
   * @see Vote
   */
  Result<Vote> registerVote(Input input);

  /**
   * Represents all required input to register a new vote
   *
   * @see Validateable
   */
  class Input implements Validateable<Input> {

    /**
     * Validation error when a given input has a score with a value less than 0 or greater than 5
     */
    public static Error INVALID_SCORE =
        new Error(
            "input.validation.score.bounds", "provided score is less than 0 or greater than 5");

    /** Validation error when the user id is missing from input */
    public static Error MISSING_USER =
        new Error("input.validation.user.null", "provided user id is null");

    /** Validation error when the voting id is missing from input */
    public static Error MISSING_VOTING =
        new Error("input.validation.voting.null", "provided voting id is null");

    private UUID userId;
    private UUID votingId;
    private String comment;
    private int score;
    private boolean anonymous;

    /**
     * Returns a {@link Builder} capable of building instances of type {@link Input}
     *
     * @return a {@link Builder} capable of building instances of type {@link Input}
     */
    public static Builder<Input> builder() {
      return Builder.build(Input::new);
    }

    @Override
    public Result<Input> validate() {
      return Result.<Input>create()
          .thenCheck(() -> notNull(userId, MISSING_USER))
          .thenCheck(() -> notNull(votingId, MISSING_VOTING))
          .thenCheck(() -> between(score, 1, 5, INVALID_SCORE))
          .then(() -> this);
    }

    public UUID getUserId() {
      return userId;
    }

    public void setUserId(UUID userId) {
      this.userId = userId;
    }

    public UUID getVotingId() {
      return votingId;
    }

    public void setVotingId(UUID votingId) {
      this.votingId = votingId;
    }

    public String getComment() {
      return comment;
    }

    public void setComment(String comment) {
      this.comment = comment;
    }

    public int getScore() {
      return score;
    }

    public void setScore(int score) {
      this.score = score;
    }

    public boolean isAnonymous() {
      return anonymous;
    }

    public void setAnonymous(boolean anonymous) {
      this.anonymous = anonymous;
    }
  }
}
