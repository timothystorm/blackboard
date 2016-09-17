package org.storm.syspack.utils;

import static java.lang.String.format;

import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

public class TimeUtils {

  public enum Scale {
    MILLIS,
    SECONDS,
    MINUTES,
    HOURS,
    DAYS;
    public static final EnumSet<Scale> FULL = EnumSet.allOf(Scale.class);
    public static final EnumSet<Scale> TIME = EnumSet.of(Scale.HOURS, Scale.MINUTES, Scale.SECONDS, Scale.MILLIS);
  }

  public static String formatMillis(Long millis) {
    return formatMillis(millis, Scale.FULL);
  }

  public static String formatMillis(Long millis, EnumSet<Scale> scale) {
    long ms = millis;

    StringBuilder dur = new StringBuilder();
    if (scale.contains(Scale.DAYS)) {
      long days = TimeUnit.MILLISECONDS.toDays(ms);
      ms -= TimeUnit.DAYS.toMillis(days);
      if(days > 0) dur.append(format("%02d ", days));
    }

    if (scale.contains(Scale.HOURS)) {
      long hours = TimeUnit.MILLISECONDS.toHours(ms);
      ms -= TimeUnit.HOURS.toMillis(hours);
      dur.append(format("%02d:", hours));
    }

    if (scale.contains(Scale.MINUTES)) {
      long mins = TimeUnit.MILLISECONDS.toMinutes(ms);
      ms -= TimeUnit.MINUTES.toMillis(mins);
      dur.append(format("%02d:", mins));
    }

    if (scale.contains(Scale.SECONDS)) {
      long secs = TimeUnit.MILLISECONDS.toSeconds(ms);
      ms -= TimeUnit.SECONDS.toMillis(secs);
      dur.append(format("%02d.", secs));
    }

    if (scale.contains(Scale.MILLIS)) {
      dur.append(format("%03d ", ms));
    }

    dur.setLength(dur.length() - 1);
    return dur.toString();
  }
}
