package org.storm.syspack.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.storm.syspack.domain.BindPackage;

import com.opencsv.CSVParser;
import com.opencsv.CSVReader;

public class BindPackageCsvReader implements Closeable {
  protected Logger        _log = LoggerFactory.getLogger(getClass());

  private final CSVReader _csv;

  public BindPackageCsvReader(Reader reader) {
    assert reader != null;

    _csv = new CSVReader(reader, CSVParser.DEFAULT_SEPARATOR, CSVParser.DEFAULT_QUOTE_CHARACTER, 1);
  }

  public Collection<BindPackage> read() {
    Map<String, BindPackage> bindPacks = new LinkedHashMap<>();

    try {
      String[] line = null;
      while ((line = _csv.readNext()) != null) {
        String name = getValue(line, 0, null);

        BindPackage bindPack = bindPacks.computeIfAbsent(name, n -> new BindPackage(n));
        bindPack.setContoken(getValue(line, 2, null));
        bindPack.setLastUsed(getValue(line, 3, null));
        bindPack.addTable(getValue(line, 1, null));
      }
    } catch (IOException ioe) {
      _log.error("BindPackage read failed", ioe);
    }

    return bindPacks.values();
  }

  @Override
  public void close() throws IOException {
    if (_csv != null) _csv.close();
  }

  private String getValue(String[] array, int index, String defaultValue) {
    if (array == null) return defaultValue;
    if (index >= array.length) return defaultValue;
    return array[index];
  }
}
