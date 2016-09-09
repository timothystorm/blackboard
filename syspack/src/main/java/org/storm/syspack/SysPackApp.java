package org.storm.syspack;

import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.lang3.StringUtils;
import org.storm.syspack.domain.BindPackage;
import org.storm.syspack.service.BindPackageService;

import com.opencsv.CSVWriter;

/**
 * CLI utility to find tables associated with bind packages
 * 
 * @author Timothy Storm
 */
public class SysPackApp implements Runnable {
  private static final String USAGE = "(-u|--username) <arg> (-p|--password) password <arg> [--url] <arg> [-d|--directory] <arg> [-h|--help] (PACKAGE_PATTERN...)";
  private final CommandLine   _cmd;

  private SysPackApp(String[] args) {
    _cmd = parse(define(), args);
  }

  /**
   * Parses the command line options. If the parsing fails or the user select "help" this will show usage and exit the
   * VM.
   * 
   * @param cli
   *          - to parse the args with
   * @param args
   *          - user args to parse
   * @return parsed command line
   */
  private CommandLine parse(CliBuilder cli, String[] args) {
    CommandLine cmd = cli.parse(args);
    if (cmd == null || cmd.hasOption('h')) {
      cli.usage();
      System.exit((cmd == null) ? -1 : 0);
    }
    return cmd;
  }

  /**
   * Define the cli options
   * 
   * @return CliBuilder
   */
  private CliBuilder define() {
    CliBuilder cli = new CliBuilder(USAGE).hasArgs();
    cli.with(cli.opt('h').longOpt("help").desc("This message").build());
    cli.with(cli.opt('u').longOpt("username").desc("RACF").required().hasArg().build());
    cli.with(cli.opt('p').longOpt("password").desc("DB2 Password").required().hasArg().build());
    cli.with(cli.opt('d').longOpt("directory").desc("Specify where to place generated csv files").hasArg().build());
    cli.with(
        cli.opt().longOpt("url").desc("DB2 URL to use - defaults to " + SysPackAppConfig.DEFAULT_URL).hasArg().build());
    cli.usageWidth(800);
    return cli;
  }

  @Override
  public void run() {
    try {
      // extract option values
      String username = _cmd.getOptionValue('u');
      String password = _cmd.getOptionValue('p');
      String url = _cmd.getOptionValue("url");
      String dir = _cmd.getOptionValue('d');
      String[] packages = _cmd.getArgs();

      // configure
      SysPackAppConfig config = new SysPackAppConfig(username, password, url);
      BindPackageService svc = config.sysPackageService();

      // execute
      PrintWriter writer = newPrintWriter(dir, "syspack.csv");
      try (CSVWriter csv = new CSVWriter(writer)) {
        Arrays.stream(packages).distinct().forEach((pkg) -> {
          write(svc.getPackages(pkg), csv);
          if (Thread.currentThread().isInterrupted()) return;
        });
      }
    } catch (Exception e) {
      e.printStackTrace(System.err);
    }
  }

  public static void main(String[] args) throws Exception {
    new Thread(new SysPackApp(args), "SysPack").start();
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        System.out.println(String.format("\n%s", StringUtils.center("SysPack", 100, '-')));
      }
    });
  }

  /**
   * Creates a new PrintWriter for the file path. If filePath is null then stdout is used
   *
   * @param filePath
   * @return PrintWriter instance
   */
  private static PrintWriter newPrintWriter(String filePath, String fileName) {
    try {
      if (filePath == null) return new PrintWriter(System.out);
      Path path = Paths.get(filePath, fileName).normalize().toAbsolutePath();
      return new PrintWriter(path.toFile());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Writes the sysPackages to the writer
   *
   * @param sysPackages
   * @param csv
   */
  private static void write(Collection<BindPackage> sysPackages, CSVWriter csv) {
    for (BindPackage sysPack : sysPackages) {
      sysPack.getTables().forEach(table -> {
        String name = sysPack.getName();
        String contoken = sysPack.getContoken() == null ? "" : sysPack.getContoken();
        String lastUsed = sysPack.getLastUsed() == null ? "" : sysPack.getLastUsed().format(DateTimeFormatter.ISO_DATE);
        csv.writeNext(new String[] { name, table, contoken, lastUsed });
      });
    }
  }
}
