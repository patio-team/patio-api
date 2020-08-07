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
package patio.user.domain;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests some functions in domain classes other than getters and setters.
 *
 * @since 0.1.0
 */
public class GroupMemberTests {

  @Test
  void testConstructor() {
    // given: some known values
    var uuid = random(UUID.class);
    var text = random(String.class);
    var user = random(User.class);
    var time = OffsetDateTime.now();

    // when: creating a new instance
    GroupMember groupMember = new GroupMember(uuid, text, text, true, true, user, time, time);

    // then: they are correctly created
    assertEquals(groupMember.getId(), uuid);
    assertEquals(groupMember.getName(), text);
    assertEquals(groupMember.getEmail(), text);
    assertEquals(groupMember.getRegistrationPending(), true);
    assertEquals(groupMember.getAcceptancePending(), true);
    assertEquals(groupMember.getInvitedBy(), user);
    assertEquals(groupMember.getOtpCreationDateTime(), time);
    assertEquals(groupMember.getMemberFromDateTime(), time);
  }

  @Test
  void testGettersSetters() {
    // given: some known values
    var uuid = random(UUID.class);
    var text = random(String.class);
    var user = random(User.class);
    var time = OffsetDateTime.now();

    // when: establishing its parameters
    GroupMember groupMember = random(GroupMember.class);
    groupMember.setName(text);
    groupMember.setId(uuid);
    groupMember.setEmail(text);
    groupMember.setRegistrationPending(true);
    groupMember.setAcceptancePending(true);
    groupMember.setInvitedBy(user);
    groupMember.setOtpCreationDateTime(time);
    groupMember.setMemberFromDateTime(time);

    // then: they are correctly recovered
    assertEquals(groupMember.getId(), uuid);
    assertEquals(groupMember.getName(), text);
    assertEquals(groupMember.getEmail(), text);
    assertEquals(groupMember.getRegistrationPending(), true);
    assertEquals(groupMember.getAcceptancePending(), true);
    assertEquals(groupMember.getInvitedBy(), user);
    assertEquals(groupMember.getOtpCreationDateTime(), time);
    assertEquals(groupMember.getMemberFromDateTime(), time);
  }

  @ParameterizedTest(name = "Test getting hash emails: email [{0}]")
  @ValueSource(strings = {"somebody@email.com", "SOMEBODY@email.com", "somebody@EMAIL.com"})
  void testGetHash(String email) {
    // given: a user with email
    GroupMember groupMember = random(GroupMember.class);
    groupMember.setEmail(email);

    // when: getting user's hash
    String hash = groupMember.getHash();

    // then: it should match the provided value
    assertEquals(hash, "66b0ef1ce525f909ad733d06415331d5");
  }
}
