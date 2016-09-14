package org.storm.syspack.dao.db2;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

class NamedParamInsert {
  private final String _query;

  NamedParamInsert(DatabaseMetaData metaData, String tableName) throws SQLException {
    _query = init(metaData, tableName);
  }

  private String init(DatabaseMetaData metaData, String tableName) throws SQLException {
    // DB2 is nice enough to give me the columns names from multiple segments so we have to filter to only use unique
    // columns
    Set<String> uniqueSet = new HashSet<>();

    StringBuilder query = new StringBuilder("INSERT INTO ").append(tableName);
    StringBuilder cols = new StringBuilder();
    StringBuilder vals = new StringBuilder();

    try (ResultSet columns = metaData.getColumns(null, null, tableName, null)) {
      while (columns.next()) {
        String c = columns.getString("COLUMN_NAME");

        if (!uniqueSet.contains(c)) {
          cols.append(c).append(", ");
          vals.append(":" + c).append(", ");

          uniqueSet.add(c);
        }
      }
    }

    // remove trailing ','
    cols.setLength(cols.length() - 2);
    vals.setLength(vals.length() - 2);

    // asseble parts
    query.append("(").append(cols).append(") VALUES(").append(vals).append(")");
    return query.toString();
  }

  public String query() {
    return _query;
  }
}
