package org.storm.syspack.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

import org.storm.syspack.domain.BindPackage;

import com.opencsv.CSVWriter;

/**
 * Writes {@link BindPackage}s to csv.
 * 
 * @see BindPackageCsvReader
 */
public class BindPackageCsvWriter implements Closeable {
  private final CSVWriter      _csv;
  private final AtomicBoolean  _headerWritten = new AtomicBoolean(false);
  public static final String[] HEADER         = { "BIND_NAME", "TABLE", "CONTOKEN", "LAST_USED" };

  public BindPackageCsvWriter(Writer writer) {
    assert writer != null;
    _csv = new CSVDB2Writer(writer);
  }

  public void write(Collection<BindPackage> bindPackages) {
    if (bindPackages == null || bindPackages.isEmpty()) return;

    if (!_headerWritten.get()) {
      _csv.writeNext(HEADER);
      _headerWritten.set(true);
    }

    // write rows
    for (BindPackage bindPack : bindPackages) {
      String name = bindPack.getName();
      String contoken = bindPack.getContoken() == null ? "" : bindPack.getContoken();
      String lastUsed = bindPack.getLastUsedString() == null ? "" : bindPack.getLastUsedString();

      bindPack.getTables().forEach(table -> {
        _csv.writeNext(new String[] { name, table, contoken, lastUsed });
      });
    }
  }

  public void write(BindPackage... bindPackages) {
    write(Arrays.asList(bindPackages));
  }

  @Override
  public void close() throws IOException {
    if (_csv != null) _csv.close();
  }
}
