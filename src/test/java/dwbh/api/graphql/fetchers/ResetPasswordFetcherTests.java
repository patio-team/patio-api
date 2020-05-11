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

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;

import dwbh.api.domain.Email;
import dwbh.api.domain.User;
import dwbh.api.graphql.fetchers.utils.FetcherTestUtils;
import dwbh.api.services.internal.DefaultResetPasswordService;
import dwbh.api.services.internal.EmailComposerService;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Tests {@link UserFetcher} class
 *
 * @since 0.1.0
 */
public class ResetPasswordFetcherTests {

  @Test
  void testResetPassword() {
    // given: a user
    User user = random(User.class);

    // given: mocked services
    var mockedService = Mockito.mock(DefaultResetPasswordService.class);
    var emailComposerService = Mockito.mock(EmailComposerService.class);

    // and: a mocked environment
    var mockedEnvironment =
        FetcherTestUtils.generateMockedEnvironment(user, Map.of("email", user.getEmail()));

    // when: fetching user list invoking the service
    ResetPasswordFetcher fetchers = new ResetPasswordFetcher(mockedService);
    fetchers.resetPassword(mockedEnvironment);

    // then: check certain assertions should be met
    assertThat("The user's OTP is now defined", user.getOtp(), is(not(isEmptyString())));
    Mockito.when(emailComposerService.composeEmail(any(), any(), any(), any()))
        .thenReturn(random(Email.class));
  }
}