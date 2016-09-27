package org.storm.abseil.example;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import org.storm.abseil.Abseil;
import org.storm.abseil.runnable.RunnableSupplier;
import org.storm.abseil.utils.TimeUtils;

public class FastFinder {
  private static final int POOL_SIZE = 8;

  /**
   * Creates a pool of runnables that are responsible for traversing directories looking for other files and directories
   */
  static class DirFinderSupplier implements RunnableSupplier, Consumer<Runnable> {
    private final BlockingQueue<Runnable> _pool;

    private DirFinderSupplier() {
      _pool = new LinkedBlockingQueue<>(POOL_SIZE);

      // fill the pool
      for (int i = 0; i < POOL_SIZE; i++) {
        _pool.offer(new Runnable() {
          public void run() {
            try {
              Path p = _dirs.poll(500, TimeUnit.MILLISECONDS);
              if (p == null) return;

              if (isNavigable(p)) {
                for (File f : p.toFile().listFiles()) {
                  if (f.isDirectory()) _dirs.offer(f.toPath());
                  if (f.isFile()) _files.offer(f.toPath());
                }

                // go back into the pool to process another directory
                _pool.offer(this);
              }
            } catch (InterruptedException e) {
              return;
            }
          }
        });
      }
    }

    @Override
    public void accept(Runnable run) {
      _pool.offer(run);
    }

    @Override
    public Runnable get() {
      try {
        return _pool.poll(500, TimeUnit.MILLISECONDS);
      } catch (InterruptedException ignore) {}
      return null;
    }
  }

  /**
   * Creates a pool of runnables that are responsible for finding files that match a pattern
   */
  static class FileFinderSupplier implements RunnableSupplier, Consumer<Runnable> {
    private final AtomicLong              _count = new AtomicLong();
    private final BlockingQueue<Runnable> _pool;

    private FileFinderSupplier(final String pattern) {
      _pool = new LinkedBlockingQueue<>(POOL_SIZE);

      // fill the pool
      for (int i = 0; i < POOL_SIZE; i++) {
        _pool.offer(new Runnable() {
          public void run() {
            try {
              Path p = _files.poll(100, TimeUnit.MILLISECONDS);
              if (p == null) return;
              if (p.toFile().getName().matches(pattern)) {
                System.out.println(String.format("[%03d] %s", _count.incrementAndGet(), p));
              }

              // go back to the pool for more processing
              _pool.offer(this);
            } catch (InterruptedException e) {
              return;
            }
          }
        });
      }
    }

    @Override
    public void accept(Runnable run) {
      _pool.offer(run);
    }

    @Override
    public Runnable get() {
      try {
        return _pool.poll(100, TimeUnit.MILLISECONDS);
      } catch (InterruptedException ignore) {}
      return null;
    }
  }

  // directories to be traversed
  private static final BlockingQueue<Path> _dirs  = new LinkedBlockingQueue<>();

  // files to be traversed
  private static final BlockingQueue<Path> _files = new LinkedBlockingQueue<>();

  /**
   * Verifies a directory is navigable by checking its characteristics
   */
  private static boolean isNavigable(Path p) {
    return Files.isDirectory(p, LinkOption.NOFOLLOW_LINKS);
  }

  public static void main(String[] args) throws Exception {
    new FastFinder().find(new File(args[0]).toPath(), args[1]);
  }

  private final Abseil _producer, _consumer;

  public FastFinder() {
    _producer = Abseil.fixedTaskAbseil(POOL_SIZE);
    _consumer = Abseil.fixedTaskAbseil(POOL_SIZE);
  }

  public void find(final Path p, final String pattern) throws InterruptedException {
    // prime the directory queue
    if (isNavigable(p)) _dirs.put(p);
    else return;

    final long start = System.currentTimeMillis();
    _producer.process(new DirFinderSupplier());
    _consumer.process(new FileFinderSupplier(pattern));

    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        long end = System.currentTimeMillis();
        System.out.println(TimeUtils.formatMillis(end - start));
      }
    });
  }
}
