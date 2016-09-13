package org.storm.syspack.io;

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;
import java.util.Collection;

import org.junit.Test;
import org.storm.syspack.domain.BindPackage;

public class BindPackageCsvWriterTest extends BindPackageCsvTest {
  @Test(expected = AssertionError.class)
  public void test_null_writer() throws Exception {
    try (BindPackageCsvWriter csv = new BindPackageCsvWriter(null)) {} ;
  }

  @Test
  public void test_null_bindPackages() throws Exception {
    StringWriter writer = new StringWriter();
    try (BindPackageCsvWriter csv = new BindPackageCsvWriter(writer)) {
      csv.write((Collection<BindPackage>) null);
    } ;

    assertEquals(EMPTY, writer.toString());
  }

  @Test
  public void test_simple() throws Exception {
    StringWriter writer = new StringWriter();
    try (BindPackageCsvWriter csv = new BindPackageCsvWriter(writer)) {
      csv.write(STUB_BIND_PACK);
    } ;
    
    assertEquals(STUB_CSV.toString(), writer.toString());
  }
}
