package org.storm.syspack.dao.db2;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.stereotype.Repository;

import com.opencsv.CSVReader;

/**
 * Loads data from a CSV file into the DB2 tables. It is assumed that the CSV file is a direct table match with the
 * table to be loaded; in other words a column for column match.
 */
@Repository
public class Db2TableLoader extends NamedParameterJdbcDaoSupport {
  static final Logger log = LoggerFactory.getLogger(Db2TableLoader.class);

  public Db2TableLoader(final JdbcTemplate template) {
    setJdbcTemplate(template);
  }

  public void load(String tableName, CSVReader csv) {
    MapSqlParameterSource params = null;

    try {
      NamedParameterInsert insert = new NamedParameterInsert(getConnection(), tableName);

      // read the header row creating a mapping between index and column names
      Map<Integer, String> indexToNameMap = new HashMap<>();
      String[] header = csv.readNext();
      for (int i = 0; i < header.length; i++) {
        indexToNameMap.put(i, header[i]);
      }

      // iterate and insert each row
      String[] line = null;
      while ((line = csv.readNext()) != null) {
        params = new MapSqlParameterSource();

        // add the values to the appropriate column
        for (int i = 0; i < line.length; i++) {
          params.addValue(indexToNameMap.get(i), line[i]);
        }

        try {
          getNamedParameterJdbcTemplate().update(insert.query(), params);
        } catch (DuplicateKeyException ignore) {}
      }
    } catch (Exception e) {
      StringBuilder msg = new StringBuilder();
      msg.append("load of '").append(tableName).append("' failed\n");
      msg.append("params = ").append(params.getValues()).append("\n");
      log.error(msg.toString(), e);
    }
  }
}
