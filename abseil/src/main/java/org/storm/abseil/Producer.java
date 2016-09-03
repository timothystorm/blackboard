package org.storm.abseil;

import java.util.concurrent.BlockingQueue;

public abstract class Producer<T> implements Runnable {
  private final Boolean          _stopOnError;
  private final BlockingQueue<T> _producerQueue;

  public Producer(BlockingQueue<T> producerQueue) {
    this(producerQueue, true);
  }

  public Producer(BlockingQueue<T> queue, boolean stopOnError) {
    _producerQueue = queue;
    _stopOnError = stopOnError;
  }

  @Override
  public void run() {
    T item = null;
    while ((item = produce()) != null) {
      try {
        _producerQueue.offer(item);
      } catch (Exception e) {
        if (_stopOnError) return;
      }
    }
  }

  abstract protected T produce();
}
