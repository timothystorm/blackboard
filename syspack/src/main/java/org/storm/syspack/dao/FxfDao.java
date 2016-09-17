package org.storm.syspack.dao;

import java.util.Collection;

import org.storm.syspack.domain.User;

import com.opencsv.CSVWriter;

public interface FxfDao {
  abstract void load(Collection<User> users, CSVWriter csv);
}
