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

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.jooq.Converter;

/**
 * Converts {@link String} to {@link DayOfWeek} instances
 *
 * @since 0.1.0
 */
@SuppressWarnings("PMD.ShortMethodName")
public class DayOfWeekConverter implements Converter<String[], List<DayOfWeek>> {

  /**
   * Current version id
   *
   * @since 0.1.0
   */
  public static final long serialVersionUID = 1L;

  @Override
  public List<DayOfWeek> from(String[] databaseObject) {
    return Optional.ofNullable(databaseObject).map(this::convertFromString).orElse(List.of());
  }

  private List<DayOfWeek> convertFromString(String... databaseObject) {
    return Arrays.stream(databaseObject).map(DayOfWeek::valueOf).collect(Collectors.toList());
  }

  @Override
  public String[] to(List<DayOfWeek> userObject) {
    throw new IllegalStateException("This method is not supposed to be called");
  }

  @Override
  public Class<String[]> fromType() {
    throw new IllegalStateException("This method is not supposed to be called");
  }

  @Override
  public Class<List<DayOfWeek>> toType() {
    throw new IllegalStateException("This method is not supposed to be called");
  }
}
