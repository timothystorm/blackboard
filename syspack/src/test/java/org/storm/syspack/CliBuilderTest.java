package org.storm.syspack;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.Test;

public class CliBuilderTest {
  @Test
  public void test_short_help() throws Exception {
    StringWriter str = new StringWriter();
    PrintWriter writer = new PrintWriter(str);

    // build cli
    CliBuilder cli = new CliBuilder();
    cli.with(cli.help('h').desc("SHOW_ME").build());
    cli.writer(writer);

    // parse
    cli.parse(new String[] { "-h" });
    assertThat(str.toString(), containsString("-h"));
    assertThat(str.toString(), not(containsString("-h,--help")));
    assertThat(str.toString(), containsString("SHOW_ME"));
  }

  @Test
  public void test_long_help() throws Exception {
    StringWriter str = new StringWriter();
    PrintWriter writer = new PrintWriter(str);

    // build cli
    CliBuilder cli = new CliBuilder();
    cli.with(cli.help("help").desc("SHOW_ME").build());
    cli.writer(writer);

    // parse
    cli.parse(new String[] { "--help" });
    assertThat(str.toString(), containsString("--help"));
    assertThat(str.toString(), not(containsString("-h,--help")));
    assertThat(str.toString(), containsString("SHOW_ME"));
  }

  @Test
  public void test_short_long_help() throws Exception {
    StringWriter str = new StringWriter();
    PrintWriter writer = new PrintWriter(str);

    // build cli
    CliBuilder cli = new CliBuilder();
    cli.with(cli.help('h', "help").desc("SHOW_ME").build());
    cli.writer(writer);

    // parse
    cli.parse(new String[] { "-h" });
    assertThat(str.toString(), containsString("-h,--help"));
    assertThat(str.toString(), containsString("SHOW_ME"));
  }
}
