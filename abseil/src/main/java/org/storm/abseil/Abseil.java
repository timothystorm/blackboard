package org.storm.abseil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.storm.abseil.runnable.RunnableFactory;

/**
 * An abseil is a controlled descent down a vertical drop. In the same vein this {@link Abseil} is a controlled
 * execution of many {@link Runnable}s until either a timeout occurs, no {@link Runnable}s are remaining or the process
 * is forcefully terminated.
 * 
 * @author Timothy Storm
 */
public class Abseil {

  /**
   * Executes the {@link Runnable}s in the {@link RunnableFactory}
   */
  private class AbseilTask implements Runnable, RunnableMonitor.Listener {
    /** active tasks, this is not exact but is a best effort answer */
    private final AtomicLong      _active  = new AtomicLong();

    /** average execution time of strategy tasks */
    private final AtomicLong      _average = new AtomicLong();

    /** executor that does the work of executing the strategy tasks */
    private final ExecutorService _executor;

    AbseilTask(ExecutorService executor) {
      _executor = executor;
    }

    @Override
    public void accept(final RunnableMonitor monitor) {
      _active.set(monitor.getActiveCount());
      _average.set(monitor.getAverageRuntime());
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

    /**
     * Processes the {@link RunnableFactory} by submitting a new command for each {@link Runnable} of the
     * {@link RunnableFactory}.
     */
    @Override
    public void run() {
      init();
      if (log.isDebugEnabled()) log.debug("absail running...");

      Runnable run = null;
      while (getState().is(State.RUNNING) && (run = _runnableFactory.build()) != null) {
        _executor.execute(new RunnableMonitor(run, this));
      }

      if (log.isDebugEnabled()) log.debug("absail shutting down.");
      shutdown();
    }

    /**
     * Shutdown this task.
     * 
     */
    private void shutdown() {
      if (getState().is(State.RUNNING)) {
        if (log.isDebugEnabled()) log.debug("shut down starting...");

        try {
          _executor.shutdownNow();
          transitionTo(State.SHUTTING_DOWN);

          // configure terminate timeout
          long wait = _average.get() * _active.get();
          long median = Math.min(Math.max(wait, _minWait), _maxWait);
          _executor.awaitTermination(median, TimeUnit.MILLISECONDS);
          
          if(_executor.isTerminated()) transitionTo(State.SHUTDOWN);
          else kill(/* forcefully kill this thread */);
        } catch (InterruptedException e) {
          return;
        }
      }
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

  private static final Logger log           = LoggerFactory.getLogger(Abseil.class);

  /** max runtime millis the abseil should run */
  private final Long          _maxRuntime;

  /** max time to wait for a shutdown */
  private Long                _maxWait      = TimeUnit.SECONDS.toMillis(10);

  /** min time to wait for a shutdown */
  private Long                _minWait      = TimeUnit.SECONDS.toMillis(3);

  /** abseil process that is run apart from the main thread and can be stopped if the abseil task is unresponsive */
  private Thread              _processor;

  /** time this abseil process started */
  private final AtomicLong    _startAt      = new AtomicLong(Long.MIN_VALUE);

  /** state of this abseil */
  private State               _state        = State.INIT;

  /** locks state mutation to prevent overlapping transitions */
  private final Lock          _stateLock    = new ReentrantLock();

  private RunnableFactory     _runnableFactory;

  private final AbseilTask    _task;

  /** period sleep duration for the timeout daemon */
  private Long                _timeoutSleep = TimeUnit.MILLISECONDS.toMillis(500);

  Abseil(AbseilBuilder builder) {
    _maxRuntime = builder.getMaxRuntimeMillis();
    _task = new AbseilTask(builder.getExecutorService());
    _processor = new Thread(_task, "Abseil - processor");
  }

  /**
   * Forcefully kill this {@link Abseil} processor thread
   */
  private void kill() {
    log.warn("killing process");
    _processor.interrupt();
    Thread.currentThread().interrupt();
  }

  /**
   * Process the {@link RunnableFactory}
   */
  public void process(final RunnableFactory runnableFactory) {
    _runnableFactory = runnableFactory;

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
   * configure the maximum time to wait during a shutdown before a forced shutdown is initiated
   * 
   * @param time
   * @param unit
   * @return this {@link Abseil} for further configuration
   */
  public Abseil maxTimeoutWait(Long time, TimeUnit unit) {
    _maxWait = unit.toMillis(time);
    return this;
  }

  /**
   * configure the minimum time to wait during a shutdown before a forced shutdown is initiated
   * 
   * @param time
   * @param unit
   * @return this this {@link Abseil} for further configuration for further configuration
   */
  public Abseil minTimeoutWait(Long time, TimeUnit unit) {
    _minWait = unit.toMillis(time);
    return this;
  }

  public void shutdown() {
    _task.shutdown();
  }

  public State getState() {
    return _task.getState();
  }
}
