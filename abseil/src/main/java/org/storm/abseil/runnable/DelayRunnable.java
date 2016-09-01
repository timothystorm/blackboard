package org.storm.abseil.runnable;

import java.util.concurrent.TimeUnit;

/**
 * Delays execution of decorated {@link Runnable} by the specified time.
 * 
 * @author Timothy Storm
 */
public class DelayRunnable implements Runnable {
  private final Runnable _run;
  private final Long     _delayMillis;

  public DelayRunnable(long delay, TimeUnit unit) {
    this(() -> {}, delay, unit);
  }

  public DelayRunnable(Runnable run, long delay, TimeUnit unit) {
    _run = run;
    _delayMillis = unit.toMillis(delay);
  }

  @Override
  public void run() {
    try {
      Thread.sleep(_delayMillis);
      _run.run();
    } catch (InterruptedException ignore_and_return) {
      return;
    }
  }
}
