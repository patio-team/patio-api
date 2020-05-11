/*
 * Copyright (C) 2019 Kaleidos Open Source SL
 *
 * This file is part of Don't Worry Be Happy (DWBH).
 * DWBH is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DWBH is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DWBH.  If not, see <https://www.gnu.org/licenses/>
 */
package dwbh.api.services.internal.checkers;

import static dwbh.api.util.Check.checkIsTrue;

import dwbh.api.util.Check;
import dwbh.api.util.Error;
import dwbh.api.util.ErrorConstants;
import java.util.Optional;

/**
 * This checker expects the argument passed to be present, otherwise it will return a failing {@link
 * Check}
 *
 * @since 0.1.0
 */
public class NotPresent {
  /**
   * Checks that the argument passed as parameter is not null. Otherwise will build a failing {@link
   * Check} containing an error {@link ErrorConstants#NOT_FOUND}
   *
   * @param object the object checked
   * @return a failing {@link Check} if the object is not present
   * @since 0.1.0
   */
  public Check check(Optional object) {
    return check(object, ErrorConstants.NOT_FOUND);
  }

  /**
   * Checks that the argument passed as parameter is not null. Otherwise will build a failing {@link
   * Check} containing an error
   *
   * @param object the object to check
   * @param error error to show when check didn't pass
   * @return a failing {@link Check} if the object is not present
   * @since 0.1.0
   */
  public Check check(Optional object, Error error) {
    return checkIsTrue(object.isPresent(), error);
  }
}