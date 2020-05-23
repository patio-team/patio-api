package patio.voting.application.port.out;

import patio.common.Result;
import patio.voting.domain.VotingSlot;

/**
 * Responsible to find a {@link VotingSlot}
 *
 * @see VotingSlot
 * @see Result
 */
public interface LoadVotingSlotPort {

  /**
   * Finds a {@link VotingSlot} that is the spot of a given user to vote in a given voting
   *
   * @param votingSlotID the id identifying a {@link VotingSlot}
   * @return a {@link VotingSlot} containing information on the current voting
   * @see VotingSlot
   */
  Result<VotingSlot> findVotingSlotByID(VotingSlot.VotingSlotID votingSlotID);
}
