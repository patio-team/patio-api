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
package dwbh.api.services.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link AlgorithmFactory}
 *
 * @since 0.1.0
 */
public class AlgorithmFactoryTests {

  @Test
  void testCreateAlgorithm() throws Exception {
    // when: creating a new algorithm instance
    var factory = new AlgorithmFactory();
    var algorithm = factory.create("secret", "HS256");

    // then: we should expect a new instance
    assertNotNull(algorithm);

    // and: we should expect it to be a certain algorithm type
    assertEquals("HS256", algorithm.getName());
  }
}
