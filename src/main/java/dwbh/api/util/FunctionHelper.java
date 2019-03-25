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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Class containing function helpers
 *
 * @since 0.1.0
 */
public final class FunctionHelper {

  private FunctionHelper() {
    /* empty */
  }

  /**
   * This should be used when in a local scope a function call want to be cached. Do not use outside
   * a local scope.
   *
   * @param <A> is the input type of the function
   * @param <B> is the output type of the function
   * @param function the function to locally cache
   * @return the same function with an inner cache
   * @since 0.1.0
   */
  @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
  public static <A, B> Function<A, B> cache(Function<A, B> function) {
    final Map<A, B> values = new ConcurrentHashMap<>();

    return (A a) -> values.computeIfAbsent(a, (k) -> function.apply(a));
  }
}
