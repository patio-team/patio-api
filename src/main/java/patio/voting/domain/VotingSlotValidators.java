package patio.voting.domain;

import static dwbh.api.util.Check.checkIsFalse;
import static dwbh.api.util.Check.checkIsTrue;

import dwbh.api.util.Check;
import dwbh.api.util.Error;
import java.util.Optional;

/**
 * Validation functions when dealing with {@link Vote} instances, e.g. when registering a new vote
 * or when updating a vote.
 *
 * @see Check
 */
/* default */ final class VotingSlotValidators {

  /* default */ static final Error ALREADY_VOTED = new Error("ALREADY_VOTED", "user already voted");
  /* default */ static final Error VOTING_HAS_EXPIRED =
      new Error("VOTING_HAS_EXPIRED", "current voting has expired");
  /* default */ static final Error ANONYMOUS_NOT_ALLOWED =
      new Error("ANONYMOUS_VOTE_NOT_ALLOWED", "voting doesn't allow anonymous votes");
  /* default */ static final Error NOT_SAME_USER =
      new Error("NOT_SAME_USER", "vote seems to be done on behave of someone else");
  /* default */ static final Error VOTE_DOESNT_BELONG_TO_VOTING =
      new Error(
          "VOTE_DOESNT_BELONG_TO_VOTING", "the current vote doesn't belong to the target voting");

  private VotingSlotValidators() {
    /* empty */
  }

  /**
   * Checks that the {@link VotingSlot} where the vote wants to be registered or updated has not
   * expired yet
   *
   * @param votingSlot the {@linkn VotingSlot} to check
   * @return a failing {@link Check} if the {@link VotingSlot} has expired
   */
  /* default */ static Check votingHasExpired(VotingSlot votingSlot) {
    return checkIsFalse(votingSlot.hasExpired(), VOTING_HAS_EXPIRED);
  }

  /**
   * Checks whether the current {@link Vote} can be used in the current {@link VotingSlot} depending
   * on whether the vote is anonymous and whether the voting allows the anonymous votes.
   *
   * @param votingSlot the {@link VotingSlot} to check
   * @param vote the {@link Vote} to check
   * @return a failing {@link Check} if the voting slot doesn't allow anonymous votes and the vote
   *     is anonymous
   */
  /* default */ static Check anonymousVoteAllowed(VotingSlot votingSlot, Vote vote) {
    var shouldBeAllowed = votingSlot.isAnonymousAllowed() || !vote.isAnonymous();
    return checkIsTrue(shouldBeAllowed, ANONYMOUS_NOT_ALLOWED);
  }

  /**
   * Checks whether there's a previous vote for the current {@link VotingSlot}
   *
   * @param votingSlot the {@link VotingSlot} to check
   * @return a failing {@link Check} if a previous vote for the voting/user has been found
   */
  /* default */ static Check userAlreadyVoted(VotingSlot votingSlot) {
    return checkIsFalse(votingSlot.previousVoteExists(), ALREADY_VOTED);
  }

  /**
   * A {@link VotingSlot} is a place to put the voting slot user's vote, not someone else's
   *
   * @param votingSlot the {@link VotingSlot} created for the user
   * @param vote the user's {@link Vote}
   * @return an invalid check if the user tries to vote on someone else's behave
   */
  static Check isSameUser(VotingSlot votingSlot, Vote vote) {
    var votingSlotUserID =
        Optional.ofNullable(votingSlot)
            .map(VotingSlot::getVotingSlotID)
            .map(VotingSlot.VotingSlotID::getUserID);
    var fromVote = Optional.ofNullable(vote).map(Vote::getVoteID).map(Vote.VoteID::getUserID);

    boolean same =
        votingSlotUserID.flatMap(fromVoting -> fromVote.map(fromVoting::equals)).orElse(false);

    return checkIsTrue(same, NOT_SAME_USER);
  }

  /**
   * This check avoids someone trying to vote in a different voting than the expected
   *
   * @param votingSlot target {@link VotingSlot}
   * @param vote the user's {@link Vote}
   * @return a failing {@link Check} if the vote tries to be registered in a different voting than
   *     the expected
   */
  static Check voteBelongsToVoting(VotingSlot votingSlot, Vote vote) {
    var fromVoting = votingSlot.getVotingSlotID().getVotingID();
    var fromVote = vote.getVoteID().getVotingID();

    return checkIsTrue(fromVoting.equals(fromVote), VOTE_DOESNT_BELONG_TO_VOTING);
  }
}
