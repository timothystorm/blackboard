package org.storm.abseil.runnable;

import java.util.concurrent.TimeUnit;

/**
 * Delays execution of decorated {@link Runnable} by the specified time.
 * 
 * @author Timothy Storm
 */
public class RunnableDelay implements Runnable {
  private final Runnable _runnable;
  private final Long     _delayMillis;

  public RunnableDelay(long delay, TimeUnit unit) {
    this(() -> {}, delay, unit);
  }

  public RunnableDelay(Runnable runnable, long delay, TimeUnit unit) {
    _runnable = runnable;
    _delayMillis = unit.toMillis(delay);
  }

  protected Runnable decorated() {
    return _runnable;
  }

  @Override
  public void run() {
    try {
      Thread.sleep(_delayMillis);
      decorated().run();
    } catch (InterruptedException ignore) {}
  }
}
