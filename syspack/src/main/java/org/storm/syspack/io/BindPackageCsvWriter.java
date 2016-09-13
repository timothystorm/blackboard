package org.storm.syspack.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collection;

import org.storm.syspack.domain.BindPackage;

import com.opencsv.CSVWriter;

/**
 * Writes {@link BindPackage}s to csv.
 * 
 * @see BindPackageCsvReader
 */
public class BindPackageCsvWriter implements Closeable {
  private final CSVWriter _csv;

  public BindPackageCsvWriter(Writer writer) {
    _csv = new CSVWriter(writer);
  }

  public void write(Collection<BindPackage> bindPackages) {
    if (bindPackages == null || bindPackages.isEmpty()) return;

    for (BindPackage bindPack : bindPackages) {
      String name = bindPack.getName();
      String contoken = bindPack.getContoken() == null ? "" : bindPack.getContoken();
      String lastUsed = bindPack.getLastUsedString() == null ? "" : bindPack.getLastUsedString();

      // write tables of the bind package
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
