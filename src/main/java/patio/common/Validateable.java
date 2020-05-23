package patio.common;

import static dwbh.api.util.Check.checkIsTrue;

import dwbh.api.util.Check;
import dwbh.api.util.Error;

/**
 * An object implementing this interface can be validated and return a {@link Result} object as
 * result
 *
 * @param <T> type of the object to be validated
 * @see Check
 */
public interface Validateable<T> {

  /**
   * Checks that a given object value is not null
   *
   * @param value the value to check
   * @param error an {@link Error} if the value is null
   * @return a failing {@link Check} if the value is null
   */
  default Check notNull(Object value, Error error) {
    return checkIsTrue(value != null, error);
  }

  /**
   * Checks that a given number lays between two boundaries
   *
   * @param value the value to check
   * @param min lower boundary
   * @param max upper boundary
   * @param error the error in case the check fails
   * @return a failing {@link Check} if the value is not between the bounds
   */
  default Check between(int value, int min, int max, Error error) {
    return checkIsTrue(value >= min && value <= max, error);
  }

  /**
   * Returns the {@link Result} of the validation
   *
   * @return the {@link Result} of the validation
   */
  Result<T> validate();
}
