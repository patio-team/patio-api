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
package patio.infrastructure.email.services.internal;

import io.micronaut.context.annotation.Value;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import patio.infrastructure.email.domain.Email;
import patio.infrastructure.email.services.EmailService;

/**
 * Default email implementation. It just works as a placeholder when no other email service has been
 * configured
 */
@Singleton
public class DummyEmailService implements EmailService {

  private static final Logger LOG = LoggerFactory.getLogger(DummyEmailService.class);

  private final boolean emailEnabled;

  /**
   * Inits the service checking whether the emails should be sent or not
   *
   * @param emailEnabled whether emails should be sent or not
   */
  public DummyEmailService(@Value("${email.enabled}") boolean emailEnabled) {
    this.emailEnabled = emailEnabled;
  }

  @Override
  public void send(Email email) {
    if (this.emailEnabled) {
      LOG.debug("emulate sending an email");
    } else {
      LOG.debug("email service disabled");
    }
  }

  /**
   * Whether emails should be sent or not
   *
   * @return whether emails should be sent or not
   */
  public boolean isEmailEnabled() {
    return emailEnabled;
  }
}
