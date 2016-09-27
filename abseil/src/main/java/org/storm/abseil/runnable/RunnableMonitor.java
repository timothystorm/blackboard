package org.storm.abseil.runnable;

import org.storm.abseil.Monitor;

/**
 * Decorates a {@link Runnable} with a {@link Monitor} for capturing the life cycle of the decorated runnable.
 * 
 * @author Timothy Storm
 */
public class RunnableMonitor extends RunnableDecorator {
  private final Monitor _monitor;

  public RunnableMonitor(Runnable runnable, Monitor monitor) {
    super(runnable);
    _monitor = monitor;
  }

  @Override
  public void run() {
    try {
      _monitor.start();
      runDecorated();
      _monitor.success();
    } catch (Throwable error) {
      _monitor.fail(error);
      throw error;
    } finally {
      _monitor.stop();
    }
  }
}
