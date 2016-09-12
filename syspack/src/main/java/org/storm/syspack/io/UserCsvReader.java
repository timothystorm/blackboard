package org.storm.syspack.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.storm.syspack.domain.Phone;
import org.storm.syspack.domain.User;

import com.opencsv.CSVReader;

public class UserCsvReader implements Closeable {
  private final CSVReader _csv;

  public UserCsvReader(Reader reader) {
    _csv = new CSVReader(reader);
  }

  public Collection<User> read() {
    Map<String, User> users = new LinkedHashMap<>();

    try {
      String[] line = null;
      while ((line = _csv.readNext()) != null) {
        String uuid = getValue(line, 0, null);

        User usr = users.computeIfAbsent(uuid, u -> new User(u));
        usr.setPhone(new Phone(getValue(line, 1, null)));
        usr.setUsername(getValue(line, 2, null));
        usr.addAccount(getValue(line, 3, null));
      }
    } catch (IOException ignore) {}

    return users.values();
  }

  private String getValue(String[] array, int index, String defaultValue) {
    if (array == null) return defaultValue;
    if (index >= array.length) return defaultValue;
    return array[index];
  }

  @Override
  public void close() throws IOException {
    if (_csv != null) _csv.close();
  }
}
