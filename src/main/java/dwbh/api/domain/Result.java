package dwbh.api.domain;

import java.util.List;

/**
 * Represents a result that may contain whether a successful content or a failure content (errors)
 * or both. If it's sure the result should only contain a successful response you can use the type
 * directly, otherwise the use of this type is encouraged.
 *
 * @since 0.1.0
 */
public class Result<T> {

  private final T success;
  private final List<Error> errorList;

  /**
   * Initializes the result type with the successful and failing possible results
   *
   * @param success the successful outcome
   * @param errorList a list with possible errors
   * @see Error
   * @since 0.1.0
   */
  public Result(T success, List<Error> errorList) {
    this.success = success;
    this.errorList = errorList;
  }

  /**
   * Returns successful output
   *
   * @return the success content
   * @since 0.1.0
   */
  public T getSuccess() {
    return success;
  }

  /**
   * Returns the list of possible failures
   *
   * @return the list of errors ({@link Error})
   * @since 0.1.0
   */
  public List<Error> getErrorList() {
    return errorList;
  }

  /**
   * Use this method when creating a result with no errors
   *
   * @param success successful outcome
   * @param <T> the success type
   * @return a successful {@link Result} instance with no errors
   * @since 0.1.0
   */
  public static <T> Result<T> result(T success) {
    return new Result<T>(success, List.of());
  }

  /**
   * Use this method when creating a result with only one {@link Error}
   *
   * @param error the failing output of type {@link Error}
   * @param <T> the success type (doesnt apply here)
   * @return an instance of {@link Result} containing one error
   * @since 0.1.0
   */
  public static <T> Result<T> error(Error error) {
    return new Result<T>(null, List.of(error));
  }

  /**
   * Shortcut for creating a {@link Result} with only one {@link Error}
   *
   * @param code the error's code
   * @param message the error's message
   * @param <T> the success type (doesnt apply here)
   * @return an instance of {@link Result} representing a failing result
   * @since 0.1.0
   */
  public static <T> Result<T> error(String code, String message) {
    return new Result<T>(null, List.of(new Error(code, message)));
  }
}
