package org.storm.syspack.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

import org.storm.syspack.domain.User;

import com.opencsv.CSVWriter;

public class UserCsvWriter implements Closeable {

  private final CSVWriter _csv;

  public UserCsvWriter(PrintWriter writer) {
    _csv = new CSVWriter(writer);
  }

  public void write(Collection<User> users) {
    if (users == null || users.isEmpty()) return;
    for (User usr : users) {
      String username = usr.getUsername();
      String uuid = usr.getUuid();
      String phone = usr.getPhone().getValue();

      for (String act : usr) {
        _csv.writeNext(new String[] { uuid, phone, username, act });
      }
    }
  }

  @Override
  public void close() throws IOException {
    if (_csv != null) _csv.close();
  }
}
