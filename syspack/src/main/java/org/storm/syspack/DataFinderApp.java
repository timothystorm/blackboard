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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.storm.syspack.dao.UserDao;
import org.storm.syspack.dao.db2.LevelFactory;
import org.storm.syspack.dao.fxf.FxfDao;
import org.storm.syspack.dao.fxf.FxfDaoFactory;
import org.storm.syspack.domain.BindPackage;
import org.storm.syspack.domain.User;
import org.storm.syspack.io.BindPackageCsvReader;
import org.storm.syspack.io.CSVDB2Writer;

import com.opencsv.CSVWriter;

/**
 * Extracts user's data from the bind package tables
 * 
 * @author Timothy Storm
 */
public class DataFinderApp implements Runnable {
  private static final String USAGE = "(-u|--username) <arg> (-p|--password) password <arg> (--bindpacks) <arg> [-l|--level] <[1-7]> [-d|--directory] <arg> [-h|--help] (USER_NAMES...)";

  /**
   * CLI entry point
   * 
   * @param args
   *          - cli arguments
   */
  public static void main(String[] args) {
    new Thread(new DataFinderApp(args), "DataFinder").start();

    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        System.out.println(String.format("\n%s", StringUtils.center("DataFinder", 100, '-')));
      }
    });
  }

  private final CommandLine        _cmd;
  private final ApplicationContext _cntx;
  private final String             _dir, _bindPackFilePath;
  private final FxfDaoFactory      _fxfDaoFactory;
  private final UserDao            _userDao;
  private final String[]           _usernames;

  private DataFinderApp(String[] args) {
    // define and parse
    _cmd = parse(define(), args);

    // interrogate
    _dir = _cmd.getOptionValue('d');
    _bindPackFilePath = _cmd.getOptionValue("bindpacks");
    _usernames = _cmd.getArgs();

    // populate session
    Session session = Session.instance();
    session.put(Session.USERNAME, _cmd.getOptionValue('u'));
    session.put(Session.PASSWORD, _cmd.getOptionValue('p'));
    session.put(Session.DB2LEVEL, LevelFactory.createSource(_cmd.getOptionValue('l', "3")));
    
    // setup context
    _cntx = new AnnotationConfigApplicationContext(Config.class);
    _userDao = _cntx.getBean(UserDao.class);
    _fxfDaoFactory = _cntx.getBean(FxfDaoFactory.class);
  }

  /**
   * Define the cli options
   * 
   * @return CliBuilder
   */
  private CliBuilder define() {
    CliBuilder cli = new CliBuilder(USAGE).hasArgs();
    cli.with(cli.opt('h').longOpt("help").desc("This message").build());
    cli.with(cli.opt('u').longOpt("username").desc("RACF").hasArg().required().build());
    cli.with(cli.opt('p').longOpt("password").desc("DB2 Password").hasArg().required().build());
    cli.with(cli.opt('d').longOpt("directory").desc("Specify where to place generated csv files").hasArg().build());
    cli.with(cli.opt('l').longOpt("level").desc("DB2 level to execute queries").hasArg().build());
    cli.with(cli.opt().longOpt("bindpacks").desc("BindPacks file").hasArg().required().build());
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
            fxfDao.loadTo(users, csvWriter);
          }
        } catch (NoSuchBeanDefinitionException e) {
          System.err.println(e.getMessage());
        } catch (IOException e) {
          e.printStackTrace(System.err);
        }
      });
    } catch (IOException e) {
      e.printStackTrace(System.err);
    }
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
}
