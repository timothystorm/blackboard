package org.storm.abseil;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.storm.abseil.runnable.RunnableSupplier;

/**
 * <p>
 * An abseil is a controlled descent down a vertical drop. In the same vein this {@link Abseil} is a controlled
 * execution of many {@link Runnable}s until either a timeout occurs, no {@link Runnable}s are remaining or the process
 * is terminated by the client.
 * </p>
 * <p>
 * Like a physical abseil this helps the user prevent run away or locked process; both of which are easy to do when
 * thread programming.
 * </p>
 * <p>
 * The creation of Abseils is done with the {@link AbseilBuilder2} which provides logical defaults for all required
 * abseil attributes.
 * </p>
 * 
 * @author Timothy Storm
 */
public class Abseil {

  public interface Observer {
    void done();
  }

  /**
   * Executes the {@link Runnable}s of this {@link Abseil}
   */
  private class AbseilTaskExecutor implements Runnable {
    /** executor that does the work of executing the strategy tasks */
    private final ExecutorService _executor;

    AbseilTaskExecutor(ExecutorService executor) {
      _executor = executor;
    }

    /**
     * @return current state of this {@link Abseil}
     */
    public State getState() {
      _stateLock.lock();

      try {
        State currentState = _state;
        return currentState;
      } finally {
        _stateLock.unlock();
      }
    }

    /**
     * setup task resources
     */
    void init() {
      if (getState().is(State.INIT)) {
        transitionTo(State.STARTING);
        _startAt.set(System.currentTimeMillis());
        transitionTo(State.RUNNING);
      }
    }

    /**
     * Processes the {@link RunnableSupplier} by submitting a new command for each {@link Runnable} of the
     * {@link RunnableSupplier}.
     */
    @Override
    public void run() {
      init();

      Runnable task = null;
      while (getState().is(State.RUNNING) && (task = _tasks.get()) != null) {
        _executor.execute(_monitor.monitor(task));
      }

      shutdown(true);
    }

    /**
     * Shutdown this task.
     * 
     * @param graceful
     *          - (true) allow running tasks to finish. (false) stop running tasks.
     */
    private void shutdown(boolean graceful) {
      if (getState().is(State.RUNNING)) {
        transitionTo(State.SHUTTING_DOWN);

        if (log.isDebugEnabled()) log.debug("shut down starting...");

        try {
          if (graceful) _executor.shutdown();
          else _executor.shutdownNow();

          // configure terminate timeout by using either the min/max wait or the average execution time of other tasks,
          // as long as that average is between the min/max wait bounds.
          long wait = _monitor.getAverageRuntime() * _monitor.getActive();
          long median = Math.min(Math.max(wait, MIN_TERMINATE_WAIT), MAX_TERMINATE_WAIT);
          _executor.awaitTermination(median, TimeUnit.MILLISECONDS);

          if (!_executor.isTerminated()) kill(/* forcefully kill this thread */);
        } catch (InterruptedException e) {
          return;
        } finally {
          transitionTo(State.SHUTDOWN);
          if(_observer != null) _observer.done();
        }
      }
    }

    /**
     * Transitions current state to the desired state
     * 
     * @param state
     *          - to transition to
     * @return previous state
     */
    private State transitionTo(final State state) {
      _stateLock.lock();
      try {
        State prev = _state;
        _state = state;

        if (log.isInfoEnabled()) log.info("{} -> {}", prev, _state);
        return prev;
      } finally {
        _stateLock.unlock();
      }
    }
  }

  /**
   * Builds {@link Abseil}s with logical defaults but can be customized to a clients requirments.
   */
  public static class Builder {
    private ThreadFactory            _factory;
    private RejectedExecutionHandler _handler;
    private Long                     _keepAliveMillis;
    private Long                     _maxRuntimeMillis;
    private Integer                  _minThreads, _maxThreads;
    private BlockingQueue<Runnable>  _queue;

    public Builder() {}

    public Abseil build() {
      return new Abseil(this);
    }

