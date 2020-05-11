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
package dwbh.api.services;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

import dwbh.api.domain.Email;
import dwbh.api.domain.User;
import dwbh.api.repositories.UserRepository;
import dwbh.api.services.internal.Auth0CryptoService;
import dwbh.api.services.internal.DefaultResetPasswordService;
import dwbh.api.services.internal.EmailComposerService;
import dwbh.api.services.internal.templates.URLResolverService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Tests {@link DefaultResetPasswordService}
 *
 * @since 0.1.0
 */
public class ResetPasswordServiceTests {

  @Test
  void testResetPasswordRequest() {
    // given: a user
    User user = random(User.class);

    // given: a mocked repositories and services
    var userRepository = Mockito.mock(UserRepository.class);
    Mockito.when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

    // and: a new randomly-generated One-time Password (OTP)
    var randomOTP = "$2a$10$ccXSNGubl.ja9eV2Dfrxmhd9t";
    var auth0CryptoService = Mockito.mock(Auth0CryptoService.class);
    Mockito.when(auth0CryptoService.hash(any())).thenReturn(randomOTP);

    // and: other mocked services
    var emailComposerService = Mockito.mock(EmailComposerService.class);
    var emailService = Mockito.mock(EmailService.class);
    var urlResolverService = Mockito.mock(URLResolverService.class);

    // when: invoking service resetPasswordRequest()
    var defaultResetPasswordService =
        new DefaultResetPasswordService(
            "/user/{0}/reset?otp={1}",
            userRepository,
            auth0CryptoService,
            emailComposerService,
            emailService,
            urlResolverService);
    defaultResetPasswordService.resetPasswordRequest(user.getEmail());

    // then: we should set an OTP for the user
    assertThat("The user's OTP is now defined", user.getOtp(), is(randomOTP));
    verify(emailComposerService, atLeast(1)).composeEmail(any(), any(), any(), any());

    // and: an password resetting email is generated
    Mockito.when(emailComposerService.composeEmail(any(), any(), any(), any()))
        .thenReturn(random(Email.class));

    // and: the email finally gets send
    verify(emailService, atLeast(1)).send(any());
  }
}
