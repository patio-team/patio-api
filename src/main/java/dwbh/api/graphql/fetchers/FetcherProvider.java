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
package dwbh.api.graphql.fetchers;

import dwbh.api.domain.Group;
import dwbh.api.domain.User;
import dwbh.api.domain.UserGroup;
import javax.inject.Inject;
import javax.inject.Singleton;
import patio.voting.adapters.persistence.entities.VotingEntity;

/**
 * Aggregates all fetchers responsible for handling GraphQL requests
 *
 * @since 0.1.0
 */
@Singleton
public class FetcherProvider {

  private GroupFetcher groupFetcher;
  private VotingFetcher votingFetcher;
  private UserFetcher userFetcher;
  private ResetPasswordFetcher resetPasswordFetcher;
  private UserGroupFetcher userGroupFetcher;
  private SecurityFetcher securityFetcher;

  /**
   * Returns an instance of {@link GroupFetcher}
   *
   * @return an instance of {@link GroupFetcher}
   * @since 0.1.0
   */
  public GroupFetcher getGroupFetcher() {
    return groupFetcher;
  }

  /**
   * Sets the instance responsible to handle request over {@link Group}
   *
   * @param groupFetcher instance of {@link GroupFetcher}
   * @since 0.1.0
   */
  @Inject
  public void setGroupFetcher(GroupFetcher groupFetcher) {
    this.groupFetcher = groupFetcher;
  }

  /**
   * Returns an instance of {@link VotingFetcher}
   *
   * @return an instance of {@link VotingFetcher}
   * @since 0.1.0
   */
  public VotingFetcher getVotingFetcher() {
    return votingFetcher;
  }

  /**
   * Sets the instance responsible to handle request over {@link VotingEntity}
   *
   * @param votingFetcher instance of {@link VotingFetcher}
   * @since 0.1.0
   */
  @Inject
  public void setVotingFetcher(VotingFetcher votingFetcher) {
    this.votingFetcher = votingFetcher;
  }

  /**
   * Returns an instance of {@link UserFetcher}
   *
   * @return an instance of {@link UserFetcher}
   * @since 0.1.0
   */
  public UserFetcher getUserFetcher() {
    return userFetcher;
  }

  /**
   * Returns an instance of {@link UserGroupFetcher}
   *
   * @return an instance of {@link UserGroupFetcher}
   * @since 0.1.0
   */
  public UserGroupFetcher getUserGroupFetcher() {
    return userGroupFetcher;
  }

  /**
   * Sets the instance responsible to handle request over {@link User}
   *
   * @param userFetcher instance of {@link UserFetcher}
   * @since 0.1.0
   */
  @Inject
  public void setUserFetcher(UserFetcher userFetcher) {
    this.userFetcher = userFetcher;
  }

  /**
   * Sets the instance responsible to handle request over {@link UserGroup}
   *
   * @param userGroupFetcher instance of {@link UserGroupFetcher}
   * @since 0.1.0
   */
  @Inject
  public void setUserGroupFetcher(UserGroupFetcher userGroupFetcher) {
    this.userGroupFetcher = userGroupFetcher;
  }

  /**
   * Returns an instance of {@link SecurityFetcher}
   *
   * @return an instance of {@link SecurityFetcher}
   * @since 0.1.0
   */
  public SecurityFetcher getSecurityFetcher() {
    return securityFetcher;
  }

  /**
   * Sets the instance responsible for handling security related requests
   *
   * @param securityFetcher instance of {@link SecurityFetcher}
   * @since 0.1.0
   */
  @Inject
  public void setSecurityFetcher(SecurityFetcher securityFetcher) {
    this.securityFetcher = securityFetcher;
  }

  /**
   * Returns an instance of {@link ResetPasswordFetcher}
   *
   * @return an instance of {@link ResetPasswordFetcher}
   * @since 0.1.0
   */
  public ResetPasswordFetcher getResetPasswordFetcher() {
    return resetPasswordFetcher;
  }

  /**
   * Sets the instance responsible to handle user password requests
   *
   * @param resetPasswordFetcher instance of {@link ResetPasswordFetcher}
   * @since 0.1.0
   */
  @Inject
  public void setResetPasswordFetcher(ResetPasswordFetcher resetPasswordFetcher) {
    this.resetPasswordFetcher = resetPasswordFetcher;
  }
}
