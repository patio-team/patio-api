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
package dwbh.api.util;

import static dwbh.api.util.Check.checkIsTrue;
import static dwbh.api.util.Check.checkWith;
import static dwbh.api.util.ErrorConstants.NOT_ALLOWED;
import static dwbh.api.util.FunctionHelper.cache;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Tests {@link FunctionHelper}
 *
 * @since 0.1.0
 */
public class FunctionHelperTests {

  static class MockServiceInput {
    private final transient String word;

    MockServiceInput(String word) {
      this.word = word;
    }

    public String getWord() {
      return this.word;
    }
  }

  static class MockService {

    public String convertToUpperCase(MockServiceInput input) {
      return input.word.toUpperCase(Locale.ENGLISH);
    }
  }

  @Test
  void testSameInputEqualsSameOutput() {
    // given: some mocked service with some input
    MockService service = Mockito.mock(MockService.class);
    Mockito.when(service.convertToUpperCase(any())).thenReturn("FRIEND");

    MockServiceInput input = new MockServiceInput("friend");

    // and: caching some function locally
    Function<MockServiceInput, String> func = cache(service::convertToUpperCase);

    var result1 = func.apply(input);
    var result2 = func.apply(input);

    // then: calling the function twice with same input should have the same result
    assertEquals(result1, result2);

    // and: the function should be called only once
    assertNull(verify(service, times(1)).convertToUpperCase(any()));
  }

  @Test
  void testDifferentInputEqualsDifferentOutput() {
    // given: some mocked service
    MockService service = new MockService();

    MockServiceInput input1 = new MockServiceInput("friend");
    MockServiceInput input2 = new MockServiceInput("enemy");

    // and: caching a function
    Function<MockServiceInput, String> func = cache(service::convertToUpperCase);

    // when: calling the function with different input
    String result1 = func.apply(input1);
    String result2 = func.apply(input2);

    // then: should return in different results
    assertNotEquals(result1, result2);
  }

  @Test
  void testCheckerUse() {
    // given: some input
    MockServiceInput input = new MockServiceInput("friend");

    // and: mocking service to being able to count how many times is called
    MockService service = Mockito.mock(MockService.class);
    Mockito.when(service.convertToUpperCase(any())).thenReturn("FRIEND");

    // and: caching the function we'd like to pass to all checkers
    Function<MockServiceInput, String> cached = cache(service::convertToUpperCase);

    // when: applying it to all checkers
    Optional<Result<String>> partial =
        checkWith(input, List.of(checkFirst(cached), checkLast(cached)));

    Result<String> result = partial.orElseGet(() -> Result.result("SUCCESS"));

    // then: the result should be the expected
    assertEquals("SUCCESS", result.getSuccess());

    // and: the function should be called only once
    assertNull(verify(service, times(1)).convertToUpperCase(any()));
  }

  private Function<MockServiceInput, Check> checkFirst(Function<MockServiceInput, String> func) {
    return (i) -> checkIsTrue(func.apply(i).equals("FRIEND"), NOT_ALLOWED);
  }

  private Function<MockServiceInput, Check> checkLast(Function<MockServiceInput, String> func) {
    return (i) -> checkIsTrue(func.apply(i).contains("RI"), NOT_ALLOWED);
  }
}
