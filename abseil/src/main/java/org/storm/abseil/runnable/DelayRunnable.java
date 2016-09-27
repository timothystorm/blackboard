package org.storm.abseil.runnable;

import java.util.concurrent.TimeUnit;

import org.storm.abseil.utils.Sleep;

public class DelayRunnable extends DecoratedRunnable {
  private final Long _max, _min;
  
  public DelayRunnable(Runnable runnable, long max, TimeUnit unit){
    this(runnable, 0, max, unit);
  }

  public DelayRunnable(Runnable runnable, long minInclusive, long maxExclusive, TimeUnit unit) {
    super(runnable);
    _max = unit.toMillis(maxExclusive);
    _min = unit.toMillis(minInclusive);
  }

  @Override
  public void run() {
    if(Sleep.random(_min, _max, TimeUnit.MILLISECONDS)) runDecorated();
  }
}
