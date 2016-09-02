package org.storm.abseil;

import static org.storm.abseil.AbseilBuilder.newFixedTaskAbseilBuilder;
import static org.storm.abseil.AbseilBuilder.newPooledTaskAbseilBuilder;
import static org.storm.abseil.AbseilBuilder.newSingleTaskAbseilBuilder;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.storm.abseil.Abseil.State;
import org.storm.abseil.runnable.DelayRunnable;
import org.storm.abseil.runnable.RunnableFactory;

@RunWith(Parameterized.class)
public class AbseilFinishTest extends AbseilTest {
  Abseil _abseil;

  public AbseilFinishTest(Abseil abseil) {
    _abseil = abseil;
  }

  @Parameterized.Parameters
  public static Collection<Abseil> abseils() {
    // setup abseils that run "forever"
    return Arrays.asList(new Abseil[] { newFixedTaskAbseilBuilder(Integer.MAX_VALUE, TimeUnit.MINUTES).build(),
        newPooledTaskAbseilBuilder(Integer.MAX_VALUE, TimeUnit.MINUTES).build(),
        newSingleTaskAbseilBuilder(Integer.MAX_VALUE, TimeUnit.MINUTES).build() });
  }

  @Test(timeout = 5000)
  public void timeout() throws Exception {
    final AtomicInteger count = new AtomicInteger(3);

    // run tasks in the abseil that end after a fixed number of tasks have been run
    _abseil.process(new RunnableFactory() {
      public Runnable build() {
        if (count.decrementAndGet() > 0) return new DelayRunnable(1, TimeUnit.SECONDS);
        return null;
      }
    });

    // let the absail cycle up
    Thread.sleep(500);

    assertState(_abseil, State.SHUTDOWN);
  }
}
