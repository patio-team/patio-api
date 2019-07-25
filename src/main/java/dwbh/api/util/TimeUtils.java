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
package dwbh.api.util;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Some util functions to deal with time units
 *
 * @since 0.1.0
 */
public final class TimeUtils {

  private TimeUtils() {
    /* empty */
  }

  /**
   * Returns a {@link OffsetDateTime} one day in the past
   *
   * @param from the source {@link OffsetDateTime}
   * @param truncated whether we would like to get yesterday at midnight or just 24h before
   * @return an {@link OffsetDateTime} one day in the past
   * @since 0.1.0
   */
  public static OffsetDateTime yesterdayFrom(OffsetDateTime from, boolean truncated) {
    var yesterday = from.minusDays(1);

    return truncated ? yesterday.truncatedTo(ChronoUnit.DAYS) : yesterday;
  }

  /**
   * Returns a {@link OffsetDateTime} one day in the future
   *
   * @param from the source {@link OffsetDateTime}
   * @param truncated whether we would like to get tomorrow at midnight or just 24h after
   * @return an {@link OffsetDateTime} one day in the future
   * @since 0.1.0
   */
  public static OffsetDateTime tomorrowFrom(OffsetDateTime from, boolean truncated) {
    var tomorrow = from.plusDays(1);

    return truncated ? tomorrow.truncatedTo(ChronoUnit.DAYS) : tomorrow;
  }
}
