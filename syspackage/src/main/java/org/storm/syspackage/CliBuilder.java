package org.storm.syspackage;

import java.io.PrintWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;

public class CliBuilder {
  private HelpFormatter     _formatter       = new HelpFormatter();
  private Options           _options         = new Options();
  private CommandLineParser _parser;
  private Boolean           _stopOnNonOption = true;
  private String            _usage, _header, _footer;
  private Integer           _width           = _formatter.getWidth();
  private PrintWriter       _writer          = new PrintWriter(System.out);

  public CliBuilder() {
    this(StringUtils.EMPTY);
  }

  public CliBuilder(String usage) {
    this(usage, StringUtils.EMPTY);
  }

  public CliBuilder(String usage, String header) {
    this(usage, header, StringUtils.EMPTY);
  }

  public CliBuilder(String usage, String header, String footer) {
    usage(usage);
    header(header);
    footer(footer);
  }

  public CliBuilder footer(String footer) {
    _footer = footer;
    return this;
  }

  public CliBuilder formatter(HelpFormatter formatter) {
    _formatter = formatter;
    return this;
  }

  public CliBuilder header(String header) {
    _header = header;
    return this;
  }

  /**
   * @param args
   *          to parse
   * @return Parsed {@link CommandLine} or null on bad command lines after displaying usage message.
   */
  public CommandLine parse(String[] args) {
    if (_parser == null) _parser = new DefaultParser();
    try {
      return _parser.parse(_options, args, _stopOnNonOption);
    } catch (ParseException e) {
      _writer.println("error: " + e.getMessage());
      usage();
      return null;
    }
  }

  public CliBuilder parser(CommandLineParser parser) {
    _parser = parser;
    return this;
  }

  public CliBuilder stopOnNonOption(boolean stopOnNonOption) {
    _stopOnNonOption = stopOnNonOption;
    return this;
  }

  public void usage() {
    _formatter.printHelp(_writer, _width, _usage, _header, _options, _formatter.getLeftPadding(),
        _formatter.getDescPadding(), _footer);
    _writer.flush();
  }

  public CliBuilder usage(String usage) {
    _usage = usage;
    return this;
  }

  public CliBuilder usageWidth(int width) {
    _width = width;
    return this;
  }

  public CliBuilder with(Option... opts) {
    if (opts == null || opts.length <= 0) return this;
    for (Option o : opts) {
      _options.addOption(o);
    }
    return this;
  }

  public CliBuilder writer(PrintWriter writer) {
    _writer = writer;
    return this;
  }
}
