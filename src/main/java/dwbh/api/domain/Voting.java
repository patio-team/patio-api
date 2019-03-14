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

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Represents the temporal scope when members of a given group can give their vote
 *
 * @since 0.1.0
 */
public class Voting {

  private UUID id;
  private OffsetDateTime createdAt;
  private User createdBy;
  private Group group;

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
  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  /**
   * Sets the moment the voting was created
   *
   * @param createdAt when the voting was created
   * @since 0.1.0
   */
  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
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
   * Returns the group the voting was created for
   *
   * @return the {@link Group} the voting was created for
   * @since 0.1.0
   */
  public Group getGroup() {
    return group;
  }

  /**
   * Sets the group the voting was created for
   *
   * @param group the group the voting was created for
   * @since 0.1.0
   */
  public void setGroup(Group group) {
    this.group = group;
  }

  /**
   * Creates a new fluent builder to build instances of type {@link Voting}
   *
   * @return an instance of the voting builder
   * @since 0.1.0
   */
  public static Builder newBuilder() {
    return new Builder();
  }

  /**
   * Builds instances of type {@link Voting}
   *
   * @since 0.1.0
   */
  public static class Builder {
    private transient UUID id;
    private transient OffsetDateTime createdAt;
    private transient User createdBy;
    private transient Group group;

    private Builder() {
      /* empty */
    }

    /**
     * Sets voting id
     *
     * @param id id of the {@link Voting} record
     * @return current builder instance
     * @since 0.1.0
     */
    public Builder withId(UUID id) {
      this.id = id;
      return this;
    }

    /**
     * Sets creation time
     *
     * @param createdAt creation time
     * @return current builder instance
     * @since 0.1.0
     */
    public Builder withCreatedAt(OffsetDateTime createdAt) {
      this.createdAt = createdAt;
      return this;
    }

    /**
     * Sets who created it
     *
     * @param createdBy who created it
     * @return current builder instance
     * @since 0.1.0
     */
    public Builder withCreatedBy(User createdBy) {
      this.createdBy = createdBy;
      return this;
    }

    /**
     * Sets the group
     *
     * @param group the group the voting belongs
     * @return current builder instance
     * @since 0.1.0
     */
    public Builder withGroup(Group group) {
      this.group = group;
      return this;
    }

    /**
     * Initializes and returns the {@link Voting} instance built
     *
     * @return the {@link Voting} instance initialized
     * @since 0.1.0
     */
    public Voting build() {
      Voting voting = new Voting();
      voting.setId(id);
      voting.setCreatedAt(createdAt);
      voting.setGroup(group);
      voting.setCreatedBy(createdBy);

      return voting;
    }
  }
}
