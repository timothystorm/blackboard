package org.storm.syspack;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.storm.syspack.dao.db2.Db2TableLoader;
import org.storm.syspack.dao.db2.LevelFactory;
import org.storm.syspack.utils.CliProgressMessage;
import org.storm.syspack.utils.FileUtils;
import org.storm.syspack.utils.TimeUtils;

import com.opencsv.CSVReader;

/**
 * Loads data from csv file(s) into the target databases. The csv file(s) should be named the same as the table the data
 * is to be loaded into. For example if you want to load into ALIAS_ACCOUNT you will have to have a CSV file named
 * ALIAS_ACCOUNT.csv
 */
public class LoadApp implements Runnable {
  private static final Logger             log       = LoggerFactory.getLogger(LoadApp.class);
  private static final String             USAGE     = "(-u|--username) <arg> (-p|--password) password <arg> [-l|--level] <[1-7]> [-h|--help] (FILES...)";
  private static final CliProgressMessage _progress = new CliProgressMessage(System.out);

  public static void main(String[] args) {
    new Thread(new LoadApp(args), "SysPack").start();
    
    final Long start = System.currentTimeMillis();
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        _progress.stop("[Run Time %s]", TimeUtils.formatMillis(System.currentTimeMillis() - start));
      }
    });
  }

  private final List<String>   _files;
  private final Db2TableLoader _dao;

  @SuppressWarnings("resource")
  private LoadApp(String[] args) {
    // define and parse
    CommandLine cmd = defineCommand().parse(args);
    if (cmd == null) System.exit(-1);

    // interrogate
    _files = Arrays.asList(cmd.getArgs());

    // create and populate session
    Session session = Session.instance();
    session.put(Session.USERNAME, cmd.getOptionValue('u'));
    session.put(Session.PASSWORD, cmd.getOptionValue('p'));
    session.put(Session.DB2LEVEL, LevelFactory.createEnv(cmd.getOptionValue('l')));

    // setup context
    ApplicationContext cntx = new AnnotationConfigApplicationContext(Config.class);
    _dao = cntx.getBean(Db2TableLoader.class);
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
        _progress.post("[Loading %s]", name);
        _dao.load(name, csv);
      } catch (Exception e) {
        log.error("Data load failed", e);
      }
    });
  }
}
