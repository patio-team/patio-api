package patio.voting.adapters.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dwbh.api.fixtures.Fixtures;
import io.micronaut.test.annotation.MicronautTest;
import java.util.UUID;
import java.util.stream.Stream;
import javax.inject.Inject;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import patio.voting.domain.Vote;
import patio.voting.domain.VotingSlot;

/**
 * This integration test suit tests {@link VotingPersistenceAdapter}
 *
 * @see VotingPersistenceAdapter
 */
@MicronautTest
@Testcontainers
public class VotingPersistenceAdapterTests implements FlywayAware {

  @Container
  @SuppressWarnings("unused")
  private static PostgreSQLContainer DATABASE = new PostgreSQLContainer();

  @Inject private transient VotingPersistenceAdapter adapter;
  @Inject private transient Flyway flyway;
  @Inject private transient Fixtures fixtures;

  @ParameterizedTest(name = "findVotingSlotByID: success")
  @MethodSource("getVotingSlotData")
  void testFindVotingSlotByID(String user, String voting, boolean previousVoteExists) {
    // setup: data fixtures
    fixtures.load(VotingPersistenceAdapterTests.class, "testFindVotingSlotByID.sql");

    // and: voting id
    var userID = UUID.fromString(user);
    var votingID = UUID.fromString(voting);
    var votingSlotID = new VotingSlot.VotingSlotID(userID, votingID);

    // when: looking for the specified voting slot
    var votingSlotResult = adapter.findVotingSlotByID(votingSlotID);

    // then: we should get a successful result
    assertTrue(votingSlotResult.isSuccess());

    // and: check whether there should be a previous vote or not
    var votingSlot = votingSlotResult.getSuccess();
    assertEquals(previousVoteExists, votingSlot.previousVoteExists());

    // and: the voting info is correct
    assertTrue(votingSlot.isAnonymousAllowed());
    assertTrue(votingSlot.hasExpired());
  }

  private static Stream<Arguments> getVotingSlotData() {
    return Stream.of(
        Arguments.of(
            "486590a3-fcc1-4657-a9ed-5f0f95dadea6", "7772e35c-5a87-4ba3-ab93-da8a957037fd", true),
        Arguments.of(
            "486590a3-fcc1-4657-a9ed-5f0f95dadea7", "7772e35c-5a87-4ba3-ab93-da8a957037fd", false));
  }

  @ParameterizedTest(name = "testSavingVote")
  @MethodSource("getSavingVoteAnonymity")
  void testSavingVote(boolean anonymousVote) {
    // given: some voting already created
    fixtures.load(VotingPersistenceAdapterTests.class, "testSavingVote.sql");

    // and: getting required data to identify the new vote
    var userID = UUID.fromString("486590a3-fcc1-4657-a9ed-5f0f95dadea6");
    var votingID = UUID.fromString("7772e35c-5a87-4ba3-ab93-da8a957037fd");

    // when: trying to save the vote
    Vote vote =
        Vote.builder()
            .with(v -> v.setVoteID(new Vote.VoteID(userID, votingID, null)))
            .with(v -> v.setScore(1))
            .with(v -> v.setComment("comment"))
            .with(v -> v.setAnonymous(anonymousVote))
            .build();

    var voteResult = adapter.saveVote(vote);

    // then: the persistence process result should be successful
    assertTrue(voteResult.isSuccess());
    assertEquals(anonymousVote, voteResult.map(Vote::isAnonymous).getSuccess());
  }

  private static Stream<Boolean> getSavingVoteAnonymity() {
    return Stream.of(true, false);
  }

  @Override
  public Flyway getFlyway() {
    return this.flyway;
  }
}
