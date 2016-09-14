package org.storm.syspack.dao.db2;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.opencsv.CSVReader;

public class Db2TableLoader extends NamedParameterJdbcDaoSupport {
  static final Logger log = LoggerFactory.getLogger(Db2TableLoader.class);

  public Db2TableLoader(final JdbcTemplate template) {
    setJdbcTemplate(template);
  }

  public void load(String tableName, CSVReader csv) {
    try {
      NamedParamInsert insert = new NamedParamInsert(getConnection().getMetaData(), tableName);

      // read the header row
      Map<Integer, String> indexToNameMap = new HashMap<>();
      String[] header = csv.readNext();
      for (int i = 0; i < header.length; i++) {
        indexToNameMap.put(i, header[i]);
      }

      // create params for the batch insert
      String[] line = null;
      while ((line = csv.readNext()) != null) {
        MapSqlParameterSource param = new MapSqlParameterSource();

        for (int i = 0; i < line.length; i++) {
          param.addValue(indexToNameMap.get(i), line[i]);
        }

        insert(insert.query(), param);
      }
    } catch (Exception e) {
      log.error("insert of '" + tableName + "' failed!", e);
    }
  }

  private void insert(String query, SqlParameterSource param) {
    try {
      getNamedParameterJdbcTemplate().update(query, param);
    } catch (Exception e) {
      log.warn("Insert failed", e);
    }
  }

  public static void main(String[] args) throws Exception {
    BasicDataSource ds = new BasicDataSource();
    ds.setDriverClassName("com.ibm.db2.jcc.DB2Driver");
    ds.setUrl("jdbc:db2://zos2.freight.fedex.com:5030/HRO_DBT2");
    ds.setUsername("tjs6565");
    ds.setPassword("socR3tes");
    ds.setMinIdle(5);
    ds.setMaxIdle(10);
    ds.setConnectionProperties("currentSchema=FFTA;");

    JdbcTemplate template = new JdbcTemplate(ds);

    Db2TableLoader loader = new Db2TableLoader(template);
    loader.load("ABOL_DETAIL", new CSVReader(new FileReader("C:\\Users\\796565\\Desktop\\sws-data\\ABOL_DETAIL.csv")));
  }
}
