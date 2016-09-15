package org.storm.syspack.utils;

import java.nio.file.Paths;

import org.springframework.util.StringUtils;

public class FileUtils {
  public static String normalize(String first, String... more) {
    String pth = StringUtils.replace(first, "~", System.getProperty("user.home"));
    return Paths.get(pth, more).normalize().toAbsolutePath().toString();
  }
}
