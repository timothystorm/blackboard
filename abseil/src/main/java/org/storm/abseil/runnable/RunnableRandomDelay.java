package org.storm.abseil.runnable;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class RunnableRandomDelay extends RunnableDecorator {
  private final Random   _random;
  private final Integer  _max, _min;

  public RunnableRandomDelay(Runnable runnable, int minInclusive, int maxExclusive, TimeUnit unit) {
    super(runnable);
    _random = new Random();
    _max = (int) unit.toMillis(maxExclusive);
    _min = (int) unit.toMillis(minInclusive);
  }

  @Override
  public void run() {
    try {
      Thread.sleep((_random.nextInt(_max - _min + 1) + _min));
      runDecorated();
    } catch (InterruptedException ignore) {}
  }
}
