package org.storm.syspack.utils;

import java.io.PrintStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.StringUtils;

public class CliProgressMessage implements CliProgress {
  /** producer consumer thread */
  private final BlockingQueue<String> _messageQueue = new ArrayBlockingQueue<>(10);

  /** message currently displayed */
  private String                      _msg;

  /** message sink */
  private final PrintStream           _out;

  /** consumer thread */
  private final Thread                _consumer;

  /** lock guarding access */
  private ReentrantLock               _lock         = new ReentrantLock();

  public CliProgressMessage(final PrintStream out) {
    if ((_out = out) == null) throw new NullPointerException();;

    // consumes messages posted the queue and writes them to the output
    _consumer = new Thread(() -> {
      try {
        for (;;) {
          write(_messageQueue.take());
        }
      } catch (InterruptedException ignore) {} finally {
        clr();
      }
    });
    _consumer.setDaemon(true);
    _consumer.start();
  }

  /**
   * Clears the current message from the output
   */
  private void clr() {
    _lock.lock();

    try {
      if (_msg == null) return;
      for (int i = 0; i < _msg.length(); i++) {
        _out.print("\b \b");
      }
    } finally {
      _lock.unlock();
    }
  }

  /**
   * Posts a formatted message to the output
   * 
   * @param format
   * @param args
   */
  public void post(String format, Object... args) {
    if (format == null) return;
    _messageQueue.offer(String.format(format, args));
  }

  /**
   * Posts a message to the output
   * 
   * @param message
   */
  public void post(String message) {
    if (message == null) return;
    _messageQueue.offer(message);
  }

  /**
   * Posts a new line message to the output
   * 
   * @param message
   */
  public void postln(String message) {
    writeln(message);
  }

  /*
   * (non-Javadoc)
   * @see org.storm.syspack.utils.CliProgress#stop()
   */
  @Override
  public void stop() {
    stop(StringUtils.EMPTY);
  }

  /**
   * Stops progress and displays the final message
   * 
   * @param message
   */
  public void stop(String message) {
    stop(message, (Object[]) null);
  }

  /**
   * Stops progress and displays the final message
   * 
   * @param format
   * @param args
   */
  public void stop(String format, Object... args) {
    _consumer.interrupt();
    writeln(String.format(format, args));
  }

  private void writeln(String message) {
    _lock.lock();

    try {
      if (message == null) return;
      clr();
      _out.print(message + "\n");
      _msg = null;
    } finally {
      _lock.unlock();
    }
  }

  /**
   * Writes the message to the output
   * 
   * @param message
   *          to be written
   */
  private void write(String message) {
    _lock.lock();

    try {
      if (message == null) return;
      clr();
      _out.print((_msg = message));
    } finally {
      _lock.unlock();
    }
  }
}
