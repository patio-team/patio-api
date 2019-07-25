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

import org.jooq.Field;
import org.jooq.Record;

/**
 * Jooq persistence related helper functions
 *
 * @since 0.1.0
 */
public final class JooqUtils {

  private JooqUtils() {
    /* empty */
  }

  /**
   * Takes a {@link Record} field value safely
   *
   * @param record {@link Record} to take the value from
   * @param field field to get the value from
   * @param <T> the type of the value wrapped in the {@link Field}
   * @return field value or null if there's no value or field is not present in record
   * @since 0.1.0
   */
  @SuppressWarnings("PMD.OnlyOneReturn")
  public static <T> T getValueSafely(Record record, Field<T> field) {
    try {
      return record.getValue(field);
    } catch (IllegalArgumentException illegal) {
      return null;
    }
  }
}
