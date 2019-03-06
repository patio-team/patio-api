package dwbh.api.graphql.instrumentation;

import graphql.execution.instrumentation.InstrumentationState;

/**
 * Represents the current state of the hierarchy of a field that could be annotated by
 * the @anonymousAllowed directive
 *
 * @since 0.1.0
 */
class AuthenticationCheckState implements InstrumentationState {
  private boolean allowed;

  /**
   * Initial state
   *
   * @param allowed if the hierarchy parent has been already allowed
   * @since 0.1.0
   */
  /* default */ AuthenticationCheckState(boolean allowed) {
    this.allowed = allowed;
  }

  /**
   * Returns whether the field hierarchy has been already allowed or not
   *
   * @return true if the field hierarchy has been already allowed, false otherwise
   * @since 0.1.0
   */
  /* default */ boolean isAllowed() {
    return allowed;
  }

  /**
   * @param allowed true if the field hierarchy has been already allowed, false otherwise
   * @since 0.1.0
   */
  /* default */ void setAllowed(boolean allowed) {
    this.allowed = allowed;
  }
}
