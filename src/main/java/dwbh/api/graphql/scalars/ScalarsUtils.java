package dwbh.api.graphql.scalars;

import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains utility functions for scalar types
 *
 * @since 0.1.0
 */
public final class ScalarsUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(ScalarsUtils.class);
  private static final String LOGGER_TEMPLATE = "{}.{} -> {}";
  private static final String ERRORS_LITERAL = "API_ERRORS.PARSE_LITERAL_EXCEPTION";
  private static final String ERRORS_VALUE = "API_ERRORS.PARSE_VALUE_EXCEPTION";

  private ScalarsUtils() {
    /* empty */
  }

  /**
   * Logs the error and throws a {@link CoercingParseLiteralException}
   *
   * @param fieldId field responsible for throwing an {@link CoercingParseLiteralException}
   * @param throwable the {@link Throwable} causing the exception
   * @since 0.1.0
   * @see graphql.schema.CoercingParseLiteralException
   */
  /* default */ static void throwLiteralException(String fieldId, Throwable throwable) {
    LOGGER.error(LOGGER_TEMPLATE, ERRORS_LITERAL, fieldId, throwable.getMessage());

    throw new CoercingParseLiteralException();
  }

  /**
   * Logs the error and throws a {@link CoercingParseValueException}
   *
   * @param fieldId field responsible for throwing an {@link CoercingParseValueException}
   * @param throwable the {@link Throwable} causing the exception
   * @since 0.1.0
   * @see graphql.schema.CoercingParseValueException
   */
  /* default */ static void throwValueException(String fieldId, Throwable throwable) {
    LOGGER.error(LOGGER_TEMPLATE, ERRORS_VALUE, fieldId, throwable.getMessage());

    throw new CoercingParseValueException();
  }
}
