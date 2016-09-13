package org.storm.syspack.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;

import org.junit.Test;
import org.storm.syspack.domain.BindPackage;

public class BindPackageCsvTest {
  static final String  HEADER   = "\"BIND_NAME\",\"TABLE\",\"CONTOKEN\",\"LAST_USED\"";
  static final String  LF       = "\n";
  static final String  EMPTY    = "";

  static StringBuilder STUB_CSV = new StringBuilder(HEADER).append(LF);
  static {
    STUB_CSV.append("\"WW57I001\",\"ANI_CUSTOMER\",\"18b3f349171af0c2\",\"2016-08-29\"").append(LF);
    STUB_CSV.append("\"WW57I001\",\"ANI_CUSTOMER_XREF\",\"18b3f349171af0c2\",\"2016-08-29\"").append(LF);
  }

  static BindPackage STUB_BIND_PACK = new BindPackage("WW57I001");
  static {
    STUB_BIND_PACK.setContoken("18b3f349171af0c2");
    STUB_BIND_PACK.setLastUsed("2016-08-29");
    STUB_BIND_PACK.addTable("ANI_CUSTOMER");
    STUB_BIND_PACK.addTable("ANI_CUSTOMER_XREF");
  }

  @Test
  public void marshal_unmarshal() throws Exception {
    StringWriter writer = new StringWriter();
    try (BindPackageCsvWriter csv = new BindPackageCsvWriter(writer)) {
      csv.write(STUB_BIND_PACK);
    }

    BindPackage actual = null;
    StringReader reader = new StringReader(writer.toString());
    try (BindPackageCsvReader csv = new BindPackageCsvReader(reader)) {
      Collection<BindPackage> bindPacks = csv.read();
      assertNotNull(bindPacks);
      assertFalse(bindPacks.isEmpty());
      actual = bindPacks.iterator().next();
    }

    assertEquals(STUB_BIND_PACK, actual);
  }
}
