package org.storm.abseil;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.storm.abseil.Abseil.State;
import org.storm.abseil.runnable.RunnableDelay;

@RunWith(Parameterized.class)
public class AbseilFinishTest extends AbseilTest {
  Abseil _abseil;

  public AbseilFinishTest(Abseil abseil) {
    _abseil = abseil;
  }

  @Parameterized.Parameters
  public static Collection<Abseil> abseils() {
    // setup abseils that run "forever"
    return Arrays.asList(new Abseil[] { Abseil.fixedTaskAbseil(3, Integer.MAX_VALUE, TimeUnit.MINUTES),
        Abseil.singleTaskAbseil(Integer.MAX_VALUE, TimeUnit.MINUTES) });
  }

  @Test
  public void timeout() throws Exception {
    final AtomicInteger count = new AtomicInteger(3);

    // run tasks in the abseil that end after a fixed number of tasks have been run
    _abseil.process(() -> {
      if (count.decrementAndGet() > 0) return new RunnableDelay(1, TimeUnit.SECONDS);
      return null;
    });

    // give the abseil a moment to cycle up
    Thread.sleep(500);

    assertState(_abseil, State.SHUTDOWN);
  }
}
