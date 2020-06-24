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
package patio.voting.graphql;

import java.time.OffsetDateTime;
import java.util.UUID;
import patio.common.domain.utils.Builder;

/**
 * Class containing the input to recover the statistics given a group
 *
 * @since 0.1.0
 */
public class GetStatsByGroupInput {

  private OffsetDateTime startDateTime;
  private OffsetDateTime endDateTime;
  private UUID groupId;

  /**
   * Creates a new builder to create a new instance of type {@link GetStatsByGroupInput}
   *
   * @return an instance of the builder to build instances of type GetStatsByGroupInput
   * @since 0.1.0
   */
  public static Builder<GetStatsByGroupInput> newBuilder() {
    return Builder.build(GetStatsByGroupInput::new);
  }

  /**
   * Returns the startDate
   *
   * @return the startDate
   * @since 0.1.0
   */
  public OffsetDateTime getStartDateTime() {
    return startDateTime;
  }

  /**
   * Sets the startDateTime
   *
   * @param startDateTime the first date time to retrieve the stats
   */
  public void setStartDateTime(OffsetDateTime startDateTime) {
    this.startDateTime = startDateTime;
  }

  /**
   * Returns the endDate
   *
   * @return the endDate
   * @since 0.1.0
   */
  public OffsetDateTime getEndDateTime() {
    return endDateTime;
  }

  /**
   * Sets the endDateTime
   *
   * @param endDateTime the last date time to retrieve the stats
   */
  public void setEndDateTime(OffsetDateTime endDateTime) {
    this.endDateTime = endDateTime;
  }

  /**
   * Returns the id of the group
   *
   * @return the id of the group
   * @since 0.1.0
   */
  public UUID getGroupId() {
    return groupId;
  }

  /**
   * Sets the group id
   *
   * @param groupId of type {@link UUID} to get its stats
   */
  public void setGroupId(UUID groupId) {
    this.groupId = groupId;
  }
}
