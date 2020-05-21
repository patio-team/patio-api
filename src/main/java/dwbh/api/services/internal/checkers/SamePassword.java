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
package dwbh.api.services.internal.checkers;

import static dwbh.api.util.Check.checkIsFalse;

import dwbh.api.domain.User;
import dwbh.api.services.CryptoService;
import dwbh.api.util.Check;
import dwbh.api.util.ErrorConstants;
import dwbh.api.util.Result;

/**
 * Checks if the user has already defined the same password as the one provided
 *
 * @since 0.1.0
 */
public class SamePassword {

  private final transient CryptoService cryptoService;

  /**
   * This checker expects the password being updated to be different
   *
   * @param cryptoService an instance of {@link CryptoService}
   */
  public SamePassword(CryptoService cryptoService) {
    this.cryptoService = cryptoService;
  }

  /**
   * Checks the new password is not the same the one user already has
   *
   * @param user the {@link User}
   * @param newPassword the new password has to be different
   * @return a failing {@link Result} if the user has already defined that password
   * @since 0.1.0
   */
  public Check check(User user, String newPassword) {
    return checkIsFalse(
        cryptoService.verifyWithHash(newPassword, user.getPassword()),
        ErrorConstants.SAME_PASSWORD);
  }
}
