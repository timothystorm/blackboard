package org.storm.abseil.example;

import java.io.File;
import java.util.concurrent.atomic.AtomicLong;

import org.storm.abseil.utils.TimeUtils;

public class SlowFinder {
  private final AtomicLong _count = new AtomicLong();

  public void find(File dir, String pattern) {
    if (dir == null || !dir.isDirectory()) return;
    for (File f : dir.listFiles()) {
      if (f.isDirectory()) find(f, pattern);
      else if (f.isFile() && f.getName().matches(pattern)) {
        System.out.println(String.format("[%03d] %s", _count.incrementAndGet(), f.getAbsolutePath()));
      }
    }
  }

  public static void main(String[] args) throws Exception {
    long start = System.currentTimeMillis();
    new SlowFinder().find(new File(args[0]), args[1]);
    long end = System.currentTimeMillis();
    
    System.out.println(TimeUtils.formatMillis(end - start));
  }
}
