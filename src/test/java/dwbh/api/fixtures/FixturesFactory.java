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
package dwbh.api.fixtures;

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import javax.inject.Singleton;
import org.jooq.DSLContext;

/**
 * Responsible to create a singleton instance of {@link Fixtures} type to use in database related
 * tests
 *
 * @since 0.1.0
 * @see Fixtures
 */
@Factory
public class FixturesFactory {

  /**
   * Creates a singleton instance of a {@link Fixtures} type
   *
   * @param dslContext to execute queries
   * @return an instance of {@link Fixtures}
   */
  @Bean
  @Singleton
  public Fixtures create(DSLContext dslContext) {
    return new Fixtures(dslContext);
  }
}
