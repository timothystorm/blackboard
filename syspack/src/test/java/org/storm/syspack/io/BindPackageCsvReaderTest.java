package org.storm.syspack.io;

import static org.junit.Assert.*;

import java.io.StringReader;
import java.util.Collection;

import org.junit.Test;
import org.storm.syspack.domain.BindPackage;

public class BindPackageCsvReaderTest extends BindPackageCsvTest {
  @Test
  public void test_empty_reader() throws Exception {
    StringReader reader = new StringReader("");
    try (BindPackageCsvReader csv = new BindPackageCsvReader(reader)) {
      Collection<BindPackage> bindPacks = csv.read();
      assertNotNull(bindPacks);
      assertTrue(bindPacks.isEmpty());
    }
  }

  @Test(expected = AssertionError.class)
  public void test_null_reader() throws Exception {
    try (BindPackageCsvReader csv = new BindPackageCsvReader(null)) {} ;
  }

  @Test
  public void test_header_only() throws Exception {
    StringReader reader = new StringReader(HEADER);
    try (BindPackageCsvReader csv = new BindPackageCsvReader(reader)) {
      Collection<BindPackage> bindPacks = csv.read();
      assertNotNull(bindPacks);
      assertTrue(bindPacks.isEmpty());
    }
  }

  @Test
  public void test_basic() throws Exception {
    StringReader reader = new StringReader(STUB_CSV.toString());
    
    try (BindPackageCsvReader csv = new BindPackageCsvReader(reader)) {
      Collection<BindPackage> bindPacks = csv.read();
      assertNotNull(bindPacks);
      assertFalse(bindPacks.isEmpty());
      
      BindPackage bindPack = bindPacks.iterator().next();
      assertEquals("WW57I001", bindPack.getName());
      assertEquals("18b3f349171af0c2", bindPack.getContoken());
      assertEquals("2016-08-29", bindPack.getLastUsedString());
      assertTrue(bindPack.getTables().contains("ANI_CUSTOMER"));
      assertTrue(bindPack.getTables().contains("ANI_CUSTOMER_XREF"));
    }
  }
}
