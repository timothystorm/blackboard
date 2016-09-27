package org.storm.abseil;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.storm.abseil.Abseil.State;
import org.storm.abseil.runnable.DelayRunnable;

/**
 * Tests the shutdown of Abseils after a predetermined timeout period, using different Abseil instances. This test is to
 * verify the Abseil stops in a "reasonable" amount of time even if tasks are still running.
 */
@RunWith(Parameterized.class)
public class AbseilTimeoutTest extends AbseilTest {
  Abseil _abseil;

  public AbseilTimeoutTest(Abseil abseil) {
    _abseil = abseil;
  }

  @Parameterized.Parameters
  public static Collection<Abseil> abseils() {
    // setup abseils that timeout after 1 second

    return Arrays.asList(
        new Abseil[] { Abseil.fixedTaskAbseil(2, 1, TimeUnit.SECONDS), Abseil.singleTaskAbseil(1, TimeUnit.SECONDS) });
  }

  @Test
  public void timeout() throws Exception {
    // run tasks in the abseil that never end
    _abseil.process(() -> new DelayRunnable(() -> {}, Integer.MAX_VALUE, TimeUnit.SECONDS));

    // let the absail cycle up
    Thread.sleep(500);

    assertState(_abseil, State.SHUTDOWN);
  }
}
