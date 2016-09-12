package org.storm.syspack.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.opencsv.CSVWriter;

public class TableCsvWriter implements Closeable {
  private final CSVWriter _csv;

  public TableCsvWriter(Writer writer) {
    _csv = new CSVWriter(writer);
  }

  public void write(List<Map<String, Object>> table, boolean includeHeader) {
    boolean headerWritten = false;
    for (Map<String, Object> row : table) {
      if (!headerWritten && includeHeader) {
        Set<String> columnNames = row.keySet();
        _csv.writeNext(columnNames.toArray(new String[columnNames.size()]));
        headerWritten = true;
      }

      List<String> values = row.values().stream().map(v -> {
        return Objects.toString(v, "");
      }).collect(Collectors.toList());
      _csv.writeNext(values.toArray(new String[values.size()]));
    }
  }

  @Override
  public void close() throws IOException {
    if (_csv != null) _csv.close();
  }
}
