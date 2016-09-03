package org.storm.abseil;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.TimeUnit;

/**
 * Builds different Abseil strategies with logical defaults but can still allows additional configuration by the client.
 * This simplifies the creation of {@link ExecutorService}s by using logical and common defaults.
 * 
 * @author Timothy Storm
 */
public abstract class AbseilBuilder {
  /**
   * Creates a fixed {@link Abseil} which executes a fixed set of tasks. The number of tasks being executed
   * never exceeds the max task boundary.
   */
  public static class FixedTaskAbseilBuilder extends AbseilBuilder {
    private static final Long        DEFAULT_KEEP_ALIVE = TimeUnit.SECONDS.toMillis(1);
    private static final Integer     DEFAULT_TASKS      = 10;
    private RejectedExecutionHandler _handler;
    private Long                     _keepAlive;
    private BlockingQueue<Runnable>  _queue;
    private Integer                  _tasks;

    public FixedTaskAbseilBuilder(long maxRuntime, TimeUnit unit) {
      super(maxRuntime, unit);
    }

    public FixedTaskAbseilBuilder keepAlive(Long time, TimeUnit unit) {
      assert time >= 0;

      _keepAlive = unit.toMillis(time);
      return this;
    }

    @Override
    public ExecutorService newExecutorService() {
      Integer tasks = _tasks == null ? DEFAULT_TASKS : _tasks;
      Long keepAlive = _keepAlive == null ? DEFAULT_KEEP_ALIVE : _keepAlive;
      BlockingQueue<Runnable> queue = _queue == null ? new LinkedBlockingQueue<Runnable>(tasks) : _queue;
      RejectedExecutionHandler handler = _handler == null ? DEFAULT_HANDLER : _handler;

      return new ThreadPoolExecutor(tasks, tasks, keepAlive, TimeUnit.MILLISECONDS, queue, handler);
    }

    public FixedTaskAbseilBuilder queue(BlockingQueue<Runnable> queue) {
      _queue = queue;
      return this;
    }

    public FixedTaskAbseilBuilder rejectHandler(RejectedExecutionHandler handler) {
      _handler = handler;
      return this;
    }

    public FixedTaskAbseilBuilder tasks(int tasks) {
      assert tasks > 0;

      _tasks = tasks;
      return this;
    }
  }

  /**
   * Creates a pooled {@link Abseil} which executes tasks from a size bound pool. The pool sizes dynamically
   * within the min/max task bounds.
   */
  public static class PooledTaskAbseilBuilder extends AbseilBuilder {
    private static final Long        DEFAULT_KEEP_ALIVE = TimeUnit.SECONDS.toMillis(2);
    private static final Integer     DEFAULT_MAX_TASKS  = 10;
    private static final Integer     DEFAULT_MIN_TASKS  = 0;
    private RejectedExecutionHandler _handler;
    private Long                     _keepAlive;
    private Integer                  _maxTasks, _minTasks;
    private BlockingQueue<Runnable>  _queue;

    public PooledTaskAbseilBuilder(long maxRuntime, TimeUnit unit) {
      super(maxRuntime, unit);
    }

    public PooledTaskAbseilBuilder keepAlive(Long keepAlive, TimeUnit unit) {
      _keepAlive = unit.toMillis(keepAlive);
      return this;
    }

    public PooledTaskAbseilBuilder maxTasks(int maxTasks) {
      assert maxTasks > 0;

      _maxTasks = maxTasks;
      return this;
    }

    public PooledTaskAbseilBuilder minTasks(int minTasks) {
      assert minTasks >= 0;
      _minTasks = minTasks;
      return this;
    }

    @Override
    public ExecutorService newExecutorService() {
      Integer minThread = _minTasks == null ? DEFAULT_MIN_TASKS : _minTasks;
      Integer maxThread = _maxTasks == null ? DEFAULT_MAX_TASKS : _maxTasks;
      Long keepAlive = _keepAlive == null ? DEFAULT_KEEP_ALIVE : _keepAlive;
      BlockingQueue<Runnable> queue = _queue == null ? new SynchronousQueue<Runnable>() : _queue;
      RejectedExecutionHandler handler = _handler == null ? DEFAULT_HANDLER : _handler;

      // build configured executor
      return new ThreadPoolExecutor(minThread, maxThread, keepAlive, TimeUnit.MILLISECONDS, queue, handler);
    }

    public PooledTaskAbseilBuilder queue(BlockingQueue<Runnable> queue) {
      _queue = queue;
      return this;
    }

    public PooledTaskAbseilBuilder rejectHandler(RejectedExecutionHandler handler) {
      _handler = handler;
      return this;
    }
  }

  /**
   * Creates a pooled {@link Abseil} which executes tasks one at a time. Not all that useful since this is how things
   * usually work anyway but it useful for testing or when you just need a task executed on a separate thread.
   */
  public static class SingleTaskAbseilBuilder extends AbseilBuilder {
    private RejectedExecutionHandler _handler;
    private Long                     _keepAlive;

