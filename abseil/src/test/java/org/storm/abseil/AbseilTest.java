package org.storm.abseil;

import static org.junit.Assert.fail;

import java.util.concurrent.TimeUnit;

import org.junit.Ignore;
import org.junit.Test;
import org.storm.abseil.Abseil.State;
import org.storm.abseil.runnable.DelayRunnable;

public class AbseilTest {

  @Test(timeout = 5000)
  public void shutdownNow_fixed_tasks() throws Exception {
    Abseil abseil = AbseilBuilder.newFixedTaskAbseilBuilder(Integer.MAX_VALUE, TimeUnit.MINUTES).build();
    abseil.process(() -> new DelayRunnable(Integer.MAX_VALUE, TimeUnit.SECONDS));

    // let the absail cycle up
    Thread.sleep(1000);

    // create background thread to kill abseil
    new Thread(() -> {
      abseil.shutdownNow();
    }).start();

    assertState(abseil, State.SHUTDOWN, 3, TimeUnit.SECONDS);
  }

  @Test(timeout = 5000)
  public void shutdownNow_pooled_tasks() throws Exception {
    Abseil abseil = AbseilBuilder.newFixedTaskAbseilBuilder(Integer.MAX_VALUE, TimeUnit.MINUTES).build();
    abseil.process(() -> new DelayRunnable(Integer.MAX_VALUE, TimeUnit.SECONDS));

    // let the absail cycle up
    Thread.sleep(1000);

    // create background thread to kill abseil
    new Thread(() -> {
      abseil.shutdownNow();
    }).start();

    assertState(abseil, State.SHUTDOWN, 3, TimeUnit.SECONDS);
  }

  @Test
  @Ignore("Only used for debugging by keeping the Abseil going indefinitely")
  public void run_forever() throws Exception {
    Abseil abseil = AbseilBuilder.newFixedTaskAbseilBuilder(Integer.MAX_VALUE, TimeUnit.SECONDS).tasks(3).build();
    abseil.process(() -> new DelayRunnable(Integer.MAX_VALUE, TimeUnit.SECONDS));

    // keep test running
    Thread.sleep(Integer.MAX_VALUE);
  }

  /**
   * Abseil should be stoppable even when processes are hung
   */
  @Test
  public void shutdown_fixed_tasks() throws Exception {
    Abseil abseil = AbseilBuilder.newFixedTaskAbseilBuilder(Integer.MAX_VALUE, TimeUnit.MINUTES).tasks(3).build();
    abseil.process(() -> new DelayRunnable(Integer.MAX_VALUE, TimeUnit.SECONDS));

    // let the absail cycle up
    Thread.sleep(1000);

    // create background thread to stop abseil
    new Thread(() -> {
      abseil.shutdown();
    }).start();

    assertState(abseil, State.SHUTDOWN, 3, TimeUnit.SECONDS);
  }

  /**
   * Abseil should be stoppable even when processes are hung
   */
  @Test
  public void shutdown_pooled_tasks() throws Exception {
    Abseil abseil = AbseilBuilder.newPooledTaskAbseilBuilder(Integer.MAX_VALUE, TimeUnit.MINUTES).build();
    abseil.process(() -> new DelayRunnable(100, TimeUnit.MILLISECONDS));

    // let the absail cycle up
    Thread.sleep(1000);

    // create background thread to stop abseil
    new Thread(() -> {
      abseil.shutdown();
    }).start();

    assertState(abseil, State.SHUTDOWN, 3, TimeUnit.SECONDS);
  }

  /**
   * Abseil should timeout as expected
   */
  @Test(timeout = 5000)
  public void timeout_fixed_tasks() throws Exception {
    Abseil abseil = AbseilBuilder.newFixedTaskAbseilBuilder(1, TimeUnit.SECONDS).build();
    abseil.process(() -> new DelayRunnable(100, TimeUnit.MILLISECONDS));

    // let the absail cycle up
    Thread.sleep(1000);

    assertState(abseil, State.SHUTDOWN, 3, TimeUnit.SECONDS);
  }

  /**
   * Abseil should timeout as expected
   */
  @Test(timeout = 5000)
  public void timeout_pooled_tasks() throws Exception {
    Abseil abseil = AbseilBuilder.newPooledTaskAbseilBuilder(100, TimeUnit.MILLISECONDS).build();
    abseil.process(() -> new DelayRunnable(100, TimeUnit.MILLISECONDS));

    // let the absail cycle up
    Thread.sleep(1000);

    assertState(abseil, State.SHUTDOWN, 0, TimeUnit.SECONDS);
  }

  /**
   * asserts an absail's expected state, failing if the timeout is reached but the state was never reached.
   * 
   * @param abseil
   * @param expectedState
   * @param waitFor
   * @param unit
   */
  private void assertState(Abseil abseil, State expectedState, long timeoutAfter, TimeUnit unit)
      throws InterruptedException {
    State actualState = null;
    long timeoutMillis = unit.toMillis(timeoutAfter);

    long i = 0;
    do {
      actualState = abseil.getState();
      if (actualState.is(expectedState)) return;
      else Thread.sleep(1000 /* 1 second */);
      i++;
    } while (i < timeoutMillis);

    fail(String.format("Never transitioned to '%s', final state '%s'", expectedState, actualState));
  }
}
