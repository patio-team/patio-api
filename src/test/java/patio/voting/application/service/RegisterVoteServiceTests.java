package patio.voting.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import dwbh.api.util.Error;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import patio.common.Result;
import patio.voting.application.port.in.RegisterVoteUseCase;
import patio.voting.application.port.out.LoadVotingSlotPort;
import patio.voting.application.port.out.SaveVotePort;
import patio.voting.domain.Vote;
import patio.voting.domain.VotingSlot;

/**
 * This test suite tests {@link RegisterVoteService} and the validation of the {@link
 * RegisterVoteUseCase.Input} values
 *
 * @see RegisterVoteUseCase
 * @see RegisterVoteService
 * @see RegisterVoteUseCase.Input
 */
public class RegisterVoteServiceTests {

  private LoadVotingSlotPort loadVotingSlotPort;
  private SaveVotePort saveVotePort;

  @BeforeEach
  void setup() {
    loadVotingSlotPort = mock(LoadVotingSlotPort.class);
    saveVotePort = mock(SaveVotePort.class);
  }

  @Test
  void registerVoteSuccessfully() {
    // setup: a user wants to vote in a given voting
    var userID = UUID.randomUUID();
    var votingID = UUID.randomUUID();

    // and: mocking how to get a VotingSlot result and how to save it
    var votingSlotID = new VotingSlot.VotingSlotID(userID, votingID);
    var votingSlotResult = createVotingSlotResult(votingSlotID);

    when(loadVotingSlotPort.findVotingSlotByID(any())).thenReturn(votingSlotResult);
    when(saveVotePort.saveVote(any())).thenReturn(Result.result(Vote.builder().build()));

    // when: executing the use case
    var service = new RegisterVoteService(loadVotingSlotPort, saveVotePort);
    var input = createInput(votingID, userID, 1);
    var voteResult = service.registerVote(input);

    // then: the result should be success meaning the vote has been validated and registered
    assertTrue(voteResult.isSuccess());
  }

  private static Result<VotingSlot> createVotingSlotResult(VotingSlot.VotingSlotID id) {
    var votingSlot =
        VotingSlot.builder()
            .with(v -> v.setAnonymousAllowed(true))
            .with(v -> v.setExpired(false))
            .with(v -> v.setVotingSlotID(id))
            .with(v -> v.setPreviousVote(null))
            .build();

    return Result.result(votingSlot);
  }

  @ParameterizedTest(name = "VOTE REGISTRATION: fails")
  @MethodSource("getInvalidInputs")
  void registerVoteFailures(RegisterVoteUseCase.Input input, Error expected) {
    // when: initializing the service and executing it with the passed input
    var service = new RegisterVoteService(loadVotingSlotPort, saveVotePort);
    var voteResult = service.registerVote(input);

    // then: we should get an error
    assertTrue(voteResult.hasErrors());

    // and: it should be the expected error
    assertEquals(voteResult.getErrorList().get(0), expected, "we get the expected error");
  }

  private static Stream<Arguments> getInvalidInputs() {
    var votingID = UUID.randomUUID();
    var userID = UUID.randomUUID();

    return Stream.of(
        // with invalid score
        Arguments.of(createInput(votingID, userID, 0), RegisterVoteUseCase.Input.INVALID_SCORE),
        // with missing user
        Arguments.of(createInput(votingID, null, 1), RegisterVoteUseCase.Input.MISSING_USER),
        // with missing voting
        Arguments.of(createInput(null, userID, 2), RegisterVoteUseCase.Input.MISSING_VOTING));
  }

  private static RegisterVoteUseCase.Input createInput(UUID votingID, UUID userID, int score) {
    return RegisterVoteUseCase.Input.builder()
        .with(i -> i.setVotingId(votingID))
        .with(i -> i.setUserId(userID))
        .with(i -> i.setScore(score))
        .with(i -> i.setComment("anything really"))
        .with(i -> i.setAnonymous(true))
        .build();
  }
}
