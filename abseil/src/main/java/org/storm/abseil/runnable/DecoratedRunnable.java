package org.storm.abseil.runnable;

public abstract class DecoratedRunnable implements Runnable {
  private final Runnable _runnable;

  protected DecoratedRunnable(Runnable runnable) {
    _runnable = runnable;
  }

  protected Runnable decorated() {
    return _runnable;
  }

  protected void runDecorated() {
    _runnable.run();
  }
}
