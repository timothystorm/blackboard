package org.storm.syspack;

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.storm.syspack.domain.BindPackage;
import org.storm.syspack.io.BindPackageCsvWriter;
import org.storm.syspack.service.BindPackageService;

/**
 * CLI utility to find tables associated with bind packages
 * 
 * @author Timothy Storm
 */
public class SysPackApp implements Runnable {
  private static final String USAGE = "(-u|--username) <arg> (-p|--password) password <arg> [-l|--level] <[1-7]> [-d|--directory] <arg> [-h|--help] (PACKAGE_PATTERN...)";

  /**
   * CLI entry point
   * 
   * @param args
   *          - cli arguments
   */
  public static void main(String[] args) {
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

      // prepare the path/file
      File file = new File(filePath, fileName);
      if (file.getParentFile() != null) file.getParentFile().mkdirs();
      file.createNewFile();

      return new PrintWriter(file);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private final CommandLine        _cmd;
  private final String             _dir;
  private final String[]           _packages;
  private final BindPackageService _service;
  private final ApplicationContext _cntx;

  private SysPackApp(String[] args) {
    // define and parse
    _cmd = parse(define(), args);

    // interrogate
    _dir = _cmd.getOptionValue('d');
    _packages = _cmd.getArgs();

    // populate session
    Session session = Session.instance();
    session.put(Session.USERNAME, _cmd.getOptionValue('u'));
    session.put(Session.PASSWORD, _cmd.getOptionValue('p'));

    Level level = Level.toLevel(_cmd.getOptionValue('l'));
    session.put(Session.DB2LEVEL, (level == null ? Level.L3 : level));

    // setup context
    _cntx = new AnnotationConfigApplicationContext(Config.class);
    _service = _cntx.getBean(BindPackageService.class);
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
    cli.with(cli.opt('l').longOpt("level").desc("DB2 level to execute queries").hasArg().build());
    cli.usageWidth(800);
    return cli;
  }

  /**
   * Parses the command line options. If the parsing fails or the user select "help" this will show usage and exit
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

  @Override
  public void run() {
    try {
      // execute
      PrintWriter writer = newPrintWriter(_dir, "bindpacks.csv");
      try (BindPackageCsvWriter csv = new BindPackageCsvWriter(writer)) {
        Arrays.stream(_packages).distinct().forEach((pkg) -> {
          Collection<BindPackage> bindPackages = _service.getPackages(pkg);
          csv.write(bindPackages);

          // stop if user interrupts
          if (Thread.currentThread().isInterrupted()) return;
        });
      }
    } catch (Exception e) {
      e.printStackTrace(System.err);
    }
  }
}
