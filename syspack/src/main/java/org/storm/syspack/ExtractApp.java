package org.storm.syspack;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.apache.commons.cli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.storm.syspack.dao.FxfDao;
import org.storm.syspack.dao.FxfDaoFactory;
import org.storm.syspack.dao.UserDao;
import org.storm.syspack.dao.db2.LevelFactory;
import org.storm.syspack.domain.BindPackage;
import org.storm.syspack.domain.User;
import org.storm.syspack.io.BindPackageCsvReader;
import org.storm.syspack.io.CSVDB2Writer;
import org.storm.syspack.utils.CliProgressMessage;
import org.storm.syspack.utils.FileUtils;
import org.storm.syspack.utils.TimeUtils;

import com.opencsv.CSVWriter;

/**
 * Extracts user's data from the bind package tables
 * 
 * @author Timothy Storm
 */
public class ExtractApp implements Runnable {
  private static final CliProgressMessage _progress     = new CliProgressMessage(System.out);
  private static final String             DEFAULT_LEVEL = "3";
  private static final Logger             log           = LoggerFactory.getLogger(ExtractApp.class);
  private static final String             USAGE         = "(-u|--username) <arg> (-p|--password) password <arg> (--bindpacks) <arg> [-l|--level] <[1-7]> [-d|--directory] <arg> [-h|--help] (USER_NAMES...)";

  /**
   * CLI entry point
   * 
   * @param args
   *          - cli arguments
   */
  public static void main(String[] args) {
    new Thread(new ExtractApp(args), "DataFinder").start();
    
    final long start = System.currentTimeMillis();
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        _progress.stop("[Run Time %s]", TimeUtils.formatMillis(System.currentTimeMillis() - start));
      }
    });
  }

  private final String        _dir, _bindPackFilePath;
  private final FxfDaoFactory _fxfDaoFactory;
  private final UserDao       _userDao;
  private final String[]      _usernames;

  @SuppressWarnings("resource")
  private ExtractApp(String[] args) {
    // define and parse
    CommandLine cmd = defineCommand().parse(args);
    if (cmd == null) System.exit(-1);

    // interrogate
    _dir = FileUtils.normalize(cmd.getOptionValue('d'));
    _bindPackFilePath = FileUtils.normalize(cmd.getOptionValue("bindpacks"));
    _usernames = cmd.getArgs();

    // create and populate session
    Session session = Session.instance();
    session.put(Session.USERNAME, cmd.getOptionValue('u'));
    session.put(Session.PASSWORD, cmd.getOptionValue('p'));
    session.put(Session.DB2LEVEL, LevelFactory.createUte(cmd.getOptionValue('l', DEFAULT_LEVEL)));

    // setup context
    ApplicationContext cntx = new AnnotationConfigApplicationContext(Config.class);
    _userDao = cntx.getBean(UserDao.class);
    _fxfDaoFactory = cntx.getBean(FxfDaoFactory.class);
  }

  /**
   * Define the cli options
   * 
   * @return CliBuilder
   */
  private CliBuilder defineCommand() {
    CliBuilder cli = new CliBuilder(USAGE).hasArgs();
    cli.with(cli.help('h', "help").build());
    cli.with(cli.opt('u').longOpt("username").desc("DB2 username").hasArg().required().build());
    cli.with(cli.opt('p').longOpt("password").desc("DB2 password").hasArg().required().build());
    cli.with(cli.opt('d').longOpt("directory").desc("Directory to write CSVs").hasArg().required().build());
    cli.with(cli.opt('l').longOpt("level").desc("DB2 level [1-7] (" + DEFAULT_LEVEL + " by default)").hasArg().build());
    cli.with(cli.opt().longOpt("bindpacks").desc("BindPacks file path").hasArg().required().build());
    cli.usageWidth(800);
    return cli;
  }

  private FileWriter newFileWriter(String dir, String fileName) {
    try {
      // prepare the path/file
      File file = new File(dir, fileName);
      if (file.getParentFile() != null) file.getParentFile().mkdirs();
      file.createNewFile();

      return new FileWriter(file);
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
  }

  @Override
  public void run() {
    try {
      // iterate each user login
      Collection<User> users = new ConcurrentSkipListSet<>();
      Arrays.stream(_usernames).forEach(name -> users.add(_userDao.read(name)));

      // load bind packages
      Collection<BindPackage> bindPackages = null;
      FileReader reader = new FileReader(_bindPackFilePath);
      try (BindPackageCsvReader csvReader = new BindPackageCsvReader(reader)) {
        bindPackages = csvReader.read();
      }

      // extract unique tables from bind packages
      final Set<String> uniqueTables = new LinkedHashSet<>();
      bindPackages.forEach(bp -> {
        uniqueTables.addAll(bp.getTables());
      });

      // iterate tables
      uniqueTables.parallelStream().forEach(table -> {
        try {
          // create dao for the FXF table
          FxfDao fxfDao = _fxfDaoFactory.getFxfDao(table);

          // write table data
          FileWriter writer = newFileWriter(_dir, table + ".csv");
          try (CSVWriter csvWriter = new CSVDB2Writer(writer)) {
            _progress.postln("[Extracting " + table + "]");
            fxfDao.load(users, csvWriter);
          }
        } catch (NoSuchBeanDefinitionException e) {
          _progress.postln("[No Extractor defined for " + table + "]");
        } catch (IOException e) {
          log.error("Failed to write csv data", e);
        }
      });
    } catch (IOException e) {
      log.error("Failed to process bind data", e);
    }
  }
}
