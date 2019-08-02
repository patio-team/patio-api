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
package dwbh.api.graphql.dataloader;

import dwbh.api.domain.User;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import javax.inject.Singleton;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderRegistry;

/**
 * Data loaders are important specially when batching data in nested GraphQL queries
 *
 * @since 0.1.0
 */
@Factory
public class DataLoaderRegistryFactory {

  /**
   * Key that can be used within a {@link graphql.schema.DataFetcher} to get an instance of a {@link
   * dwbh.api.graphql.dataloader.UserBatchLoader}
   *
   * @since 0.1.0
   */
  public static final String DL_USERS_BY_IDS = "users_by_id";

  /**
   * Creates a new {@link DataLoaderRegistry}
   *
   * @param userBatchLoader an instance of {@link dwbh.api.graphql.dataloader.UserBatchLoader} to
   *     load {@link User} data
   * @return a new instance of {@link DataLoaderRegistry}
   * @since 0.1.0
   */
  @Bean
  @Singleton
  public DataLoaderRegistry create(UserBatchLoader userBatchLoader) {
    DataLoaderRegistry registry = new DataLoaderRegistry();

    return registry.register(DL_USERS_BY_IDS, DataLoader.newDataLoader(userBatchLoader));
  }
}
