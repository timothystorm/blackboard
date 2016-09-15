package org.storm.syspack.io;

import java.io.Writer;

import com.opencsv.CSVWriter;
import com.opencsv.ResultSetHelperService;

/**
 * Customized CSVWriter that writes data in a format DB2 can consume
 * 
 * @author Timothy Storm
 */
public class CSVDB2Writer extends CSVWriter {

  public CSVDB2Writer(Writer writer, char separator, char quotechar, char escapechar, String lineEnd) {
    super(writer, separator, quotechar, escapechar, lineEnd);
    initDb2();
  }

  public CSVDB2Writer(Writer writer, char separator, char quotechar, char escapechar) {
    super(writer, separator, quotechar, escapechar);
    initDb2();
  }

  public CSVDB2Writer(Writer writer, char separator, char quotechar, String lineEnd) {
    super(writer, separator, quotechar, lineEnd);
    initDb2();
  }

  public CSVDB2Writer(Writer writer, char separator, char quotechar) {
    super(writer, separator, quotechar);
    initDb2();
  }

  public CSVDB2Writer(Writer writer, char separator) {
    super(writer, separator);
    initDb2();
  }

  public CSVDB2Writer(Writer writer) {
    super(writer);
    initDb2();
  }

  protected void initDb2() {
    ResultSetHelperService resultSetHelper = new ResultSetHelperService();
    resultSetHelper.setDateFormat("yyyy-MM-dd");
    resultSetHelper.setDateTimeFormat("yyyy-MM-dd HH:mm:ss.SSS");
    setResultService(resultSetHelper);
  }
}
