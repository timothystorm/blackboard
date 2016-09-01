package org.storm.abseil.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.storm.abseil.Abseil;
import org.storm.abseil.AbseilBuilder;

public class Replay {
  private static class ReplayRunnable implements Runnable {
    private final String _command;

    ReplayRunnable(String command) {
      _command = command;
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
          str.append(line);
        }
        System.out.println(str);
      } catch (InterruptedException e) {
        return;
      } catch (Exception e) {
        System.err.println(str.append(e.getMessage()).append(StringUtils.LF));
      }
    }
  }

  public static void main(String[] args) throws Exception {
    Abseil abseil = AbseilBuilder.newSingleTaskAbseilBuilder(10, TimeUnit.SECONDS).build();
    abseil.process(() -> {
      return new ReplayRunnable(StringUtils.join(args, ' '));
    });
  }
}
