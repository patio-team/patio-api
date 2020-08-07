/*
 * Copyright (C) 2019 Kaleidos Open Source SL
 *
 * This file is part of PATIO.
 * PATIO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PATIO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PATIO.  If not, see <https://www.gnu.org/licenses/>
 */
package patio.user.domain;

import java.util.Optional;
import java.util.UUID;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * Represents a user of patio and its data as a member of any group
 *
 * @since 0.1.0
 */
public final class GroupMember {
  /* default */ UUID id;
  /* default */ String name;
  /* default */ String email;
  /* default */ Boolean registrationPending;
  /* default */ Boolean acceptancePending;

  /**
   * Creates a new instance of {@link GroupMember}
   *
   * @param id the user id
   * @param name the user name
   * @param email the user email
   * @param registrationPending weather the user is still pending to join patio
   * @param acceptancePending weather the user is still pending to join patio
   */
  public GroupMember(
      UUID id, String name, String email, Boolean registrationPending, Boolean acceptancePending) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.registrationPending = registrationPending;
    this.acceptancePending = acceptancePending;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Boolean getRegistrationPending() {
    return registrationPending;
  }

  public void setRegistrationPending(Boolean registrationPending) {
    this.registrationPending = registrationPending;
  }

  public Boolean getAcceptancePending() {
    return acceptancePending;
  }

  public void setAcceptancePending(Boolean acceptancePending) {
    this.acceptancePending = acceptancePending;
  }

  /**
   * Generates a user's md5 hash which can be used for third party services such as Gravatar.
   *
   * @return gets a md5 hash from the user's email
   * @since 0.1.0
   */
  public String getHash() {
    return Optional.ofNullable(this.email)
        .map(String::trim)
        .map(String::toLowerCase)
        .map(DigestUtils::md5Hex)
        .orElse("");
  }
}
