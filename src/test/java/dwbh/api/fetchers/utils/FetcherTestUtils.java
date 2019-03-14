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
package dwbh.api.fetchers.utils;

import dwbh.api.domain.User;
import dwbh.api.graphql.Context;
import graphql.schema.DataFetchingEnvironment;
import java.util.Map;
import org.mockito.Mockito;

public abstract class FetcherTestUtils {

  /**
   * Generates a mocked DataFetchingEnvironment with the user as authenticatedUser and the arguments
   * of the map
   *
   * @param authenticatedUser The authenticatedUser
   * @param arguments The map of arguments of the environment
   * @return an mocked instance of {@link DataFetchingEnvironment}
   * @since 0.1.0
   */
  public static DataFetchingEnvironment generateMockedEnvironment(
      User authenticatedUser, Map<String, Object> arguments) {
    // create a mocked environment
    var mockedEnvironment = Mockito.mock(DataFetchingEnvironment.class);

    // and a mocked context
    var mockedContext = Mockito.mock(Context.class);

    // mocking context behavior to return the authenticatedUser
    Mockito.when(mockedContext.getAuthenticatedUser()).thenReturn(authenticatedUser);

    // mocking environment behavior to return the context
    Mockito.when(mockedEnvironment.getContext()).thenReturn(mockedContext);

    // mocking environment behavior to return the arguments
    arguments.forEach((k, v) -> Mockito.when(mockedEnvironment.getArgument(k)).thenReturn(v));

    return mockedEnvironment;
  }
}
