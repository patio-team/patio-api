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
package dwbh.api.domain;

import static java.util.Optional.ofNullable;

import java.util.UUID;

/**
 * Class reponsible for building instances of type {@link User}
 *
 * @since 0.1.0
 */
public class UserBuilder {

  private transient UUID id;
  private transient String name;
  private transient String email;
  private transient String password;
  private transient String otp;

  /**
   * Creates an instance of {@link UserBuilder}
   *
   * @return an empty instance of {@link UserBuilder}
   */
  public static UserBuilder builder() {
    return new UserBuilder();
  }

  /**
   * Sets the name of the {@link User}
   *
   * @param name the name of the user
   * @return the current builder instance
   */
  public UserBuilder withName(String name) {
    ofNullable(name).ifPresent(st -> this.name = name);
    return this;
  }

  /**
   * Sets the identifier of the {@link User}
   *
   * @param id the identifier
   * @return the current builder instance
   */
  public UserBuilder withId(UUID id) {
    ofNullable(id).ifPresent(pid -> this.id = pid);
    return this;
  }

  /**
   * Sets the email of the {@link User}
   *
   * @param email the email of the user
   * @return the current builder instance
   */
  public UserBuilder withEmail(String email) {
    ofNullable(email).ifPresent(st -> this.email = email);
    return this;
  }

  /**
   * Sets the password of the {@link User}
   *
   * @param password the password of the user
   * @return the current builder instance
   */
  public UserBuilder withPassword(String password) {
    ofNullable(password).ifPresent(st -> this.password = password);
    return this;
  }

  /**
   * Sets the otp of the {@link User}
   *
   * @param otp the otp of the user
   * @return the current builder instance
   */
  public UserBuilder withOtp(String otp) {
    ofNullable(otp).ifPresent(st -> this.otp = otp);
    return this;
  }

  /**
   * Once the properties of the {@link User} have been set you can call this method to return the
   * resulting {@link User} instance
   *
   * @return the resulting {@link User} instance
   */
  public User build() {
    User user = new User();

    user.setId(id);
    user.setName(name);
    user.setEmail(email);
    user.setPassword(password);
    user.setOtp(otp);

    return user;
  }
}
