package org.storm.abseil.supplier;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import org.storm.abseil.runnable.DecoratedRunnable;

public class PulseSupplier extends DecoratedSupplier<Runnable> {
  private final AtomicInteger _running = new AtomicInteger();
  private final Integer       _burst;
  private AtomicBoolean       _wait    = new AtomicBoolean(false);

  public PulseSupplier(Supplier<Runnable> supplier, Integer burst) {
    super(supplier);
    _burst = burst;
  }

  public static Supplier<Runnable> decorate(Supplier<Runnable> supplier, Integer burst) {
    return new PulseSupplier(supplier, burst);
  }

  /**
   * callback to let the burst know that a runnable has completed
   */
  private synchronized void done() {
    if (_running.decrementAndGet() == 0) {
      _wait.set(false);
      notify();
    }
  }

  private synchronized Runnable syncGet() {
    // wait till the previous burst is done
    while (_wait.get()) {
      try {
        wait();
      } catch (InterruptedException e) {
        return null;
      }
    }

    // determine if the burst is still going
    if (_running.incrementAndGet() == _burst) {
      _wait.set(true);
      notify();
    }

    // decorate the runnable to notify the burst when it is done
    return new DecoratedRunnable(decoratedGet()) {
      @Override
      public void run() {
        try {
          runDecorated();
        } finally {
          // notify the burst when the runnable is done
          done();
        }
      }
    };
  }

  @Override
  public Runnable get() {
    return syncGet();
  }
}
