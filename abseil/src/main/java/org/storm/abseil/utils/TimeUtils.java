package org.storm.abseil.utils;

import java.util.concurrent.TimeUnit;

public class TimeUtils {
  public static String formatMillis(Long millis) {
    long ms = millis;

    long hours = TimeUnit.MILLISECONDS.toHours(ms);
    ms -= TimeUnit.HOURS.toMillis(hours);

    long mins = TimeUnit.MILLISECONDS.toMinutes(ms);
    ms -= TimeUnit.MINUTES.toMillis(mins);

    long secs = TimeUnit.MILLISECONDS.toSeconds(ms);
    ms -= TimeUnit.SECONDS.toMillis(secs);

    return String.format("%02d:%02d:%02d:%03d", hours, mins, secs, ms);
  }
}
