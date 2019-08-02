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
import dwbh.api.services.UserService;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import javax.inject.Singleton;
import org.dataloader.BatchLoader;

/**
 * Loads a list of {@link User} by their ids
 *
 * @since 0.1.0
 */
@Singleton
public class UserBatchLoader implements BatchLoader<UUID, User> {

  private final transient UserService userService;

  /**
   * Initializes the data loader with a {@link UserService}
   *
   * @param userService required to retrieve users
   * @since 0.1.0
   */
  public UserBatchLoader(UserService userService) {
    this.userService = userService;
  }

  @Override
  public CompletionStage<List<User>> load(List<UUID> keys) {
    return CompletableFuture.supplyAsync(() -> userService.listUsersByIds(keys));
  }
}
