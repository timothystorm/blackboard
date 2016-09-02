package org.storm.abseil.example;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.storm.abseil.Abseil;
import org.storm.abseil.AbseilBuilder;
import org.storm.abseil.runnable.RunnableFactory;

/**
 * Demonstrates the use of {@link Abseil} by recursively searching a directory for files that match a pattern
 */
public class Finder {
  class DirectoryProducer implements Runnable {
    final File _file;

    DirectoryProducer(File file) {
      _file = file;
    }

    private boolean isNavigable(File dir) {
      try {
        if (dir == null) return false;
        if (!dir.exists()) return false;
        if (!dir.canRead()) return false;
        if (Files.isSymbolicLink(dir.toPath())) return false;

        // check for Windows Junction files - a.k.a Shortcuts
        Path path = dir.toPath();
        if (path.compareTo(path.toRealPath()) != 0) return false;

        return dir.isDirectory();
      } catch (IOException ioe) {
        return false;
      }
    }

    private void locateDirectories(File dir) {
      _queue.offer(dir);

      File[] files = dir.listFiles();
      if (files == null) return;

      for (File f : files) {
        if (isNavigable(f)) locateDirectories(f);
      }
    }

    @Override
    public void run() {
      if (isNavigable(_file)) locateDirectories(_file);
      done();
    }
  }

  class FileLocator implements Runnable {
    private final File   _dir;
    private final String _pattern;

    FileLocator(File dir, String pattern) {
      _dir = dir;
      _pattern = pattern;
    }

    @Override
    public void run() {
      File[] files = _dir.listFiles();
      if (files == null) return;

//      System.out.println(String.format("Searching in [%s]", _dir.getAbsolutePath()));
      for (File f : files) {
        if (f == null) continue;
        if (f.isFile() && f.getName().matches(_pattern)) {
//          System.out.println(String.format("\t{%s} %s", Thread.currentThread().getName(), f.getAbsolutePath()));
        }
      }
    }
  }

  /**
   * Finds directories and puts them in the queue
   */
  class FinderFactory implements RunnableFactory {
    private final String _pattern;

    FinderFactory(String pattern) {
      _pattern = pattern;
    }

    @Override
    public Runnable build() {
      try {
        while (!isDone()) {
          File dir = _queue.poll(1, TimeUnit.SECONDS);
          if (dir == null) continue;
          return new FileLocator(dir, _pattern);
        }
      } catch (InterruptedException e) {}
      return null;
    }
  }

  private static String formatMillis(Long millis) {
    long ms = millis;

    long hours = TimeUnit.MILLISECONDS.toHours(ms);
    ms -= TimeUnit.HOURS.toMillis(hours);

    long mins = TimeUnit.MILLISECONDS.toMinutes(ms);
    ms -= TimeUnit.MINUTES.toMillis(mins);

    long secs = TimeUnit.MILLISECONDS.toSeconds(ms);
    ms -= TimeUnit.SECONDS.toMillis(secs);

    return String.format("%02d:%02d:%02d:%03d", hours, mins, secs, ms);
  }

  public static void main(String[] args) throws Exception {
    // find all java files in the home directory
    new Finder().find(new File(args[0]), args[1]);
  }

  private final AtomicBoolean       _done = new AtomicBoolean(false);

  // holds the directories to search in
  private final BlockingQueue<File> _queue;

  public Finder() {
    _queue = new ArrayBlockingQueue<>(1000);
  }

  void done() {
    _done.set(true);
  }

  public void find(File file, String pattern) {
    // time the execution
    final Long startAt = System.currentTimeMillis();

    // setup shutdown output
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        System.out.println(formatMillis(System.currentTimeMillis() - startAt));
      }
    });

    // verify the inputs
    if (file == null || !file.isDirectory()) throw new IllegalArgumentException("file must be a directory!");
    if (pattern == null) throw new IllegalArgumentException("pattern required!");

    // start putting directories in the queue to be processed
    new Thread(new DirectoryProducer(file)).start();

    // search the directories for matching file patterns
    Abseil abseil = AbseilBuilder.newPooledTaskAbseilBuilder().build();
//    Abseil abseil = AbseilBuilder.newFixedTaskAbseilBuilder().tasks(3).build();
//    Abseil abseil = AbseilBuilder.newSingleTaskAbseilBuilder().build();
    abseil.process(new FinderFactory(pattern));
  }

  boolean isDone() {
    return _done.get();
  }
}
