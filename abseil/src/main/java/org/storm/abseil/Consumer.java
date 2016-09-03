package org.storm.abseil;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public abstract class Consumer<T> implements Runnable {
  private final BlockingQueue<T> _queue;
  private final Long             _timeout;

  public Consumer(BlockingQueue<T> queue, Long timeout, TimeUnit unit) {
    _queue = queue;
    _timeout = unit.toMillis(timeout);
  }

  @Override
  public void run() {
    try {
      consume(_queue.poll(_timeout, TimeUnit.MILLISECONDS));
    } catch (InterruptedException e) {
      return;
    }
  }

  abstract protected void consume(T item);
}
