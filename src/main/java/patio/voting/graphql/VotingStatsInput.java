package patio.voting.graphql;

import patio.common.domain.utils.Builder;

import java.util.UUID;

public class VotingStatsInput {
  private UUID votingId;

  /**
   * Returns the id of the voting
   *
   * @return the id of the user
   * @since 0.1.0
   */
  public UUID getVotingId() {
    return votingId;
  }

  /**
   * Sets voting id
   *
   * @param votingId the voting id
   */
  public void setVotingId(UUID votingId) {
    this.votingId = votingId;
  }

  /**
   * Creates a builder to build instances of type {@link VotingStatsInput}
   *
   * @return a {@link Builder} that creates instances of type {@link VotingStatsInput}
   * @since 0.1.0
   */
  public static Builder<VotingStatsInput> builder() {
    return Builder.build(VotingStatsInput::new);
  }
}


