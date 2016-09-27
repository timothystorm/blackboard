package org.storm.abseil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import org.storm.abseil.runnable.MonitorRunnable;

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
 * The creation of Abseils is done with the {@link AbseilBuilder} which provides logical defaults for all required
 * attributes.
 * </p>
 * 
 * @author Timothy Storm
 */
public class Abseil {
  /**
   * Executes the {@link Runnable}s of this {@link Abseil}
   */
  private class AbseilTaskExecutor implements Runnable {
    /** executor that does the work of executing the strategy tasks */
    final ExecutorService _executor;

    private AbseilTaskExecutor(ExecutorService executor) {
      _executor = executor;
    }

    /**
     * @return current state of this {@link Abseil}
     */
    private State getState() {
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
    private void init() {
      if (getState().is(State.INIT)) {
        transitionTo(State.STARTING);
        _monitor.before();
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

      try {
        Runnable task = null;
        while (getState().is(State.RUNNING) && (task = _tasks.get()) != null) {
          _executor.execute(new MonitorRunnable(task, _monitor));
        }
      } finally {
        shutdown();
      }
    }

    /**
     * Shutdown this task.
     * 
     * @param graceful
     *          - (true) allow running tasks to finish. (false) stop running tasks.
     * @return if the executor was shutdown
     */
    private boolean shutdown() {
      if (getState().is(State.RUNNING)) {
        transitionTo(State.SHUTTING_DOWN);

        try {
          // stop new tasks from being processed
          _executor.shutdown();

          // terminate timeout by min|average_execution|max
          long wait = _monitor.getAverage() * _monitor.getActive();
          long median = Math.min(Math.max(wait, MIN_TERMINATE_WAIT), MAX_TERMINATE_WAIT);

          // Wait for tasks to respond to being cancelled
          if (!_executor.awaitTermination(median, TimeUnit.MILLISECONDS)) _executor.shutdownNow();
        } catch (InterruptedException e) {
          _executor.shutdownNow();
          return false;
        } finally {
          transitionTo(State.SHUTDOWN);
          _monitor.after();
          _future.onComplete(_monitor);
          kill();
        }
      }

      return getState().is(State.SHUTDOWN);
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
        return prev;
      } finally {
        _stateLock.unlock();
      }
    }
  }

  /**
   * State of the Abseil
   */
  enum State {
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

  /** period sleep duration for the timeout daemon */
  private static final Long _timeoutSleep      = TimeUnit.MILLISECONDS.toMillis(500);

  static final Long         MAX_TERMINATE_WAIT = TimeUnit.SECONDS.toMillis(5);

  static final Long         MIN_TERMINATE_WAIT = TimeUnit.SECONDS.toMillis(3);

  public static AbseilBuilder builder() {
    return new AbseilBuilder();
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

  private final AbseilTaskExecutor _executor;

  private final AbseilFuture       _future;

  /** max runtime millis the abseil should run */
  private final Long               _maxRuntime;

  /** monitor task executions */
  private final Monitor            _monitor;

  /** abseil process that is run apart from the main thread and can be stopped if the abseil task is unresponsive */
  private Thread                   _processor;

  /** state of this abseil */
  private State                    _state     = State.INIT;

  /** locks state mutation to prevent overlapping transitions */
  private final Lock               _stateLock = new ReentrantLock();

  private Supplier<Runnable>       _tasks;

  /**
   * Builder CTOR
   * 
   * @param builder
   */
  Abseil(AbseilBuilder builder) {
    this(builder.getExecutorService(), builder.getMaxRuntimeMillis(), TimeUnit.MILLISECONDS);
  }

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
  public Abseil(ExecutorService executorService, long maxRuntime, TimeUnit unit) {
    _maxRuntime = unit.toMillis(maxRuntime);
    _executor = new AbseilTaskExecutor(executorService);
    _processor = new Thread(_executor, "Abseil - processor");
    _monitor = new Monitor();
    _future = new AbseilFuture(this);
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
    _processor.interrupt();
    Thread.currentThread().interrupt();
  }

  /**
   * Processes the provided {@link Runnable} until stopped
   * 
   * @param runnable
   * @see #process(Supplier)
   */
  public Future<Monitor> process(final Runnable runnable) {
    return process(() -> runnable);
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
  public Future<Monitor> process(final Supplier<Runnable> tasks) {
    _tasks = tasks;
    final long startAt = System.currentTimeMillis();

    // start timeout daemon
    Thread timeoutDaemon = new Thread(() -> {
      try {
        while ((System.currentTimeMillis() - startAt) < _maxRuntime) {
          Thread.sleep(_timeoutSleep);
        }
        shutdown();
      } catch (InterruptedException e) {
        kill();
      }
    }, "Abseil - timeout");
    timeoutDaemon.setDaemon(true);
    timeoutDaemon.start();

    // setup short circuit shutdown
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      shutdown();
    }));

    // start processing
    _processor.start();
    return _future;
  }

  /**
   * Shutdown this abseil
   */
  public boolean shutdown() {
    return _executor.shutdown();
  }
}
