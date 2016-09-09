package org.storm.syspack;

import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

import org.apache.commons.cli.CommandLine;
import org.storm.syspack.domain.BindPackage;
import org.storm.syspack.service.BindPackageService;

import com.opencsv.CSVWriter;

/**
 * CLI utility to find tables associated with bind packages
 * 
 * @author Timothy Storm
 */
public class SysPackApp {
  private static final String USAGE = "(-u|--username) <arg> (-p|--password) password <arg> [--url] <arg> [-d|--directory] <arg> [-h|--help] (PACKAGE_PATTERN...)";

  public static void main(String[] args) {
    System.out.println();

    try {
      // define
      CliBuilder cli = new CliBuilder(USAGE).hasArgs();
      cli.with(cli.opt('h').longOpt("help").desc("This message").build());
      cli.with(cli.opt('u').longOpt("username").desc("RACF").required().hasArg().build());
      cli.with(cli.opt('p').longOpt("password").desc("DB2 Password").required().hasArg().build());
      cli.with(cli.opt('d').longOpt("directory").desc("Specify where to place generated csv files").hasArg().build());
      cli.with(cli.opt().longOpt("url").desc("DB2 URL to use - defaults to " + SysPackAppConfig.DEFAULT_URL).hasArg()
          .build());
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
      SysPackAppConfig config = new SysPackAppConfig(username, password, url);
      BindPackageService svc = config.sysPackageService();

      // execute
      PrintWriter writer = newPrintWriter(cmd.getOptionValue('d'), "syspack.csv");
      try (CSVWriter csv = new CSVWriter(writer)) {
        packages.stream().distinct().forEach((pkg) -> write(svc.getPackages(pkg), csv));
      }
    } catch (Exception e) {
      e.printStackTrace(System.err);
    }
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
