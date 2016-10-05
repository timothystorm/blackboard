package org.storm.abseil.cli;

import java.io.PrintWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * <p>
 * Wrapper around org.apche.commons.cli that keeps the different parts together which makes its use more succinct
 * </p>
 *
 * @author Timothy Storm
 */
public class CliBuilder {

  /**
   * Optional additional message for usage; displayed after the options are displayed
   */
  private String            _footer;

  /**
   * Normally set internally but can be overridden if you want to customize how the usage message is displayed
   */
  private HelpFormatter     _formatter       = new HelpFormatter();

  private boolean           _hasArgs;

  /**
   * Optional additional message for usage; displayed after the usage summary but before the options are displayed
   */
  private String            _header;

  /**
   * Underlying options
   */
  private Options           _options         = new Options();

  /**
   * Allows you full customization of the underlying processing engine
   */
  private CommandLineParser _parser;

  /**
   * Indicates that option processing should continue for all arguments even if arguments not recognized as options are
   * encountered (default true)
   */
  private Boolean           _stopOnNonOption = true;

  /**
   * Usage summary displayed as the first line when {@link #usage()} is called
   */
  private String            _usage;

  /**
   * Allows customization of the usage message width
   */
  private Integer           _width           = _formatter.getWidth();

  /**
   * Defaults to stdout but a different PrintWriter can be provided
   */
  private PrintWriter       _writer          = new PrintWriter(System.out);

  /**
   * Creates a cli with configurable items defaulted
   */
  public CliBuilder() {
    this("");
  }

  /**
   * Creates a cli with the usage set and all other configurable items defaulted
   *
   * @param usage
   */
  public CliBuilder(String usage) {
    this(usage, "");
  }

  /**
   * Creates a cli with the usage and header set and all other configurable items defaulted
   *
   * @param usage
   * @param header
   */
  public CliBuilder(String usage, String header) {
    this(usage, header, "");
  }

  /**
   * Creates a cli with the usage, header and footer set and all other configurable items defaulted
   *
   * @param usage
   * @param header
   * @param footer
   */
  public CliBuilder(String usage, String header, String footer) {
    usage(usage);
    header(header);
    footer(footer);
  }

  /**
   * Sets the footer that shows after usage options
   *
   * @param footer
   * @return this builder for further configuration
   */
  public CliBuilder footer(String footer) {
    _footer = footer;
    return this;
  }

  /**
   * Customize the formatter used to generate usage messages
   *
   * @param formatter
   * @return this builder for further configuration
   */
  public CliBuilder formatter(HelpFormatter formatter) {
    _formatter = formatter;
    return this;
  }

  public CliBuilder hasArgs() {
    return hasArgs(true);
  }

  public CliBuilder hasArgs(boolean hasArgs) {
    _hasArgs = hasArgs;
    return this;
  }

  /**
   * Sets the header that shows after usage message but before options
   *
   * @param header
   * @return this builder for further configuration
   */
  public CliBuilder header(String header) {
    _header = header;
    return this;
  }

  /**
   * Make options accessible from command line args with parser. The command line is checked to determine if the help
   * option was requested; if yes then the usage message is displayed and null returned.
   *
   * @param args
   *          to parse
   * @return Parsed {@link CommandLine} or null on bad command lines after displaying usage message.
   * @see CliBuilder#help(char), {@link #help(String)}
   */
  public CommandLine parse(String[] args) {
    if (_parser == null) _parser = new DefaultParser();
    try {
      CommandLine cmd = _parser.parse(_options, args, _stopOnNonOption);
      if (_hasArgs && cmd.getArgs().length <= 0) { throw new ParseException("Missing required argument(s)"); }
      return cmd;
    } catch (ParseException e) {
      _writer.println("error: " + e.getMessage());
      return null;
    }
  }

  /**
   * Sets the underlying parse engine
   *
   * @param parser
   * @return this builder for further configuration
   */
  public CliBuilder parser(CommandLineParser parser) {
    _parser = parser;
    return this;
  }

  /**
   * Specifies if option processing should continue for all arguments even if arguments not recognized as options are
   * encountered (default true)
   *
   * @param stopOnNonOption
   * @return this builder for further configuration
   */
  public CliBuilder stopOnNonOption(boolean stopOnNonOption) {
    _stopOnNonOption = stopOnNonOption;
    return this;
  }

  /**
   * Prints out the usage message to the writer
   *
   * @see #writer(PrintWriter)
   */
  public void usage() {
    _formatter.printHelp(_writer, _width, _usage, _header, _options, _formatter.getLeftPadding(),
        _formatter.getDescPadding(), _footer);
    _writer.flush();
  }

  /**
   * Sets the usage message
   *
   * @param usage
   * @return this builder for further configuration
   */
  public CliBuilder usage(String usage) {
    _usage = (usage == null || usage.length() <= 0 ? " " : usage);
    return this;
  }

  /**
   * Specify the {@link #usage()} width
   *
   * @param width
   * @return this builder for further configuration
   */
  public CliBuilder usageWidth(int width) {
    _width = width;
    return this;
  }

  /**
   * Add 1-* {@link Option}s to this cli for parsing
   *
   * @param opts
   * @return this builder for further configuration
   */
  public CliBuilder with(Option... opts) {
    if (opts == null || opts.length <= 0) return this;
    for (Option o : opts) {
      _options.addOption(o);
    }
    return this;
  }

  /**
   * Set writer used for {@link #usage()}
   *
   * @param writer
   * @return this builder for further configuration
   */
  public CliBuilder writer(PrintWriter writer) {
    _writer = writer;
    return this;
  }
}
