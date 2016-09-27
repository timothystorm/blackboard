package org.storm.abseil.runnable;

import org.storm.abseil.Monitor;

/**
 * Decorates a {@link Runnable} with a {@link Monitor} for capturing the life cycle of the decorated runnable.
 * 
 * @author Timothy Storm
 */
public class RunnableMonitor implements Runnable {
  private final Monitor  _monitor;
  private final Runnable _runnable;

  public RunnableMonitor(Runnable runnable, Monitor monitor) {
    _monitor = monitor;
    _runnable = runnable;
  }

  protected Runnable decorated() {
    return _runnable;
  }

  @Override
  public void run() {
    try {
      _monitor.start();
      decorated().run();
      _monitor.success();
    } catch (Throwable error) {
      _monitor.fail(error);
      throw error;
    } finally {
      _monitor.stop();
    }
  }
}
