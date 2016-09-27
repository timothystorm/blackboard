package org.storm.abseil;

import static org.junit.Assert.fail;

import java.util.concurrent.TimeUnit;

import org.storm.abseil.Abseil.State;

/**
 * Utilities to test Abseils
 */
abstract class AbseilTest {
  /**
   * asserts an absail's expected state, failing if Abseil.MAX_TERMINATE_WAIT + 1 is reached but the state was never reached.
   * 
   * @param abseil
   *          - to check state of
   * @param expectedState
   *          the final state expected
   */
  void assertState(Abseil abseil, State expectedState) throws InterruptedException {
    assertState(abseil, expectedState, (Abseil.MAX_TERMINATE_WAIT + 1), TimeUnit.SECONDS);
  }

  /**
   * asserts an absail's expected state, failing if the timeout is reached but the state was never reached.
   * 
   * @param abseil
   *          - to check state of
   * @param expectedState
   *          - the final state expected
   * @param timeoutAfter
   *          - time to wait for the state to change
   * @param unit
   *          - of timeout
   */
  void assertState(Abseil abseil, State expectedState, long timeoutAfter, TimeUnit unit) throws InterruptedException {
    State actualState = null;
    long timeoutMillis = unit.toMillis(timeoutAfter);

    long i = 0;
    do {
      actualState = abseil.getState();
      if (actualState.is(expectedState)) return;
      else Thread.sleep(1000 /* 1 second */);
      i += 1000;
    } while (i < timeoutMillis);

    // final check of abseil state
    if (actualState.is(expectedState)) return;
    else fail(String.format("Never transitioned to '%s', final state '%s'", expectedState, actualState));
  }
}