    private ExecutorService getExecutorService() {
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

    private long getMaxRuntimeMillis() {
      return _maxRuntimeMillis == null ? Integer.MAX_VALUE : _maxRuntimeMillis;
    }

    /**
     * Duration a thread should be idle before being removed from the pool.
     * 
     * @param time
     * @param unit
     * @return
     */
    public Builder keepAlive(long time, TimeUnit unit) {
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
    public Builder maxRuntime(long time, TimeUnit unit) {
      _maxRuntimeMillis = unit.toMillis(time);
      return this;
    }

    public Builder maxThreads(int maxThreads) {
      _maxThreads = maxThreads;
      return this;
    }

    public Builder minThreads(int minThreads) {
      _minThreads = minThreads;
      return this;
    }

    public Builder rejectedExecutionHandler(RejectedExecutionHandler handler) {
      _handler = handler;
      return this;
    }

    public Builder threadFactory(ThreadFactory factory) {
      _factory = factory;
      return this;
    }

    public Builder workQueue(BlockingQueue<Runnable> queue) {
      _queue = queue;
      return this;
    }
  }

  /**
   * State of the Abseil
   */
  public enum State {
    INIT,
    RUNNING,
    SHUTDOWN,
    SHUTTING_DOWN,
    STARTING;

    /**
     * Checks this state against the provided state
     * 
     * @param state
     *          - to check this state against
     * @return true if this state and the argument state are the same, false otherwise
     */
    public boolean is(State state) {
      return this.equals(state);
    }
  }

  private static final Logger log                = LoggerFactory.getLogger(Abseil.class);

  private static final Long   MAX_TERMINATE_WAIT = TimeUnit.SECONDS.toMillis(5);

  private static final Long   MIN_TERMINATE_WAIT = TimeUnit.SECONDS.toMillis(3);

  public static Abseil.Builder builder() {
    return new Abseil.Builder();
  }

  /**
   * At any point, at most tasks will be actively processing. If additional tasks are submitted when all threads are
   * active, they will wait in the queue until a thread is available. If any thread terminates due to a failure during
   * execution prior to shutdown, a new one will take its place if needed to execute subsequent tasks. The threads in
   * the pool will exist until {@link #shutdown()} is called or the client forcefully terminates the application -
   * (CTNRL +
   * C)
   * 
   * @param tasks
   *          - maximum number of active tasks
   * @return a fixed task {@link Abseil}
   */
  public static Abseil fixedTaskAbseil(int tasks) {
    return fixedTaskAbseil(tasks, Integer.MAX_VALUE, TimeUnit.MICROSECONDS);
  }

  /**
   * At any point, at most tasks will be actively processing. If additional tasks are submitted when all threads are
   * active, they will wait in the queue until a thread is available. If any thread terminates due to a failure during
   * execution prior to shutdown, a new one will take its place if needed to execute subsequent tasks. The threads in
   * the pool will exist until the timeout period passes, {@link #shutdown()} is called or the client forcefully
   * terminates the application - (CTNRL +
   * C)
   * 
   * @param tasks
   * @param maxRuntime
   * @param unit
   * @return a fixed task {@link Abseil}
   */
  public static Abseil fixedTaskAbseil(int tasks, long maxRuntime, TimeUnit unit) {
    return builder().minThreads(tasks).maxThreads(tasks).maxRuntime(maxRuntime, unit).build();
  }

  /**
   * Creates an {@link Abseil} that uses a single worker thread operating
   * off an unbounded queue. (Note however that if this single
   * thread terminates due to a failure during execution prior to
   * shutdown, a new one will take its place if needed to execute
   * subsequent tasks.) Tasks are guaranteed to execute
   * sequentially, and no more than one task will be active at any
   * given time.
   * 
   * @return a single task {@link Abseil}
   */
  public static Abseil singleTaskAbseil() {
    return singleTaskAbseil(Integer.MAX_VALUE, TimeUnit.MILLISECONDS);
  }

  /**
   * Creates an {@link Abseil} that uses a single worker thread operating
   * off an unbounded queue. (Note however that if this single
   * thread terminates due to a failure during execution prior to
   * shutdown, a new one will take its place if needed to execute
   * subsequent tasks.) Tasks are guaranteed to execute
   * sequentially, and no more than one task will be active at any
   * given time.
   * 
   * @param maxRuntime
   * @param unit
   * @return a single task {@link Abseil}
   */
  public static Abseil singleTaskAbseil(long maxRuntime, TimeUnit unit) {
    return builder().minThreads(1).maxThreads(1).maxRuntime(maxRuntime, unit).build();
  }

  /** max runtime millis the abseil should run */
  private final Long               _maxRuntime;

  /** creates monitored runnables */
  private final RunnableMonitor    _monitor;

  /** abseil process that is run apart from the main thread and can be stopped if the abseil task is unresponsive */
  private Thread                   _processor;

  /** time this abseil process started */
  private final AtomicLong         _startAt      = new AtomicLong(Long.MIN_VALUE);

  /** state of this abseil */
  private State                    _state        = State.INIT;

  /** locks state mutation to prevent overlapping transitions */
  private final Lock               _stateLock    = new ReentrantLock();

  /**
   * 
   */
  private final AbseilTaskExecutor _executor;

  private Supplier<Runnable>       _tasks;

  private Abseil.Observer          _observer;

  /** period sleep duration for the timeout daemon */
  private Long                     _timeoutSleep = TimeUnit.MILLISECONDS.toMillis(500);

  /**
   * Creates an {@link Abseil} that uses the provided {@link ExecutorService} to process the tasks and will timeout, if
   * processing hasn't completed, before the given maxRuntime.
   * 
   * @param executor
   *          - that will execute the tasks
   * @param maxRuntime
   *          - when this {@link Abseil} stops running if the tasks are done or not
   * @param unit
   *          - of the maxRuntime
   */
  private Abseil(Abseil.Builder builder) {
    _maxRuntime = builder.getMaxRuntimeMillis();
    _executor = new AbseilTaskExecutor(builder.getExecutorService());
    _processor = new Thread(_executor, "Abseil - executor");
    _monitor = new RunnableMonitor();
  }

  /**
   * @return current {@link State} of this {@link Abseil}
   */
  public State getState() {
    return _executor.getState();
  }

  /**
   * Forcefully kill this {@link Abseil} processor thread. This doesn't allow for proper resource cleanup and should
   * only be called as a last resort.
   */
  private void kill() {
    log.warn("killing process");
    _processor.interrupt();
    Thread.currentThread().interrupt();
  }

  public void process(Abseil.Observer observer, final Supplier<Runnable> tasks) {
    _observer = observer;
    _tasks = tasks;
    _startAt.set(System.currentTimeMillis());

    // start timeout daemon
    Thread timeoutDaemon = new Thread(() -> {
      try {
        while ((System.currentTimeMillis() - _startAt.get()) < _maxRuntime) {
          Thread.sleep(_timeoutSleep);
        }
        shutdown();
      } catch (InterruptedException e) {
        kill();
      }
    }, "Abseil - timeout monitor");
    timeoutDaemon.setDaemon(true);
    timeoutDaemon.start();

    // setup short circuit shutdown
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      shutdown();
    }));

    _processor.start();
  }

  /**
   * <p>
   * Process the {@link Supplier<Runnable}s. Processing of tasks will terminate under 4 conditions...
   * </p>
   * <ul>
   * <li>{@link Supplier#get()} returns null</li>
   * <li>The {@link Abseil} has been processing for the max runtime</li>
   * <li>The client calls {@link #shutdown()}</li>
   * <li>The program is forcefully terminated; example CNTRL + C.
   * </ul>
   */
  public void process(final Supplier<Runnable> tasks) {
    process(null, tasks);
  }

  /**
   * Tries to shutdown the Abseil as fast as possible
   */
  public void shutdown() {
    _executor.shutdown(false);
  }
}
