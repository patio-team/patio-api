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
  void testCreateAlgorithm() {
    // when: creating a new algorithm instance
    var factory = new AlgorithmFactory();
    var algorithm = factory.create("secret");

    // then: we should expect a new instance
    assertNotNull(algorithm);

    // and: we should expect it to be a certain algorithm type
    assertEquals("HS256", algorithm.getName());
  }
}
