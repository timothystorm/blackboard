package org.storm.abseil;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.TimeUnit;

/**
 * Builds {@link Abseil}s with logical defaults but can be customized to a clients requirements.
 * 
 * @author Timothy Storm
 */
public class AbseilBuilder {

  private ThreadFactory            _factory;
  private RejectedExecutionHandler _handler;
  private Long                     _keepAliveMillis;
  private Long                     _maxRuntimeMillis;
  private Integer                  _minThreads, _maxThreads;
  private BlockingQueue<Runnable>  _queue;

  public AbseilBuilder() {}

  public Abseil build() {
    return new Abseil(this);
  }

  ExecutorService getExecutorService() {
    // capture and set executor attributes
    int minThreads = (_minThreads == null || _minThreads <= 0) ? 1 : _minThreads;
    int maxThreads = (_maxThreads == null || _maxThreads <= 0) ? 1 : _maxThreads;
    long keepAliveMillis = (_keepAliveMillis == null || _keepAliveMillis <= 0) ? TimeUnit.SECONDS.toMillis(1)
        : _keepAliveMillis;
    BlockingQueue<Runnable> queue = (_queue == null) ? new LinkedBlockingQueue<>(maxThreads) : _queue;
    ThreadFactory factory = (_factory == null) ? ((r) -> new Thread(r, "Abseil - task")) : _factory;
    RejectedExecutionHandler handler = (_handler == null) ? new CallerRunsPolicy() : _handler;

    return new ThreadPoolExecutor(minThreads, maxThreads, keepAliveMillis, TimeUnit.MILLISECONDS, queue, factory,
        handler);
  }

  long getMaxRuntimeMillis() {
    return _maxRuntimeMillis == null ? Integer.MAX_VALUE : _maxRuntimeMillis;
  }

  /**
   * Duration a thread should be idle before being removed from the pool.
   * 
   * @param time
   * @param unit
   * @return
   */
  public AbseilBuilder keepAlive(long time, TimeUnit unit) {
    _keepAliveMillis = unit.toMillis(time);
    return this;
  }

  /**
   * Total time the Abseil should run before shutting down gracefully.
   * 
   * @param time
   * @param unit
   * @return
   */
  public AbseilBuilder maxRuntime(long time, TimeUnit unit) {
    _maxRuntimeMillis = unit.toMillis(time);
    return this;
  }

  public AbseilBuilder maxThreads(int maxThreads) {
    _maxThreads = maxThreads;
    return this;
  }

  public AbseilBuilder minThreads(int minThreads) {
    _minThreads = minThreads;
    return this;
  }

  public AbseilBuilder rejectedExecutionHandler(RejectedExecutionHandler handler) {
    _handler = handler;
    return this;
  }

  public AbseilBuilder threadFactory(ThreadFactory factory) {
    _factory = factory;
    return this;
  }

  public AbseilBuilder workQueue(BlockingQueue<Runnable> queue) {
    _queue = queue;
    return this;
  }
}
