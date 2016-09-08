package org.storm.syspackage;

import static org.apache.commons.lang3.StringEscapeUtils.escapeCsv;
import static org.apache.commons.lang3.StringUtils.LF;

import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.lang3.StringUtils;
import org.storm.syspackage.domain.SysPackage;
import org.storm.syspackage.service.SysPackageService;

public class SysPackageApp {
  private static final String USAGE = "(-u|--username) <arg> (-p|--password) password <arg> [--url] <arg> [-o|--out] <arg> [-h|--help] (PACKAGE_NAMES...)";

  public static void main(String[] args) {
    System.out.println(StringUtils.center("SysPackage", 80, '-'));

    try {
      // define
      CliBuilder cli = new CliBuilder(USAGE).hasArgs();
      cli.with(cli.opt('h').longOpt("help").desc("This message").build());
      cli.with(cli.opt('u').longOpt("username").desc("RACF").required().hasArg().build());
      cli.with(cli.opt('p').longOpt("password").desc("DB2 Password").required().hasArg().build());
      cli.with(cli.opt('o').longOpt("out").desc("file to write results to - defaults to stdout").hasArg().build());
      cli.with(
          cli.opt().longOpt("url").desc("DB2 URL to use - defaults to " + SysAppConfig.DEFAULT_URL).hasArg().build());
      cli.usageWidth(800);

      // parse
      CommandLine cmd = cli.parse(args);
      if (cmd == null || cmd.hasOption('h')) {
        cli.usage();
        return;
      }

      // interrogate
      String username = cmd.getOptionValue('u');
      String password = cmd.getOptionValue('p');
      String url = cmd.getOptionValue("url");
      Collection<String> packages = cmd.getArgList();

      // configure
      SysAppConfig config = new SysAppConfig(username, password, url);
      SysPackageService svc = config.sysPackageService();

      // execute
      try (PrintWriter writer = newPrintWriter(cmd.getOptionValue('o'))) {
        packages.stream().distinct().forEach((pkg) -> write(svc.getPackages(pkg), writer));
      }
    } catch (Exception e) {
      e.printStackTrace(System.err);
    } finally {
      System.out.println(StringUtils.repeat('-', 80));
    }
  }

  /**
   * Creates a new PrintWriter for the file path. If filePath is null then stdout is used
   * 
   * @param filePath
   * @return PrintWriter instance
   */
  private static PrintWriter newPrintWriter(String filePath) {
    try {
      if (filePath == null) return new PrintWriter(System.out);

      Path path = Paths.get(filePath).normalize().toAbsolutePath();
      return new PrintWriter(path.toFile());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Writes the sysPackages to the writer
   * 
   * @param sysPackages
   * @param writer
   */
  private static void write(Collection<SysPackage> sysPackages, PrintWriter writer) {
    sysPackages.forEach((sysPack) -> {
      sysPack.getPackages().forEach((qualifier, tables) -> {
        tables.forEach((table) -> {
          writer.write(escapeCsv(sysPack.getName()) + ',');
          writer.write(escapeCsv(table) + ',');
          writer.write(escapeCsv(qualifier) + ',');
          writer.write(escapeCsv(sysPack.getContoken()) + ',');
          writer.write(escapeCsv(sysPack.getLastUsed().format(DateTimeFormatter.ISO_DATE)) + LF);
        });
      });
    });

    writer.flush();
  }
}
