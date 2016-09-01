package org.storm.abseil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.TimeUnit;

public abstract class AbseilBuilder {
  protected Long                                _maxRuntimeMillis;

  private static final RejectedExecutionHandler DEFAULT_HANDLER = new CallerRunsPolicy();

  private AbseilBuilder() {}

  public Abseil build() {
    return new Abseil(this);
  }

  abstract ExecutorService getExecutorService();

  Long getMaxRuntimeMillis() {
    return _maxRuntimeMillis;
  }

  @SuppressWarnings("unchecked")
  protected <T extends AbseilBuilder> T maxRuntime(long maxRuntime, TimeUnit unit) {
    _maxRuntimeMillis = unit.toMillis(maxRuntime);
    return (T) this;
  }

  public static PooledTaskAbseilBuilder newPooledTaskAbseilBuilder() {
    return new PooledTaskAbseilBuilder();
  }

  public static PooledTaskAbseilBuilder newPooledTaskAbseilBuilder(long maxRuntime, TimeUnit unit) {
    return new PooledTaskAbseilBuilder().maxRuntime(maxRuntime, unit);
  }

  public static FixedTaskAbseilBuilder newFixedTaskAbseilBuilder() {
    return new FixedTaskAbseilBuilder();
  }

  public static FixedTaskAbseilBuilder newFixedTaskAbseilBuilder(long maxRuntime, TimeUnit unit) {
    return new FixedTaskAbseilBuilder().maxRuntime(maxRuntime, unit);
  }

  /**
   * Creates a fixed {@link Abseil} which executes a runnable tasks simultaneously. The number of tasks being worked
   * never exceeds the max task boundary.
   */
  public static class FixedTaskAbseilBuilder extends AbseilBuilder {
    private Integer _tasks;

    public FixedTaskAbseilBuilder tasks(int tasks) {
      assert tasks > 0;
      _tasks = tasks;
      return this;
    }

    @Override
    ExecutorService getExecutorService() {
      // Copied from Executors.newFixedThreadPool() but allows the user to customize the task count
      int taskCount = _tasks == null ? 10 : _tasks;
      return new ThreadPoolExecutor(taskCount, taskCount, 0L, TimeUnit.MILLISECONDS,
          new LinkedBlockingQueue<Runnable>(taskCount), DEFAULT_HANDLER);
    }
  }

  /**
   * Creates a pooled {@link Abseil} which executes a pool runnable tasks simultaneously. The pool sizes dynamically
   * within the min/max task bounds.
   */
  public static class PooledTaskAbseilBuilder extends AbseilBuilder {
    private Integer _maxTasks, _minTasks;

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
    ExecutorService getExecutorService() {
      // Copied from Executors.newCachedThreadPool() but allows the user to customize the min/max task bounds
      return new ThreadPoolExecutor((_minTasks == null ? 0 : _minTasks),
          (_maxTasks == null ? Integer.MAX_VALUE : _maxTasks), 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(),
          DEFAULT_HANDLER);
    }
  }
}
