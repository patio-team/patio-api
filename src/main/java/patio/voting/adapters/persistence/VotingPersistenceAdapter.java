package patio.voting.adapters.persistence;

import dwbh.api.domain.User;
import dwbh.api.repositories.UserRepository;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import javax.inject.Singleton;
import patio.common.Result;
import patio.voting.adapters.persistence.entities.VoteEntity;
import patio.voting.adapters.persistence.entities.VotingEntity;
import patio.voting.adapters.persistence.repositories.VoteRepository;
import patio.voting.adapters.persistence.repositories.VotingRepository;
import patio.voting.application.port.out.LoadVotingSlotPort;
import patio.voting.application.port.out.SaveVotePort;
import patio.voting.domain.Vote;
import patio.voting.domain.VotingSlot;

/**
 * This adapter implements all persistence output ports required by the voting module
 *
 * @see LoadVotingSlotPort
 * @see SaveVotePort
 */
@Singleton
public class VotingPersistenceAdapter implements LoadVotingSlotPort, SaveVotePort {

  private final transient VotingRepository votingRepository;
  private final transient VoteRepository voteRepository;
  private final transient UserRepository userRepository;

  /**
   * Initializes adapter with required persistence repositories
   *
   * @param voteRepository instance of {@link VotingRepository}
   * @param votingRepository instance of {@link VoteRepository}
   * @param userRepository instance of {@link UserRepository}
   */
  public VotingPersistenceAdapter(
      VotingRepository votingRepository,
      VoteRepository voteRepository,
      UserRepository userRepository) {
    this.votingRepository = votingRepository;
    this.voteRepository = voteRepository;
    this.userRepository = userRepository;
  }

  @Override
  public Result<VotingSlot> findVotingSlotByID(VotingSlot.VotingSlotID votingSlotID) {
    var votingID = votingSlotID.getVotingID();
    var userID = votingSlotID.getUserID();
    var votingEntity = votingRepository.findByIdAndVotingUserId(votingID, userID);
    var previousVote = voteRepository.findByVotingIdAndUserId(votingID, userID).orElse(null);

    return Result.from(votingEntity).map(ve -> toVotingSlot(ve, previousVote, userID));
  }

  @Override
  public Result<Vote> saveVote(Vote vote) {
    var userEntity = userRepository.findById(vote.getVoteID().getUserID());
    var votingEntity = votingRepository.findById(vote.getVoteID().getVotingID());

    return Result.from(votingEntity)
        .map(voting -> toVoteEntity(vote, voting, userEntity.orElse(null)))
        .map(voteRepository::save)
        .map(VotingPersistenceAdapter::toVote);
  }

  private static Vote toVote(VoteEntity voteEntity) {
    var userID = Optional.ofNullable(voteEntity.getCreatedBy()).map(User::getId).orElse(null);
    var votingID = voteEntity.getVoting().getId();
    var voteID = voteEntity.getId();

    return Vote.builder()
        .with(v -> v.setVoteID(new Vote.VoteID(userID, votingID, voteID)))
        .with(v -> v.setAnonymous(voteEntity.getCreatedBy() == null))
        .with(v -> v.setComment(voteEntity.getComment()))
        .with(v -> v.setScore(voteEntity.getScore()))
        .with(v -> v.setCreatedAtDateTime(voteEntity.getCreatedAtDateTime()))
        .build();
  }

  private static VoteEntity toVoteEntity(Vote vote, VotingEntity votingEntity, User user) {
    var voteID = vote.getVoteID().getVoteID();
    var voteUser = vote.isAnonymous() ? null : user;
    var createdAt = Optional.ofNullable(vote.getCreatedAtDateTime()).orElse(OffsetDateTime.now());

    return VoteEntity.newBuilder()
        .with(v -> v.setId(voteID))
        .with(v -> v.setComment(vote.getComment()))
        .with(v -> v.setCreatedAtDateTime(createdAt))
        .with(v -> v.setScore(vote.getScore()))
        .with(v -> v.setVoting(votingEntity))
        .with(v -> v.setCreatedBy(voteUser))
        .build();
  }

  private static VotingSlot toVotingSlot(VotingEntity voting, VoteEntity vote, UUID userID) {
    var votingSlotID = new VotingSlot.VotingSlotID(userID, voting.getId());
    var votingCreatedAt = voting.getCreatedAtDateTime();
    var expired = votingCreatedAt.plusDays(1).isBefore(OffsetDateTime.now());
    var previousVote = Optional.ofNullable(vote).map(VotingPersistenceAdapter::toVote).orElse(null);

    // #TODO VotingEntity should carry isAnonymous and expired/expired_at properties
    return VotingSlot.builder()
        .with(v -> v.setVotingSlotID(votingSlotID))
        .with(v -> v.setAnonymousAllowed(voting.getGroup().isAnonymousVote()))
        .with(v -> v.setExpired(expired))
        .with(v -> v.setPreviousVote(previousVote))
        .build();
  }
}
