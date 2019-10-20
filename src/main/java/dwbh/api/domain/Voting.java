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

import dwbh.api.util.Builder;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Represents the temporal scope when members of a given group can give their vote
 *
 * @since 0.1.0
 */
public final class Voting {
  private UUID id;
  private OffsetDateTime createdAtDateTime;
  private User createdBy;
  private UUID groupId;
  private Integer average;

  private Voting() {
    /* empty */
  }

  /**
   * Creates a new fluent builder to build instances of type {@link Voting}
   *
   * @return an instance of the voting builder
   * @since 0.1.0
   */
  public static Builder<Voting> newBuilder() {
    return Builder.build(Voting::new);
  }

  /**
   * Returns the voting's id
   *
   * @return the voting's id
   * @since 0.1.0
   */
  public UUID getId() {
    return id;
  }

  /**
   * Sets the voting id
   *
   * @param id the voting id
   * @since 0.1.0
   */
  public void setId(UUID id) {
    this.id = id;
  }

  /**
   * Returns the moment the voting was created
   *
   * @return the moment the voting was created
   * @since 0.1.0
   */
  public OffsetDateTime getCreatedAtDateTime() {
    return createdAtDateTime;
  }

  /**
   * Sets the moment the voting was created
   *
   * @param createdAtDateTime when the voting was created
   * @since 0.1.0
   */
  public void setCreatedAtDateTime(OffsetDateTime createdAtDateTime) {
    this.createdAtDateTime = createdAtDateTime;
  }

  /**
   * Returns the user who created the voting
   *
   * @return the user who created the voting
   * @since 0.1.0
   */
  public User getCreatedBy() {
    return createdBy;
  }

  /**
   * Sets the user who created the voting
   *
   * @param createdBy the {@link User} who created the voting
   * @since 0.1.0
   */
  public void setCreatedBy(User createdBy) {
    this.createdBy = createdBy;
  }

  /**
   * Returns the group id the voting was created for
   *
   * @return the {@link UUID} the voting was created for
   * @since 0.1.0
   */
  public UUID getGroupId() {
    return groupId;
  }

  /**
   * Sets the group the voting was created for
   *
   * @param groupId the group id the voting was created for
   * @since 0.1.0
   */
  public void setGroupId(UUID groupId) {
    this.groupId = groupId;
  }

  /**
   * Returns the voting's average
   *
   * @return the voting's average
   * @since 0.1.0
   */
  public Integer getAverage() {
    return average;
  }

  /**
   * Sets the voting average
   *
   * @param average the voting average
   * @since 0.1.0
   */
  public void setAverage(Integer average) {
    this.average = average;
  }
}
