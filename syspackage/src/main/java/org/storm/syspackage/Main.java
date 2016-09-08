package org.storm.syspackage;

import static org.apache.commons.lang3.StringEscapeUtils.escapeCsv;
import static org.apache.commons.lang3.StringUtils.LF;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.io.FilenameUtils;
import org.storm.syspackage.domain.SysPackage;
import org.storm.syspackage.service.SysPackageBasicService;
import org.storm.syspackage.service.SysPackageService;
import org.storm.syspackage.service.dao.SysPackageDao;
import org.storm.syspackage.service.dao.SysPackageJdbcDao;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class Main {
  private static final String DRIVER_CLASS = "com.ibm.db2.jcc.DB2Driver";

  public static void main(String[] args) throws Exception {
    System.out.println();
    
    // Define
    CliBuilder cli = new CliBuilder(Main.class.getSimpleName()
        + " (-u|--username) <arg> (-p|--password) password <arg> [--url] <arg> [-h|--help] [PACKAGE_NAMES...]");
    cli.with(Option.builder("h").longOpt("help").desc("This message").build());
    cli.with(Option.builder("u").longOpt("username").desc("RACF").required().hasArg().build());
    cli.with(Option.builder("p").longOpt("password").desc("DB2 Password").required().hasArg().build());
    cli.with(Option.builder("o").longOpt("output")
        .desc("Output file of write results to - writes to console by default").hasArg().build());
    cli.with(Option.builder().longOpt("url").desc("DB2 URL to use").hasArg().build());
    cli.usageWidth(1200);

    // parse
    CommandLine command = cli.parse(args);
    if (command == null) return;
    if (command.hasOption('h')) cli.usage();

    // interrogate
    String username = command.getOptionValue('u');
    String password = command.getOptionValue('p');
    String url = command.getOptionValue("url", "jdbc:db2://zos1.freight.fedex.com:446/HRO_DBP1");
    List<String> packages = command.getArgList();

    // Setup service call - this should be replaced by DI (guice, spring, etc...)
    DataSource dataSource = newDataSource(username, password, url);
    SysPackageDao dao = new SysPackageJdbcDao(dataSource);
    SysPackageService service = new SysPackageBasicService(dao);

    // execute service
    List<SysPackage> sysPackages = service.getPackagesFor(packages);

    // write output
    try (PrintWriter writer = newPrintWriter(command.getOptionValue('o'))) {
      write(sysPackages, writer);
    }
  }

  static PrintWriter newPrintWriter(String filePath) {
    try {
      PrintWriter writer = null;
      if (filePath == null) writer = new PrintWriter(System.out);
      else {
        String fileName = FilenameUtils.normalize(filePath);
        writer = new PrintWriter(new FileOutputStream(new File(fileName)), true);
      }
      return writer;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  static void write(List<SysPackage> sysPackages, PrintWriter writer) {
    StringBuilder str = new StringBuilder();

    // writer header
    str.append("package,").append("qualifier,").append("table").append(LF);

    sysPackages.forEach((sysPack) -> {
      sysPack.getPackages().forEach((qualifier, tables) -> {
        tables.forEach((table) -> {
          str.append(escapeCsv(sysPack.getName())).append(",");
          str.append(escapeCsv(qualifier)).append(",");
          str.append(escapeCsv(table)).append(LF);
        });
      });
    });

    writer.write(str.toString());
    writer.flush();
  }

  static DataSource newDataSource(String username, String password, String url) {
    try {
      ComboPooledDataSource ds = new ComboPooledDataSource();
      ds.setDriverClass(DRIVER_CLASS);
      ds.setJdbcUrl(url);
      ds.setUser(username);
      ds.setPassword(password);

      return ds;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
