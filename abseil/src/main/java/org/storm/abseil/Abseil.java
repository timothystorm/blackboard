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

    void init() {
      if (getState().is(State.INIT)) {
        transitionTo(State.STARTING);
        _startAt.set(System.currentTimeMillis());
        transitionTo(State.RUNNING);
      }
    }

    /**
     * Processes the {@link RunnableFactory} by submitting a new command for each {@link Runnable} of the
     * {@link RunnableFactory}.
     */
    @Override
    public void run() {
      if (log.isDebugEnabled()) log.debug("absail running...");
      init();

      Runnable run = null;
      while (getState().is(State.RUNNING) && (run = _runnableFactory.build()) != null) {
        _executor.execute(new RunnableMonitor(run, this));
      }

      if (log.isDebugEnabled()) log.debug("absail shutting down.");
      shutdown(true);
    }

    /**
     * Shut down the running task.
     * 
     * @param graceful
     *          - true: orderly shutdown by allowing active commands to complete. false: attempt to stop active
     *          commands.
     */
    void shutdown(final boolean graceful) {
      if (getState().is(State.RUNNING)) {
        if (log.isDebugEnabled()) log.debug("shut down gracefully='{}' starting...", graceful);
        transitionTo(State.SHUTTING_DOWN);

        if (graceful) _executor.shutdown();
        else _executor.shutdownNow();

        try {
          // configure terminate timeout
          long wait = _average.get() * _active.get();
          long median = Math.min(Math.max(wait, _minWait), _maxWait);

          _executor.awaitTermination(median, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ignore) {} finally {
          if (!_executor.isTerminated()) kill();
          else transitionTo(State.SHUTDOWN);
        }
      }
    }
  }

  /**
   * State of the Abseil
   */
  public enum State {
    FAIL,
    FORCE_SHUTDOWN,
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
  private Long                _maxWait      = TimeUnit.SECONDS.toMillis(32);

  /** min time to wait for a shutdown */
  private Long                _minWait      = TimeUnit.SECONDS.toMillis(4);

  /** abseil process that is run apart from the main thread and can be stopped if the abseil task is unresponsive */
  private Thread              _process;

  /** time this abseil process started */
  private final AtomicLong    _startAt      = new AtomicLong(Long.MIN_VALUE);

  /** state of this abseil */
  private State               _state        = State.INIT;

  /** locks state mutation to prevent overlapping transitions */
  private final Lock          _stateLock    = new ReentrantLock();

  private RunnableFactory     _runnableFactory;

  /** runnable abseil task that does the work of executing the strategy */
  private final AbseilTask    _task;

  /** period sleep duration for the timeout daemon */
  private Long                _timeoutSleep = TimeUnit.MILLISECONDS.toMillis(250);

  Abseil(AbseilBuilder builder) {
    _maxRuntime = builder.getMaxRuntimeMillis();
    _task = new AbseilTask(builder.getExecutorService());
    _process = new Thread(_task, "Abseil worker");
  }

  /**
   * @return current state of this {@link Abseil}
   */
  public State getState() {
    _stateLock.lock();

    try {
      State current = _state;
      return current;
    } finally {
      _stateLock.unlock();
    }
  }

  /**
   * Forcefully kill this {@link Abseil}
   */
  private void kill() {
    log.warn("killing process");
    transitionTo(State.FORCE_SHUTDOWN);
    _process.interrupt();
    
    if(_process.isAlive())  transitionTo(State.FAIL);
    else  transitionTo(State.SHUTDOWN);
    
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
        _task.shutdown(true);
      } catch (InterruptedException e) {
        log.warn("timeout interrupted", e);
        kill();
      }
    }, "Abseil - timeout monitor");
    timeoutDaemon.setDaemon(true);
    timeoutDaemon.start();

    // setup short circuit shutdown
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      _task.shutdown(false);
    }));

    _process.start();
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

  /**
   * Initiate a graceful shutdown of this this {@link Abseil}.
   */
  public void shutdown() {
    _task.shutdown(true);
  }
  
  /**
   * Initiate a hard shutdown of this this {@link Abseil}.
   */
  public void shutdownNow(){
    _task.shutdown(false);
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
