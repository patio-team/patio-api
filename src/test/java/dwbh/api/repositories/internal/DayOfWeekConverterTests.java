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
package dwbh.api.repositories.internal;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.DayOfWeek;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Checks {@link DayOfWeekConverter}
 *
 * @since 0.1.0
 */
public class DayOfWeekConverterTests {

  @Test
  @DisplayName("Fails when trying to convert to a string array")
  void testFailureConvertingToString() {
    // expect: to fail when trying to convert it to an array of strings
    assertThrows(
        IllegalStateException.class,
        () -> {
          DayOfWeek[] days = new DayOfWeek[] {DayOfWeek.MONDAY, DayOfWeek.TUESDAY};

          new DayOfWeekConverter().to(days);
        });
  }

  @Test
  @DisplayName("Fails trying to get from type")
  void testFailureGettingFromType() {
    assertThrows(IllegalStateException.class, () -> new DayOfWeekConverter().fromType());
  }

  @Test
  @DisplayName("Fails trying to get to type")
  void testFailureGettingToType() {
    assertThrows(IllegalStateException.class, () -> new DayOfWeekConverter().toType());
  }
}
