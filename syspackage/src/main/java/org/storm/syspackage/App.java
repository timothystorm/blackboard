package org.storm.syspackage;

import static org.apache.commons.lang3.StringEscapeUtils.escapeCsv;
import static org.apache.commons.lang3.StringUtils.LF;

import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.storm.syspackage.domain.SysPackage;
import org.storm.syspackage.service.SysPackageService;

public class App {
  public static void main(String[] args) throws Exception {
    System.out.println();

    try {
      // Define
      CliBuilder cli = new CliBuilder(App.class.getSimpleName()
          + " (-u|--username) <arg> (-p|--password) password <arg> [--url] <arg> [-h|--help] [PACKAGE_NAMES...]");
      cli.with(cli.opt('h').longOpt("help").desc("This message").build());
      cli.with(cli.opt('u').longOpt("username").desc("RACF").required().hasArg().build());
      cli.with(cli.opt('p').longOpt("password").desc("DB2 Password").required().hasArg().build());
      cli.with(cli.opt('o').longOpt("output").desc("file to write results to - defaults to stdout").hasArg().build());
      cli.with(cli.opt().longOpt("url").desc("DB2 URL to use").hasArg().build());
      cli.usageWidth(800);

      // parse
      CommandLine command = cli.parse(args);
      if (command == null) return;
      if (command.hasOption('h')) cli.usage();

      // interrogate
      String username = command.getOptionValue('u');
      String password = command.getOptionValue('p');
      String url = command.getOptionValue("url", "jdbc:db2://zos1.freight.fedex.com:446/HRO_DBP1");
      Set<String> packages = new LinkedHashSet<>(command.getArgList());

      // verify
      if (packages.isEmpty()) cli.usage();

      // configure
      Config config = new Config(username, password, url);
      SysPackageService service = config.sysPackageService();

      // execute
      try (PrintWriter writer = newPrintWriter(command.getOptionValue('o'))) {
        for (String pkg : packages) {
          Collection<SysPackage> sysPackages = service.getPackagesFor(pkg);
          write(sysPackages, writer);
        }
      }

      System.out.println();
    } catch (Exception e) {
      e.printStackTrace(System.err);
      System.exit(-1);
    }
  }

  /**
   * Creates a new PrintWriter for the file path. If filePath
   * 
   * @param filePath
   * @return
   */
  private static PrintWriter newPrintWriter(String filePath) {
    try {
      PrintWriter writer = null;
      if (filePath == null) writer = new PrintWriter(System.out);
      else {
        Path path = Paths.get(filePath).normalize().toAbsolutePath();
        writer = new PrintWriter(path.toFile());
      }
      return writer;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static void write(Collection<SysPackage> sysPackages, PrintWriter writer) {
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
}
