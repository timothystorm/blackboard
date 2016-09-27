package org.storm.abseil;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

import org.storm.abseil.utils.Time;

/**
 * Monitors the execution of an Abseil. Each task {@link #start()} is recorded in thread local but all other values are
 * an aggregation of all {@link #start()} and {@link #stop()} tasks.
 * 
 * @author Timothy Storm
 */
public class Monitor implements Serializable {
  /** start of thread local task */
  private static final ThreadLocal<Long> _startAt         = new ThreadLocal<>();

  private static final long              serialVersionUID = 7768380445612685440L;

  /** active monitored tasks */
  private final AtomicLong               _active          = new AtomicLong();

  /** aggregate monitored task time */
  private final AtomicLong               _aggregate       = new AtomicLong();

  /** average monitored task time */
  private final AtomicLong               _average         = new AtomicLong();

  /** failed task count */
  private final Queue<Throwable>         _fails           = new ConcurrentLinkedQueue<>();

  /** monitor duration */
  private Long                           _duration;

  /** starting time of monitor **/
  private final AtomicLong               _start           = new AtomicLong();

  /** successfully completed tasks */
  private final AtomicLong               _success         = new AtomicLong();

  /** total monitored tasks */
  private final AtomicLong               _total           = new AtomicLong();

  public Monitor after() {
    _duration = System.currentTimeMillis() - _start.get();
    return this;
  }

  public void before() {
    _start.set(System.currentTimeMillis());
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (!(obj instanceof Monitor)) return false;
    if (obj == this) return true;

    Monitor other = (Monitor) obj;
    boolean equal = getTotal().equals(other.getTotal());
    if (!equal) return false;

    equal = getSuccess().equals(other.getSuccess());
    if (!equal) return false;

    equal = getFail().equals(other.getFails());
    if (!equal) return false;

    equal = getAverage().equals(other.getAverage());
    if (!equal) return false;

    equal = getAggregate().equals(other.getAggregate());
    if (!equal) return false;

    return equal;
  }

  public void fail(Throwable error) {
    _fails.add(error);
  }

  /**
   * @return number of tasks started but not completed
   */
  public Long getActive() {
    return _active.get();
  }

  /**
   * @return aggregate runtime of all completed tasks
   */
  public Long getAggregate() {
    return _aggregate.get();
  }

  /**
   * @return average runtime of monitored tasks
   */
  public Long getAverage() {
    return _average.get();
  }

  public Integer getFail() {
    return _fails.size();
  }

  public Collection<Throwable> getFails() {
    return Collections.unmodifiableCollection(_fails);
  }

  public Long getDuration() {
    return _duration;
  }

  public Long getSuccess() {
    return _success.get();
  }

  /**
   * @return total number of monitored tasks that have been started
   */
  public Long getTotal() {
    return _total.get();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getTotal(), getSuccess(), getFail(), getAverage(), getAggregate());
  }

  /**
   * Start monitoring thread local process
   */
  public void start() {
    _startAt.set(System.currentTimeMillis());
    _total.incrementAndGet();
    _active.incrementAndGet();
  }

  public void stop() {
    final long total = _total.get();
    final long runtime = System.currentTimeMillis() - _startAt.get();

    _aggregate.addAndGet(runtime);
    _active.decrementAndGet();
    _average.updateAndGet((avg) -> (avg * (total - 1) + runtime) / total);
  }

  public void success() {
    _success.incrementAndGet();
  }

  @Override
  public String toString() {
    StringBuilder str = new StringBuilder("[");
    str.append("total=").append(getTotal()).append(", ");
    str.append("success=").append(getSuccess()).append(", ");
    str.append("fail=").append(getFail()).append(", ");
    str.append("average=").append(Time.formatMillis(getAverage())).append(", ");
    str.append("aggregate=").append(Time.formatMillis(getAggregate())).append(", ");
    str.append("duration=").append(Time.formatMillis(getDuration()));
    return str.append("]").toString();
  }
}
