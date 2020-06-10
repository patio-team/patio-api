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
package patio.common.domain.utils;

import java.util.List;

/**
 * Represents a paginated result. It tries to be as minimalistic as possible.
 *
 * @param <T> the wrapped type to be paginated over
 */
public final class PaginationResult<T> {

  private final List<T> data;
  private final long total;

  private PaginationResult(List<T> data, long total) {
    this.data = data;
    this.total = total;
  }

  /**
   * Creates a new instance of {@link PaginationResult}
   *
   * @param data the partial list
   * @param total the total number of the result set
   * @param <A> the type of the pagination result
   * @return an instance of type {@link PaginationResult}
   */
  public static <A> PaginationResult<A> from(List<A> data, long total) {
    return new PaginationResult<A>(data, total);
  }

  /**
   * Returns a partial list of the whole result set
   *
   * @return a partial list of the whole result set
   */
  public List<T> getData() {
    return data;
  }

  /**
   * Returns the total number in the result set
   *
   * @return the total number in the result set
   */
  public long getTotal() {
    return total;
  }
}
