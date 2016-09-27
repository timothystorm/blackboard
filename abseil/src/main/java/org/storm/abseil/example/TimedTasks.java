package org.storm.abseil.example;

import java.util.concurrent.atomic.AtomicLong;

import org.storm.abseil.Abseil;
import org.storm.abseil.RunnableMonitor;

public class TimedTasks {
  public static void main(String[] args) {
    final Abseil abseil = Abseil.fixedTaskAbseil(10);
    final RunnableMonitor monitor = new RunnableMonitor();
    final AtomicLong count = new AtomicLong(100);
    
    abseil.process(() -> {
      System.out.println(monitor);
    }, () -> {
      return count.getAndDecrement() <= 0 ? null : 
      monitor.monitor(() -> {
        try {
          Thread.sleep(2000);
        } catch(Exception ignore){}
      });
    });
    
  }
}
