package org.storm.syspack;

import static java.lang.String.format;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.storm.syspack.dao.db2.Db2TableLoader;
import org.storm.syspack.dao.db2.LevelFactory;
import org.storm.syspack.utils.FileUtils;
import org.storm.syspack.utils.TimeUtils;

import com.opencsv.CSVReader;

/**
 * Loads data from csv file(s) into the target databases. The csv file(s) should be named the same as the table the data
 * is to be loaded into. For example if you want to load into ALIAS_ACCOUNT you will have to have a CSV file named
 * ALIAS_ACCOUNT.csv
 */
public class DataLoaderApp implements Runnable {
  private static final Logger log   = LoggerFactory.getLogger(DataLoaderApp.class);
  private static final String USAGE = "(-u|--username) <arg> (-p|--password) password <arg> [-l|--level] <[1-7]> [-h|--help] (FILES...)";

  public static void main(String[] args) {
    new Thread(new DataLoaderApp(args), "SysPack").start();
    final Long start = System.currentTimeMillis();

    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        System.out.println(
            format("(%s) %s", TimeUtils.formatMillis(System.currentTimeMillis() - start), _processed));
      }
    });
  }

  private final CommandLine        _cmd;
  private final List<String>       _files;
  private final ApplicationContext _cntx;
  private final Db2TableLoader     _dao;
  private static final Set<String> _processed = new ConcurrentSkipListSet<>();

  private DataLoaderApp(String[] args) {
    // define and parse
    _cmd = defineCommand().parse(args);
    if (_cmd == null) System.exit(-1);

    // interrogate
    _files = Arrays.asList(_cmd.getArgs());

    // create and populate session
    Session session = Session.instance();
    session.put(Session.USERNAME, _cmd.getOptionValue('u'));
    session.put(Session.PASSWORD, _cmd.getOptionValue('p'));
    session.put(Session.DB2LEVEL, LevelFactory.createEnv(_cmd.getOptionValue('l')));

    // setup context
    _cntx = new AnnotationConfigApplicationContext(Config.class);
    _dao = _cntx.getBean(Db2TableLoader.class);
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
    cli.with(cli.opt('l').longOpt("level").desc("DB2 level [1-7] to load data into").required().hasArg().build());
    cli.usageWidth(800);
    return cli;
  }

  @Override
  public void run() {
    _files.forEach(file -> {
      File candidate = Paths.get(FileUtils.normalize(file)).toFile();
      if (candidate.isDirectory()) {
        process(candidate.listFiles((f, name) -> name.toLowerCase().endsWith(".csv")));

        if (Thread.currentThread().isInterrupted()) return;
      } else process(candidate);
    });
  }

  private void process(File... files) {
    if (files == null || files.length <= 0) return;
    Arrays.stream(files).parallel().forEach(f -> {
      try (CSVReader csv = new CSVReader(new FileReader(f))) {
        String name = StringUtils.substringBefore(f.getName(), ".");
        _dao.load(name, csv);
        _processed.add(name);
      } catch (Exception e) {
        log.error("Data load failed", e);
      }
    });
  }
}
