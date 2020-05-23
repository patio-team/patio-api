package patio.voting.application.port.out;

import patio.common.Result;
import patio.voting.domain.Vote;

public interface SaveVotePort {

  Result<Vote> saveVote(Vote vote);
}
