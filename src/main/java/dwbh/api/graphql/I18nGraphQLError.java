package dwbh.api.graphql;

import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.language.SourceLocation;
import java.util.List;
import java.util.Map;

/**
 * Custom {@link GraphQLError} that only shows two fields code and message
 *
 * @since 0.1.0
 * @see GraphQLError
 */
public class I18nGraphQLError implements GraphQLError {

  private static final long serialVersionUID = 1L;
  private final String code;
  private final String message;
  /**
   * Initializes the error with its code and message
   *
   * @param code i18n code
   * @param message developer friendly message
   * @since 0.1.0
   */
  public I18nGraphQLError(String code, String message) {
    this.code = code;
    this.message = message;
  }

  @Override
  public Map<String, Object> getExtensions() {
    return Map.of("code", this.getCode());
  }

  /**
   * Returns the error code
   *
   * @return the error code
   * @since 0.1.0
   */
  public String getCode() {
    return this.code;
  }

  @Override
  public String getMessage() {
    return this.message;
  }

  @Override
  public List<SourceLocation> getLocations() {
    return null;
  }

  @Override
  public ErrorType getErrorType() {
    return ErrorType.OperationNotSupported;
  }
}
