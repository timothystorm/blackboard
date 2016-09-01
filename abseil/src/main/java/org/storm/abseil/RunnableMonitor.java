package org.storm.abseil;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Decorates a {@link Runnable} and captures execution vitals.
 */
class RunnableMonitor implements Runnable {
  /**
   * Optional callback to get details once this {@link Runnable} terminates.
   */
  interface Listener {
    void accept(RunnableMonitor timedRunnable);
  }

  private static volatile AtomicLong _active  = new AtomicLong();
  private static volatile AtomicLong _average = new AtomicLong();
  private static volatile AtomicLong _count   = new AtomicLong();
  private final AtomicLong           _endAt   = new AtomicLong();
  private final Listener             _listener;
  private final Runnable             _run;
  private final AtomicLong           _startAt = new AtomicLong();

  RunnableMonitor(Runnable run) {
    this(run, null);
  }

  RunnableMonitor(Runnable run, Listener listener) {
    _run = run;
    _listener = listener;
  }

  Long getAverageRuntime() {
    return _average.get();
  }

  private void after() {
    try {
      _endAt.set(System.currentTimeMillis());
      _active.decrementAndGet();

      // calculate moving average
      _average.updateAndGet((avg) -> {
        long count = getTotalCount();
        return (avg * (count - 1) + getRuntime()) / count;
      });
    } catch (Exception ignore) {} finally {
      if(_listener != null) _listener.accept(this);
    }
  }

  private void before() {
    _startAt.set(System.currentTimeMillis());
    _count.incrementAndGet();
    _active.incrementAndGet();
  }

  Long getActiveCount() {
    return _active.get();
  }

  Long getTotalCount() {
    return _count.get();
  }

  Long getRuntime() {
    return _endAt.get() - _startAt.get();
  }

  Long getEndAt() {
    return _endAt.get();
  }

  Long getStartAt() {
    return _startAt.get();
  }

  @Override
  public void run() {
    try {
      before();
      _run.run();
    } finally {
      after();
    }
  }
}
