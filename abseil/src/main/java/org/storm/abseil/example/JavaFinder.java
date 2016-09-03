package org.storm.abseil.example;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicLong;

import org.storm.abseil.utils.TimeUtils;

public class JavaFinder {
  static class SimpleFilePatternFinder extends SimpleFileVisitor<Path> {
    private final String     _pattern;
    private final AtomicLong _count = new AtomicLong();

    SimpleFilePatternFinder(String pattern) {
      _pattern = pattern;
    }

    @Override
    public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
      File f = path.toFile();

      if (f.getName().matches(_pattern)) {
        System.out.println(String.format("[%03d] %s", _count.incrementAndGet(), f.getAbsolutePath()));
      }
      return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path p, BasicFileAttributes attrs) throws IOException {
      return Files.isDirectory(p, LinkOption.NOFOLLOW_LINKS) ? FileVisitResult.CONTINUE : FileVisitResult.SKIP_SUBTREE;
    }
  }

  public static void main(String[] args) throws Exception {
    long start = System.currentTimeMillis();
    Files.walkFileTree(new File(args[0]).toPath(), new SimpleFilePatternFinder(args[1]));
    long end = System.currentTimeMillis();

    System.out.println(TimeUtils.formatMillis(end - start));
  }
}
