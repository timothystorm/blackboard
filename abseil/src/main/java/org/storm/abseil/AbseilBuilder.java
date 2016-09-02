package org.storm.abseil;

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
    private Integer _tasks;

    public FixedTaskAbseilBuilder(long maxRuntime, TimeUnit unit) {
      super(maxRuntime, unit);
    }

    @Override
    public ExecutorService getExecutorService() {
      // Copied from Executors.newFixedThreadPool() but allows the user to customize the task count
      int taskCount = _tasks == null ? DEFAULT_MAX_TASK_COUNT : _tasks;
      return new ThreadPoolExecutor(taskCount, taskCount, 0L, TimeUnit.MILLISECONDS,
          new LinkedBlockingQueue<Runnable>(taskCount), DEFAULT_HANDLER);
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
    private Long    _keepAlive;
    private Integer _maxTasks, _minTasks;

    public PooledTaskAbseilBuilder(long maxRuntime, TimeUnit unit) {
      super(maxRuntime, unit);
    }

    @Override
    public ExecutorService getExecutorService() {
      // Copied from Executors.newCachedThreadPool() but allows the user to customize the min/max task bounds
      return new ThreadPoolExecutor((_minTasks == null ? 0 : _minTasks),
          (_maxTasks == null ? DEFAULT_MAX_TASK_COUNT : _maxTasks), (_keepAlive == null ? 2000 : _keepAlive),
          TimeUnit.MILLISECONDS, new SynchronousQueue<Runnable>(), DEFAULT_HANDLER);
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
  }

  /**
   * Creates a pooled {@link Abseil} which executes tasks one at a time. Not all that useful since this is how things
   * usually work anyway but it useful for testing or when you just need a task executed on a separate thread.
   */
  public static class SingleTaskAbseilBuilder extends AbseilBuilder {

    public SingleTaskAbseilBuilder(long maxRuntime, TimeUnit unit) {
      super(maxRuntime, unit);
    }

    @Override
    public ExecutorService getExecutorService() {
      return new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(1),
          DEFAULT_HANDLER);
    }
  }

  /** This handler will run the task unless the handler has been shut down */
  private static final RejectedExecutionHandler DEFAULT_HANDLER        = new CallerRunsPolicy();

  /** A reasonable default, wouldn't you say? */
  private static final Integer                  DEFAULT_MAX_TASK_COUNT = 20;

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

  AbseilBuilder(long maxRuntime, TimeUnit unit) {
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
   * @return an {@link ExecutorService} to be used by the {@link Abseil} to run tasks.
   */
  abstract public ExecutorService getExecutorService();

  /**
   * Maximum running time an {@link Abseil} should be allowed to run before timing out and shutting down. Defaults to
   * Long.MAX_VALUE.
   * 
   * @return maximum running time
   */
  Long getMaxRuntimeMillis() {
    return _maxRuntimeMillis == null ? Long.MAX_VALUE : _maxRuntimeMillis;
  }
}
