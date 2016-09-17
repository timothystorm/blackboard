package org.storm.syspack.utils;

import java.io.PrintStream;

public class CliProgressSpinner implements CliProgress {

  private final Thread             _spinner;
  private final String[]           _marks = new String[] { "\\", "|", "/", "-" };
  private final CliProgressMessage _cliMessage;

  public CliProgressSpinner(PrintStream out) {
    _cliMessage = new CliProgressMessage(out);
    _spinner = new Thread(() -> {
      for (int i = 0;;) {
        _cliMessage.post(_marks[i]);

        if (i == _marks.length) i = 0;
        else i = ((i + 1) % _marks.length);
      }
    });
    _spinner.setDaemon(true);
    _spinner.start();
  }

  @Override
  public void stop() {
    try {
      _spinner.interrupt();
    } catch (Exception ignore) {} finally {
      _cliMessage.stop();
    }
  }

  public static void main(String[] args) throws Exception {
    CliProgress p = new CliProgressSpinner(System.out);
    Thread.sleep(5000);
    p.stop();
  }
}
