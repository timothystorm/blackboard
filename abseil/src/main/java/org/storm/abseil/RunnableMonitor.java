package org.storm.abseil;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.storm.abseil.utils.TimeUtils;

/**
 * Decorates a {@link Runnable} and captures execution vitals.
 */
public class RunnableMonitor {
  private final AtomicLong _active  = new AtomicLong();
  private final AtomicLong _average = new AtomicLong();
  private final AtomicLong _count   = new AtomicLong();
  private final AtomicLong _startAt = new AtomicLong();

  private static class Monitored implements Runnable {
    private final Runnable        _runnable;
    private final RunnableMonitor _observer;

    private Monitored(Runnable runnable, RunnableMonitor observer) {
      _runnable = runnable;
      _observer = observer;
    }

    @Override
    public void run() {
      final long startAt = System.currentTimeMillis();

      try {
        _runnable.run();
      } finally {
        _observer.finish(System.currentTimeMillis() - startAt);
      }
    }
  }

  /**
   * Callback to signal that a runnable has finished
   * 
   * @param runtimeMillis
   */
  private void finish(final Long runtimeMillis) {
    // calculate rolling average
    final long total = _count.get();
    _average.updateAndGet((avg) -> (avg * (total - 1) + runtimeMillis) / total);
  }

  public Runnable monitor(Runnable runnable) {
    _startAt.set(System.currentTimeMillis());
    _count.incrementAndGet();
    return new Monitored(runnable, this);
  }

  public Long getCount() {
    return _count.get();
  }

  public Long getActive() {
    return _active.get();
  }

  public Long getAverageRuntime() {
    return _average.get();
  }

  public Long getRuntime() {
    return System.currentTimeMillis() - _startAt.get();
  }

  @Override
  public String toString() {
    ToStringBuilder str = new ToStringBuilder(this, ToStringStyle.JSON_STYLE);
    str.append("count", getCount());
    str.append("active", getActive());
    str.append("average", TimeUtils.formatMillis(getAverageRuntime()));
    return str.toString();
  }
}
