package patio.voting.application.service;

import java.util.UUID;
import javax.inject.Singleton;
import patio.common.Result;
import patio.voting.application.port.in.RegisterVoteUseCase;
import patio.voting.application.port.out.LoadVotingSlotPort;
import patio.voting.application.port.out.SaveVotePort;
import patio.voting.domain.Vote;
import patio.voting.domain.VotingSlot;

/**
 * Implements {@link RegisterVoteUseCase}
 *
 * @see RegisterVoteUseCase
 */
@Singleton
public class RegisterVoteService implements RegisterVoteUseCase {

  private final transient LoadVotingSlotPort loadVotingSlotPort;
  private final transient SaveVotePort saveVotePort;

  /**
   * Init service with required dependencies
   *
   * @param loadVotingSlotPort {@link LoadVotingSlotPort} loads a {@link VotingSlot}
   * @param saveVotePort {@link SaveVotePort} saves a given {@link Vote}
   */
  public RegisterVoteService(LoadVotingSlotPort loadVotingSlotPort, SaveVotePort saveVotePort) {
    this.loadVotingSlotPort = loadVotingSlotPort;
    this.saveVotePort = saveVotePort;
  }

  @Override
  public Result<Vote> registerVote(Input input) {
    return input
        .validate()
        .combine(this::getVotingSlot, Result::result)
        .intoFlatMap(this::createAndRegisterVote)
        .flatMap(saveVotePort::saveVote);
  }

  private Result<VotingSlot> getVotingSlot(Input in) {
    var votingSlotID = new VotingSlot.VotingSlotID(in.getUserId(), in.getVotingId());
    return loadVotingSlotPort.findVotingSlotByID(votingSlotID);
  }

  private Result<Vote> createAndRegisterVote(VotingSlot votingSlot, Input input) {
    var voteID = UUID.randomUUID();
    var vote =
        Vote.builder()
            .with(v -> v.setVoteID(new Vote.VoteID(input.getUserId(), input.getVotingId(), voteID)))
            .with(v -> v.setScore(input.getScore()))
            .with(v -> v.setComment(input.getComment()))
            .with(v -> v.setAnonymous(input.isAnonymous()))
            .build();

    return votingSlot.registerVote(vote);
  }
}
