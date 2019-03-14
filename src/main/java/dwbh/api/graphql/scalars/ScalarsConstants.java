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
package dwbh.api.graphql.scalars;

import dwbh.api.graphql.scalars.internal.DayOfWeekCoercing;
import dwbh.api.graphql.scalars.internal.UUIDCoercing;
import graphql.scalars.ExtendedScalars;
import graphql.schema.GraphQLScalarType;
import java.util.UUID;

/**
 * Extra scalar types
 *
 * @since 0.1.0
 */
public final class ScalarsConstants {

  /**
   * Represents an entity identifier backed by an {@link UUID instance}
   *
   * @since 0.1.0
   */
  public static final GraphQLScalarType ID =
      new GraphQLScalarType("ID", "Entity identifier", new UUIDCoercing());

  /**
   * Represents the day of the week (MONDAY, TUESDAY... SUNDAY)
   *
   * @since 0.1.0
   */
  public static final GraphQLScalarType DAY_OF_WEEK =
      new GraphQLScalarType("DayOfWeek", "Day of the week (MONDAY...)", new DayOfWeekCoercing());

  /**
   * An RFC-3339 compliant date scalar that accepts string values like `1996-12-19` and produces
   * `java.time.LocalDate` objects at runtime.
   *
   * @since 0.1.0
   */
  public static final GraphQLScalarType DATE = ExtendedScalars.Date;

  /**
   * An RFC-3339 compliant date time scalar that accepts string values like
   * `1996-12-19T16:39:57-08:00` and produces * `java.time.OffsetDateTime` objects at runtime.
   *
   * @since 0.1.0
   */
  public static final GraphQLScalarType DATE_TIME = ExtendedScalars.DateTime;

  /**
   * An RFC-3339 compliant time scalar that accepts string values like `6:39:57-08:00` and produces
   * `java.time.OffsetTime` objects at runtime.
   *
   * @since 0.1.0
   */
  public static final GraphQLScalarType TIME = ExtendedScalars.Time;
}
