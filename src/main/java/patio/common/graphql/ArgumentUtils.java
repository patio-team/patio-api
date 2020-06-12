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
package patio.common.graphql;

import graphql.schema.DataFetchingEnvironment;
import patio.common.domain.utils.PaginationRequest;

/**
 * Utils to extract arguments from {@link DataFetchingEnvironment} objects
 *
 * @see DataFetchingEnvironment
 */
public final class ArgumentUtils {

  private ArgumentUtils() {
    /* empty */
  }

  /**
   * Extracts a {@link PaginationRequest} from the current GraphQL request. It looks for an offset
   * and a max arguments
   *
   * @param env GraphQL {@link DataFetchingEnvironment} object to extract arguments from
   * @return an instance of type {@link PaginationRequest}
   */
  @SuppressWarnings("PMD.ConfusingTernary")
  public static PaginationRequest extractPaginationFrom(DataFetchingEnvironment env) {
    Integer page = env.getArgument("page");
    Integer max = env.getArgument("max");

    return PaginationRequest.from(max != null ? max : 20, page != null ? page : 0);
  }
}
