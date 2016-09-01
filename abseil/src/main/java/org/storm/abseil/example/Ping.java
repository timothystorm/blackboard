package org.storm.abseil.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.storm.abseil.Abseil;
import org.storm.abseil.AbseilBuilder;

/**
 * Small example of using {@link Abseil}
 * 
 * @author Timothy Storm
 */
public final class Ping {
  private static class PingRunnable implements Runnable {
    private final String _command;

    PingRunnable(String host) {
      _command = String.format("ping -%c 3 %s", (SystemUtils.IS_OS_WINDOWS ? 'n' : 'c'), host);
    }

    @Override
    public void run() {
      Process process = null;
      StringBuilder str = new StringBuilder();

      try {
        process = Runtime.getRuntime().exec(_command);
        process.waitFor();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = null;
        while ((line = reader.readLine()) != null) {
          str.append(line).append(StringUtils.LF);
        }
        System.out.println(str);
      } catch (InterruptedException e) {
        return;
      } catch (Exception e) {
        str.append(e.getMessage()).append(StringUtils.LF);
        System.err.println(str);
      } 
    }
  }

  public static void main(String[] args) {
    Abseil abseil = AbseilBuilder.newFixedTaskAbseilBuilder(10, TimeUnit.SECONDS).tasks(4).build();
    abseil.process(() -> {
      return new PingRunnable(args[0]);
    });
  }
}
