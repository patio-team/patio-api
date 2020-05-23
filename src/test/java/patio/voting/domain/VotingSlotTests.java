package patio.voting.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dwbh.api.util.Error;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * This test suite tests all {@link VotingSlot} actions
 *
 * @see VotingSlot
 */
public class VotingSlotTests {

  @ParameterizedTest(name = "successfully registering a vote")
  @MethodSource("getValidVotingSlotAndVoteArguments")
  void testRegisterVoteSuccessfully(VotingSlot votingSlot, Vote vote) {
    // when: registering vote
    var voteResult = votingSlot.registerVote(vote);

    // expect: to be registered successfully
    assertTrue(voteResult.isSuccess());
  }

  private static Stream<Arguments> getValidVotingSlotAndVoteArguments() {
    var userID = UUID.randomUUID();
    var votingID = UUID.randomUUID();
    var votingSlotID = new VotingSlot.VotingSlotID(userID, votingID);
    var voteID = new Vote.VoteID(userID, votingID, UUID.randomUUID());

    return Stream.of(
        Arguments.of(
            votingSlot(votingSlotID, false, true, null), vote(voteID, 1, "some comments", false)),
        Arguments.of(
            votingSlot(votingSlotID, false, false, null), vote(voteID, 1, "some comments", false)),
        Arguments.of(votingSlot(votingSlotID, false, false, null), vote(voteID, 1, null, false)));
  }

  @ParameterizedTest(name = "failures while registering a vote")
  @MethodSource("getInvalidVotingSlotVoteAndErrorArguments")
  void testRegisterVoteFailures(VotingSlot votingSlot, Vote vote, Error expected) {
    // when: registering vote
    var voteResult = votingSlot.registerVote(vote);

    // expect: to be registered successfully
    assertTrue(voteResult.hasErrors());

    // and: make sure it fails because the expected error
    assertEquals(voteResult.getErrorList().get(0), expected);
  }

  private static Stream<Arguments> getInvalidVotingSlotVoteAndErrorArguments() {
    var userID = UUID.randomUUID();
    var votingID = UUID.randomUUID();
    var votingSlotID = new VotingSlot.VotingSlotID(userID, votingID);
    var voteID = new Vote.VoteID(userID, votingID, UUID.randomUUID());
    var voteIDWithDifferentVotingID = new Vote.VoteID(userID, UUID.randomUUID(), UUID.randomUUID());
    var voteIDWithDifferentUserID = new Vote.VoteID(UUID.randomUUID(), votingID, UUID.randomUUID());

    return Stream.of(
        Arguments.of(
            votingSlot(votingSlotID, true, true, null),
            vote(voteID, 1, "some comments here", false),
            VotingSlotValidators.VOTING_HAS_EXPIRED),
        Arguments.of(
            votingSlot(votingSlotID, false, true, vote(voteID, 1, "", false)),
            vote(voteID, 1, "some comments here", false),
            VotingSlotValidators.ALREADY_VOTED),
        Arguments.of(
            votingSlot(votingSlotID, false, false, null),
            vote(voteID, 1, "some comments here", true),
            VotingSlotValidators.ANONYMOUS_NOT_ALLOWED),
        Arguments.of(
            votingSlot(votingSlotID, false, true, null),
            vote(voteIDWithDifferentUserID, 1, "some comments here", true),
            VotingSlotValidators.NOT_SAME_USER),
        Arguments.of(
            votingSlot(votingSlotID, false, true, null),
            vote(voteIDWithDifferentVotingID, 1, "some comments here", true),
            VotingSlotValidators.VOTE_DOESNT_BELONG_TO_VOTING));
  }

  private static VotingSlot votingSlot(
      VotingSlot.VotingSlotID id, boolean expired, boolean anonymous, Vote previous) {
    return VotingSlot.builder()
        .with(v -> v.setPreviousVote(previous))
        .with(v -> v.setExpired(expired))
        .with(v -> v.setVotingSlotID(id))
        .with(v -> v.setAnonymousAllowed(anonymous))
        .build();
  }

  private static Vote vote(Vote.VoteID id, int score, String comment, boolean anonymous) {
    return Vote.builder()
        .with(v -> v.setScore(score))
        .with(v -> v.setVoteID(id))
        .with(v -> v.setComment(comment))
        .with(v -> v.setAnonymous(anonymous))
        .build();
  }
}
