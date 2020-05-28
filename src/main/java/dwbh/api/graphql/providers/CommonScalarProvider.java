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
package dwbh.api.graphql.providers;

import dwbh.api.graphql.ScalarProvider;
import dwbh.api.graphql.scalars.ScalarsConstants;
import graphql.schema.idl.RuntimeWiring;
import java.util.function.UnaryOperator;
import javax.inject.Singleton;

/**
 * Contains the implementation of custom scalars used in the schema
 *
 * @see ScalarsConstants
 */
@Singleton
public class CommonScalarProvider implements ScalarProvider {
  @Override
  public UnaryOperator<RuntimeWiring.Builder> getScalars() {
    return (builder) ->
        builder
            .scalar(ScalarsConstants.ID)
            .scalar(ScalarsConstants.DATE)
            .scalar(ScalarsConstants.TIME)
            .scalar(ScalarsConstants.DATE_TIME)
            .scalar(ScalarsConstants.DAY_OF_WEEK);
  }
}
