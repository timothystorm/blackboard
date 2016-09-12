package org.storm.syspack;

import org.apache.commons.lang3.ArrayUtils;

public enum Level {
  L1("jdbc:db2://zos2.freight.fedex.com:5030/HRO_DBT2", "FFTA", "1", "U", "UNIT"),
  L2("jdbc:db2://zos2.freight.fedex.com:5030/HRO_DSNB", "DB2FFI", "2", "I", "INT", "INTEGRATION"),
  L3("jdbc:db2://zos3.freight.fedex.com:5040/HRO_DSNX", "FFQA", "3", "S", "SYSTEM", "QA"),
  L4("jdbc:db2://zos3.freight.fedex.com:5040/HRO_DSNX", "FFQA", "4", "V", "VOLUME"),
  L5("jdbc:db2://zos3.freight.fedex.com:5040/HRO_DSNX", "DB2FFE", "5", "E", "EXPRESS", "EXPRESS LOAD"),
  L6("jdbc:db2://zos3.freight.fedex.com:5040/HRO_DSNX", "DB2FFQ", "6", "Q"),
  L7("jdbc:db2://zos1.freight.fedex.com:446/HRO_DBP1", "PROD", "7", "P");

  private final String _url, _schema;
  private final String[] _aka;

  private Level(String url, String schema, String...aka) {
    _url = url;
    _schema = schema;
    _aka = aka;
  }
  
  public String url(){
    return _url;
  }
  
  public String schema(){
    return _schema;
  }
  
  public static Level toLevel(String id){
    if(id == null || id.length() <= 0) return null;
    for(Level l : values()){
      if(l.name().equalsIgnoreCase(id)) return l;
      if(ArrayUtils.contains(l._aka, id.toUpperCase())) return l;
    }
    return null;
  }
}
