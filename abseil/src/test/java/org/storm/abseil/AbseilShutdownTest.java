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
 * Tests the client shutdown of Abseils using different Abseil instances. A background thread is used to stop the Abseil
 * because the shutdown blocks and if there are problems, could run forever. This test is to verify the Abseil
 * stops in a "reasonable" amount of time even if tasks are still running.
 */
@RunWith(Parameterized.class)
public class AbseilShutdownTest extends AbseilTest {
  Abseil _abseil;

  public AbseilShutdownTest(Abseil abseil) {
    _abseil = abseil;
  }

  @Parameterized.Parameters
  public static Collection<Abseil> abseils() {
    // setup abseils that run "forever"
    return Arrays.asList(new Abseil[] { Abseil.fixedTaskAbseil(3, Integer.MAX_VALUE, TimeUnit.MINUTES),
        Abseil.singleTaskAbseil(Integer.MAX_VALUE, TimeUnit.MINUTES) });
  }

  /**
   * Abseil should be externally stoppable even when processes are still running
   */
  @Test(timeout = 5000)
  public void shutdown() throws Exception {
    // run tasks in the abseil that never end
    _abseil.process(() -> new DelayRunnable(() -> {}, Integer.MAX_VALUE, TimeUnit.SECONDS));

    // let the absail cycle up
    Thread.sleep(500);

    // create background thread to stop abseil
    new Thread(() -> {
      _abseil.shutdown();
    }).start();

    // verify the abseil shuts down in a reasonable amount of time.
    assertState(_abseil, State.SHUTDOWN);
  }
}
