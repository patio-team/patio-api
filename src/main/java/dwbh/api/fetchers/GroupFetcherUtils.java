package dwbh.api.fetchers;

import dwbh.api.domain.GroupInput;
import dwbh.api.domain.UserGroupInput;
import graphql.schema.DataFetchingEnvironment;
import java.util.UUID;

/**
 * Contains functions to build domain inputs from the underlying {@link DataFetchingEnvironment}
 * coming from the GraphQL engine execution. This class is meant to be used only for the {@link
 * GroupFetcher} instance and related tests.
 *
 * @since 0.1.0
 */
final class GroupFetcherUtils {

  private GroupFetcherUtils() {
    /* empty */
  }

  /**
   * Creates a {@link GroupInput} from the data coming from the {@link DataFetchingEnvironment}
   *
   * @param environment the GraphQL {@link DataFetchingEnvironment}
   * @return an instance of {@link GroupInput}
   * @since 0.1.0
   */
  /* default */ static GroupInput group(DataFetchingEnvironment environment) {
    String name = environment.getArgument("name");
    boolean visibleMemberList = environment.getArgument("visibleMemberList");
    boolean anonymousVote = environment.getArgument("anonymousVote");

    return new GroupInput(name, visibleMemberList, anonymousVote);
  }

  /**
   * Creates a {@link UserGroupInput} from the data coming from the {@link DataFetchingEnvironment}
   *
   * @param environment the GraphQL {@link DataFetchingEnvironment}
   * @return an instance of {@link UserGroupInput}
   * @since 0.1.0
   */
  /* default */ static UserGroupInput userGroup(DataFetchingEnvironment environment) {
    UUID userId = environment.getArgument("userId");
    UUID groupId = environment.getArgument("groupId");

    return new UserGroupInput(userId, groupId);
  }
}
