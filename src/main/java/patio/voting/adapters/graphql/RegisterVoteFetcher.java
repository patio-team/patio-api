package patio.voting.adapters.graphql;

import static dwbh.api.graphql.ResultUtils.render;

import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import java.util.UUID;
import javax.inject.Singleton;
import patio.common.Result;
import patio.voting.application.port.in.RegisterVoteUseCase;
import patio.voting.domain.Vote;

/**
 * GraphQL data fetcher responsible for registering a new vote
 *
 * @see RegisterVoteUseCase
 */
@Singleton
public class RegisterVoteFetcher {

  private final transient RegisterVoteUseCase registerVoteUseCase;

  /**
   * Executes the use case responsible for registering a new vote
   *
   * @param registerVoteUseCase an instance of {@link RegisterVoteUseCase}
   */
  public RegisterVoteFetcher(RegisterVoteUseCase registerVoteUseCase) {
    this.registerVoteUseCase = registerVoteUseCase;
  }

  /**
   * Registers a new {@link Vote}
   *
   * @param environment GraphQL {@link DataFetchingEnvironment} containing input values
   * @return the result of the process registering a {@link Vote}
   * @see Result
   */
  public DataFetcherResult<Vote> registerVote(DataFetchingEnvironment environment) {
    var input = createInput(environment);
    var result = registerVoteUseCase.registerVote(input);

    return render(result);
  }

  private RegisterVoteUseCase.Input createInput(DataFetchingEnvironment environment) {
    UUID userId = environment.getArgument("userId");
    UUID votingId = environment.getArgument("votingId");
    String comment = environment.getArgument("comment");
    int score = environment.getArgument("score");
    boolean anonymous = environment.getArgument("anonymous");

    return RegisterVoteUseCase.Input.builder()
        .with(i -> i.setComment(comment))
        .with(i -> i.setAnonymous(anonymous))
        .with(i -> i.setScore(score))
        .with(i -> i.setUserId(userId))
        .with(i -> i.setVotingId(votingId))
        .build();
  }
}
