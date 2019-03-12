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
package dwbh.api.services.internal;

import com.auth0.jwt.algorithms.Algorithm;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Value;
import javax.inject.Singleton;

/**
 * Factory responsible for creating instances of {@link Algorithm}
 *
 * @since 0.1.0
 */
@Factory
public class AlgorithmFactory {

  /**
   * Provides a singleton instance of {@link Algorithm}
   *
   * @param secret the secret used to initiate the algorithm
   * @return an instance of {@link Algorithm}
   * @since 0.1.0
   */
  @Singleton
  public Algorithm create(@Value("jwt.secret") String secret) {
    return Algorithm.HMAC256(secret);
  }
}
