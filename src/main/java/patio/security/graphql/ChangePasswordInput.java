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
package patio.security.graphql;

import java.util.UUID;

/**
 * Input to change the password for a user
 *
 * @since 0.1.0
 */
public class ChangePasswordInput {

  private final UUID userId;
  private final String password;

  /**
   * Returns input's userId
   *
   * @return input's userId
   * @since 0.1.0
   */
  public UUID getUserId() {
    return userId;
  }

  /**
   * Returns the input's password
   *
   * @return input password
   * @since 0.1.0
   */
  public String getPassword() {
    return password;
  }

  /**
   * Initializes the input to allow using the service
   *
   * @param userId user's email
   * @param password user's password
   * @since 0.1.0
   */
  public ChangePasswordInput(UUID userId, String password) {
    this.userId = userId;
    this.password = password;
  }

  /**
   * Creates a new builder to create a new instance of type {@link ChangePasswordInput}
   *
   * @return an instance of {@link ChangePasswordInput.Builder}
   * @since 0.1.0
   */
  public static Builder newBuilder() {
    return new ChangePasswordInput.Builder();
  }

  /**
   * Builds an instance of type {@link ChangePasswordInput}
   *
   * @since 0.1.0
   */
  public static class Builder {

    private transient ChangePasswordInput input = new ChangePasswordInput(null, null);

    private Builder() {
      /* empty */
    }

    /**
     * Sets the userId
     *
     * @param userId the current user's id
     * @return current builder instance
     * @since 0.1.0
     */
    public ChangePasswordInput.Builder withUserId(UUID userId) {
      this.input = new ChangePasswordInput(userId, input.getPassword());
      return this;
    }

    /**
     * Sets the password
     *
     * @param password the new password for the user
     * @return the builder
     * @since 0.1.0
     */
    public ChangePasswordInput.Builder withPassword(String password) {
      this.input = new ChangePasswordInput(input.getUserId(), password);
      return this;
    }

    /**
     * Returns the instance built with this builder
     *
     * @return an instance of type {@link ChangePasswordInput}
     * @since 0.1.0
     */
    public ChangePasswordInput build() {
      return this.input;
    }
  }
}
