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
package patio.settings.domain;

/** Represents the different types of notifications a user may receive from patio. */
public enum NotificationType {
  POLL_START("POLL_START"),
  NEW_INVITATION("NEW_INVITATION"),
  FEEDBACK("FEEDBACK"),
  RESUME("RESUME");

  private final String name;

  /**
   * Creates a new NotificationType specifying a type from the ones available
   *
   * @param type
   */
  NotificationType(String type) {
    name = type;
  }

  @Override
  public String toString() {
    return this.name;
  }
}
