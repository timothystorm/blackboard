package org.storm.abseil.utils;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Sleep {
  /**
   * Safely sleeps a fixed amount of time
   * @param time to wait
   * @param unit of time 
   * @return if fixed sleep completed (true) or was interrupted (false)
   */
  public static boolean fixed(long time, TimeUnit unit) {
    try {
      Thread.sleep(unit.toMillis(time));
      return true;
    } catch (InterruptedException e) {
      return false;
    }
  }

  /**
   * Safely sleeps a random amount of time within the bounds of time
   * @param min - time to sleep inclusive
   * @param max - time to sleep exclusive
   * @param unit - of time
   * @return if fixed sleep completed (true) or was interrupted (false)
   */
  public static boolean random(long min, long max, TimeUnit unit) {
    try {
      long sleepFor = ThreadLocalRandom.current().nextLong(unit.toMillis(min), unit.toMillis(max));
      Thread.sleep(sleepFor);
      return true;
    } catch (InterruptedException e) {
      return false;
    }
  }

  /**
   * Safely sleeps a random amount of time between 0 and max time
   * @param max - time to sleep
   * @param unit - of time
   * @return if fixed sleep completed (true) or was interrupted (false)
   */
  public static boolean random(long max, TimeUnit unit) {
    return random(0, max, unit);
  }
}
