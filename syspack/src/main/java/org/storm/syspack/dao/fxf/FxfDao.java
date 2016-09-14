package org.storm.syspack.dao.fxf;

import java.util.Collection;

import org.storm.syspack.domain.User;

import com.opencsv.CSVWriter;

public interface FxfDao {
  abstract void loadTo(Collection<User> users, CSVWriter csv);
}
