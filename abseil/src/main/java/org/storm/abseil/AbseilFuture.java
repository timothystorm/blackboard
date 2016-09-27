package org.storm.abseil;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.storm.abseil.Abseil.State;

class AbseilFuture implements Future<Monitor> {
  private final Abseil                 _abseil;
  private final BlockingQueue<Monitor> _reply = new ArrayBlockingQueue<>(1);

  AbseilFuture(Abseil abseil) {
    if (abseil == null) throw new NullPointerException();
    _abseil = abseil;
  }

  @Override
  public boolean cancel(boolean mayInterruptIfRunning) {
    return _abseil.shutdown();
  }

  @Override
  public boolean isCancelled() {
    return isDone();
  }

  @Override
  public boolean isDone() {
    return _abseil.getState().is(State.SHUTDOWN);
  }

  @Override
  public Monitor get() throws InterruptedException, ExecutionException {
    return _reply.take();
  }

  @Override
  public Monitor get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
    final Monitor monitor = _reply.poll(timeout, unit);
    if (monitor == null) throw new TimeoutException();
    return monitor;
  }

  void onComplete(Monitor monitor) {
    try {
      _reply.put(monitor);
    } catch (InterruptedException e) {
      _reply.offer(null);
    }
  }
}
