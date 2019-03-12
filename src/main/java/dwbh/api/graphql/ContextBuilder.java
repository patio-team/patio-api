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
package dwbh.api.graphql;

import dwbh.api.domain.User;
import dwbh.api.services.SecurityService;
import io.micronaut.configuration.graphql.GraphQLContextBuilder;
import io.micronaut.http.HttpRequest;
import java.util.Optional;
import javax.inject.Singleton;

/**
 * Resolves {@link User} from the content of the authorization header
 *
 * @since 0.1.0
 */
@Singleton
public class ContextBuilder implements GraphQLContextBuilder {

  private static final String JWT_PREFIX = "JWT ";
  private static final String EMPTY = "";

  /**
   * Service responsible to resolve the user from the provided token
   *
   * @since 0.1.0
   */
  private final transient SecurityService securityService;
  /**
   * Initializes the builder with the security service
   *
   * @param securityService service responsible to check security constraints
   * @since 0.1.0
   */
  public ContextBuilder(SecurityService securityService) {
    this.securityService = securityService;
  }

  @Override
  public Object build(HttpRequest httpRequest) {
    return httpRequest
        .getHeaders()
        .getAuthorization()
        .flatMap(this::extractToken)
        .flatMap(this::resolveUser)
        .orElseGet(Context::new);
  }

  private Optional<String> extractToken(String authorization) {
    return Optional.ofNullable(authorization).map(auth -> auth.replace(JWT_PREFIX, EMPTY));
  }

  private Optional<Context> resolveUser(String token) {
    return Optional.of(token)
        .map(securityService::findUserByToken)
        .map(
            user -> {
              Context context = new Context();
              context.setAuthenticatedUser(user);
              return context;
            });
  }
}
