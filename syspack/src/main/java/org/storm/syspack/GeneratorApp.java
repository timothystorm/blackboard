package org.storm.syspack;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.cli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.storm.syspack.dao.BindPackageDao;
import org.storm.syspack.dao.db2.LevelFactory;
import org.storm.syspack.io.BindPackageCsvWriter;
import org.storm.syspack.utils.CliProgressMessage;
import org.storm.syspack.utils.FileUtils;
import org.storm.syspack.utils.TimeUtils;

/**
 * CLI utility to find tables associated with bind packages
 * 
 * @author Timothy Storm
 */
public class GeneratorApp implements Runnable {
  private static final Logger             log           = LoggerFactory.getLogger(GeneratorApp.class);
  private static final String             DEFAULT_LEVEL = "3";
  private static final String             USAGE         = "(-u|--username) <arg> (-p|--password) password <arg> [-l|--level] <[1-7]> [-d|--directory] <arg> [-h|--help] (PACKAGE_PATTERNS...)";
  private static final CliProgressMessage _progress     = new CliProgressMessage(System.out);

  /**
   * CLI entry point
   * 
   * @param args
   *          - cli arguments
   */
  public static void main(String[] args) {
    new Thread(new GeneratorApp(args), "SysPack").start();

    final long start = System.currentTimeMillis();
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        _progress.stop("[Run Time %s]", TimeUtils.formatMillis(System.currentTimeMillis() - start));
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

  private final String             _dir;
  private final Collection<String> _packages;
  private final BindPackageDao     _dao;
  private static final String      _fileName = "bindpacks.csv";

  @SuppressWarnings("resource")
  private GeneratorApp(String[] args) {
    // define and parse
    CommandLine cmd = defineCommand().parse(args);
    if (cmd == null) System.exit(-1);

    // interrogate
    _dir = cmd.hasOption('d') ? FileUtils.normalize(cmd.getOptionValue('d')) : null;
    _packages = Arrays.asList(cmd.getArgs());

    // create and populate session
    Session session = Session.instance();
    session.put(Session.USERNAME, cmd.getOptionValue('u'));
    session.put(Session.PASSWORD, cmd.getOptionValue('p'));
    session.put(Session.DB2LEVEL, LevelFactory.createUte(cmd.getOptionValue('l', DEFAULT_LEVEL)));

    // setup context
    ApplicationContext cntx = new AnnotationConfigApplicationContext(Config.class);
    _dao = cntx.getBean(BindPackageDao.class);
  }

  /**
   * Define the cli options
   * 
   * @return CliBuilder
   */
  private CliBuilder defineCommand() {
    CliBuilder cli = new CliBuilder(USAGE).hasArgs();
    cli.with(cli.help('h', "help").build());
    cli.with(cli.opt('u').longOpt("username").desc("DB2 username").required().hasArg().build());
    cli.with(cli.opt('p').longOpt("password").desc("DB2 Password").required().hasArg().build());
    cli.with(cli.opt('d').longOpt("directory").desc("Directory to write CSV (stdout by default)").hasArg().build());
    cli.with(cli.opt('l').longOpt("level").desc("DB2 level [1-7] (" + DEFAULT_LEVEL + " by default)").hasArg().build());
    cli.usageWidth(800);
    return cli;
  }

  @Override
  public void run() {
    PrintWriter writer = newPrintWriter(_dir, _fileName);

    try (BindPackageCsvWriter csv = new BindPackageCsvWriter(writer)) {
      _packages.stream().distinct().sorted().forEach((pkg) -> {
        _progress.post("[Generating %s]\n", pkg);
        csv.write(_dao.find(pkg));
      });
    } catch (IOException e) {
      log.error("Could not open CSV for writing", e);
    }
  }
}
