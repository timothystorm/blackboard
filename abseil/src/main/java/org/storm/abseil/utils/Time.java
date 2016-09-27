package org.storm.abseil.utils;

import java.util.concurrent.TimeUnit;

/**
 * Time utilities
 * 
 * @author Timothy Storm
 */
public class Time {
  public static String formatMillis(Long millis) {
    long ms = millis;

    long days = TimeUnit.MILLISECONDS.toDays(ms);
    ms -= TimeUnit.DAYS.toMillis(days);

    long hours = TimeUnit.MILLISECONDS.toHours(ms);
    ms -= TimeUnit.HOURS.toMillis(hours);

    long mins = TimeUnit.MILLISECONDS.toMinutes(ms);
    ms -= TimeUnit.MINUTES.toMillis(mins);

    long secs = TimeUnit.MILLISECONDS.toSeconds(ms);
    ms -= TimeUnit.SECONDS.toMillis(secs);

    // ISO8601 - PnDTnHnMnS
    return String.format("P%dDT%dH%dM%dS%d", days, hours, mins, secs, ms);
  }
}