    private BlockingQueue<Runnable>  _queue;

    public SingleTaskAbseilBuilder(long maxRuntime, TimeUnit unit) {
      super(maxRuntime, unit);
    }

    public SingleTaskAbseilBuilder keepAlive(Long time, TimeUnit unit) {
      assert time >= 0;

      _keepAlive = unit.toMillis(time);
      return this;
    }

    @Override
    public ExecutorService newExecutorService() {
      Long keepAlive = _keepAlive == null ? TimeUnit.SECONDS.toMillis(1) : _keepAlive;
      BlockingQueue<Runnable> queue = _queue == null ? new LinkedBlockingQueue<Runnable>(1) : _queue;
      RejectedExecutionHandler handler = _handler == null ? DEFAULT_HANDLER : _handler;

      return new ThreadPoolExecutor(1, 1, keepAlive, TimeUnit.MILLISECONDS, queue, handler);
    }

    public SingleTaskAbseilBuilder queue(BlockingQueue<Runnable> queue) {
      _queue = queue;
      return this;
    }

    public SingleTaskAbseilBuilder rejectHandler(RejectedExecutionHandler handler) {
      _handler = handler;
      return this;
    }
  }

  /** This handler will run the task unless the handler has been shut down */
  private static final RejectedExecutionHandler DEFAULT_HANDLER = new CallerRunsPolicy();

  /**
   * Builder that creates an {@link Abseil} which executes fixed set of tasks and has no timeout.
   * 
   * @return {@link FixedTaskAbseilBuilder}
   */
  public static FixedTaskAbseilBuilder newFixedTaskAbseilBuilder() {
    return new FixedTaskAbseilBuilder(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
  }

  /**
   * Builder that creates an {@link Abseil} which executes a fixed set of tasks at a time and times out after the
   * specified
   * period.
   * 
   * @param maxRuntime
   *          - the {@link Abseil} will run till it times out
   * @param unit
   *          - of maxRuntime
   * @return {@link PooledTaskAbseilBuilder}
   */
  public static FixedTaskAbseilBuilder newFixedTaskAbseilBuilder(long maxRuntime, TimeUnit unit) {
    return new FixedTaskAbseilBuilder(maxRuntime, unit);
  }

  /**
   * Builder that creates an {@link Abseil} which executes tasks from a size bound pool and has no timeout.
   * 
   * @return {@link PooledTaskAbseilBuilder}
   */
  public static PooledTaskAbseilBuilder newPooledTaskAbseilBuilder() {
    return new PooledTaskAbseilBuilder(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
  }

  /**
   * Builder that creates an {@link Abseil} which executes tasks from a size bound pool and times out after the
   * specified
   * period.
   * 
   * @param maxRuntime
   *          - the {@link Abseil} will run till it times out
   * @param unit
   *          - of maxRuntime
   * @return {@link PooledTaskAbseilBuilder}
   */
  public static PooledTaskAbseilBuilder newPooledTaskAbseilBuilder(long maxRuntime, TimeUnit unit) {
    return new PooledTaskAbseilBuilder(maxRuntime, unit);
  }

  /**
   * Builder that creates an {@link Abseil} which executes tasks one at a time and has no timeout.
   * 
   * @return {@link SingleTaskAbseilBuilder}
   */
  public static SingleTaskAbseilBuilder newSingleTaskAbseilBuilder() {
    return newSingleTaskAbseilBuilder(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
  }

  /**
   * Builder that creates an {@link Abseil} which executes tasks one at a time and times out after the specified
   * period.
   * 
   * @param maxRuntime
   *          - the {@link Abseil} will run till it times out
   * @param unit
   *          - of maxRuntime
   * @return {@link SingleTaskAbseilBuilder}
   */
  public static SingleTaskAbseilBuilder newSingleTaskAbseilBuilder(long maxRuntime, TimeUnit unit) {
    return new SingleTaskAbseilBuilder(maxRuntime, unit);
  }

  private final Long _maxRuntimeMillis;

  protected AbseilBuilder(long maxRuntime, TimeUnit unit) {
    assert maxRuntime > 0;

    _maxRuntimeMillis = unit.toMillis(maxRuntime);
  }

  /**
   * Build the configured {@link Abseil}
   * 
   * @return
   */
  public Abseil build() {
    return new Abseil(this);
  }

  /**
   * Maximum running time an {@link Abseil} should be allowed to run before timing out and shutting down. Defaults to
   * Long.MAX_VALUE.
   * 
   * @return maximum running time in millis
   */
  public Long getMaxRuntime() {
    return _maxRuntimeMillis == null ? Long.MAX_VALUE : _maxRuntimeMillis;
  }

  /**
   * @return an {@link ExecutorService} to be used by the {@link Abseil} to run tasks.
   */
  abstract public ExecutorService newExecutorService();
}
