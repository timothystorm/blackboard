package org.storm.abseil.runnable;

import java.util.concurrent.TimeUnit;

/**
 * Delays execution of decorated {@link Runnable} by the specified time.
 * 
 * @author Timothy Storm
 */
public class RunnableDelay extends RunnableDecorator {
  private final Long     _delayMillis;

  public RunnableDelay(long delay, TimeUnit unit) {
    this(() -> {}, delay, unit);
  }

  public RunnableDelay(Runnable runnable, long delay, TimeUnit unit) {
    super(runnable);
    _delayMillis = unit.toMillis(delay);
  }

  @Override
  public void run() {
    try {
      Thread.sleep(_delayMillis);
      runDecorated();
    } catch (InterruptedException ignore) {}
  }
}
