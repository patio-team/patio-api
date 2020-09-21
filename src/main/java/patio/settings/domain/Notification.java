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

import java.time.OffsetTime;
import java.util.UUID;
import javax.persistence.*;
import patio.common.domain.utils.Builder;
import patio.group.domain.Group;
import patio.user.domain.User;

/**
 * Represents the user's preferences about being notified by patio
 *
 * <p>Note: It's planned to be included information regarding notification hours, and also to be
 * referenced just from {@link User} (with no group) representing general notifications.
 */
@Entity
@Table(name = "notifications")
public final class Notification {

  @Id @GeneratedValue private UUID id;

  @Column(name = "is_active")
  private boolean active;

  @Column(name = "notifying_time")
  private OffsetTime notifyingTime;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", columnDefinition = "text[]")
  private NotificationType type;

  @OneToOne
  @JoinColumn(name = "user_id", referencedColumnName = "id")
  private User user;

  @OneToOne
  @JoinColumn(name = "group_id", referencedColumnName = "id")
  private Group group;

  /**
   * Creates a new {@link Builder} to create an instance of type {@link Notification}
   *
   * @return an instance of {@link Builder} to create instances of type {@link Notification}
   * @since 0.1.0
   */
  public static Builder<Notification> builder() {
    return Builder.build(Notification::new);
  }

  /**
   * Gets id.
   *
   * @return Value of id.
   */
  public UUID getId() {
    return id;
  }

  /**
   * Returns if the user wants to receive or not this notification.
   *
   * @return Boolean
   */
  public Boolean getActive() {
    return active;
  }

  /**
   * Returns the hour to be notified
   *
   * @return Time
   */
  public OffsetTime getNotifyingTime() {
    return notifyingTime;
  }

  /**
   * Returns the notification's type between the available options
   *
   * @return a {@link NotificationType}
   */
  public NotificationType getType() {
    return type;
  }

  /**
   * Returns the group related by this notification, if it involves any groups
   *
   * @return the related {@link Group}
   */
  public Group getGroup() {
    return group;
  }

  /**
   * Returns the user affected by the notification
   *
   * @return a {@link User}
   */
  public User getUser() {
    return user;
  }

  /**
   * Sets new id.
   *
   * @param id New value of id.
   */
  public void setId(UUID id) {
    this.id = id;
  }

  /**
   * Sets if the user wants to receive or not this notification.
   *
   * @param active the Boolean value
   */
  public void setActive(Boolean active) {
    this.active = active;
  }

  /**
   * Sets the hour to be notified
   *
   * @param notifyingTime the hour to be notified
   */
  public void setNotifyingTime(OffsetTime notifyingTime) {
    this.notifyingTime = notifyingTime;
  }

  /**
   * Sets the notification's type between the available options
   *
   * @param type from the available {@link NotificationType}
   */
  public void setType(NotificationType type) {
    this.type = type;
  }

  /**
   * Sets the user to be receiving the notification
   *
   * @param user the user related with the notification
   */
  public void setUser(User user) {
    this.user = user;
  }

  /**
   * Sets the group, if any, related to the notification
   *
   * @param group the {@link Group} related to the notification
   */
  public void setGroup(Group group) {
    this.group = group;
  }
}
