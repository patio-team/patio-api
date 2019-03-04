package dwbh.api.graphql;

import dwbh.api.domain.Error;
import dwbh.api.domain.Result;
import graphql.GraphQLError;
import graphql.execution.DataFetcherResult;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Responsible for converting domain classes {@link Result} and {@link Error} to instances of type
 * {@link DataFetcherResult}
 *
 * @since 0.1.0
 * @see Result
 * @see Error
 * @see DataFetcherResult
 */
public final class ResultUtils {

  private ResultUtils() {
    /* empty */
  }

  /**
   * Converts an instance of type {@link Result} to an instance of type {@link DataFetcherResult}.
   * It keeps classes from GraphQL framework from domain classes.
   *
   * @param result instance of {@link Result} to be rendered
   * @param <T> the success type
   * @return an instance of {@link DataFetcherResult}
   * @since 0.1.0
   * @see DataFetcherResult
   * @see Result
   */
  public static <T> DataFetcherResult<T> render(Result<T> result) {
    T success = result.getSuccess();
    List<GraphQLError> errors =
        result.getErrorList().stream()
            .map(error -> new I18nGraphQLError(error.getCode(), error.getMessage()))
            .collect(Collectors.toList());

    return new DataFetcherResult<>(success, errors);
  }

  /**
   * Converts an instance of domain class {@link Error} to an instance of GraphQL {@link
   * DataFetcherResult}
   *
   * @param error the source error
   * @return an instance of {@link DataFetcherResult} to be used by GraphQL
   * @since 0.1.0
   * @see DataFetcherResult
   * @see Error
   */
  public static DataFetcherResult render(Error error) {
    return render(Result.error(error));
  }
}
