package org.storm.papyrus.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.storm.papyrus.category.Unit;

@Category(Unit.class)
public class VersionTest {
  @Test
  public void serialVersionUid() throws Exception {
    assertTrue(Version.svuid() > 0L);
  }

  @Test
  public void name() throws Exception {
    assertEquals(Version.name(), "papyrus-core");
  }

  @Test
  public void majorVersion() throws Exception {
    assertTrue(Version.majorVersion()
                      .matches("\\d+"));
  }

  @Test
  public void minorVersion() throws Exception {
    assertTrue(Version.minorVersion()
                      .matches("\\d+"));
  }

  @Test
  public void maintenanceVersion() throws Exception {
    assertTrue(Version.maintenanceVersion()
                      .matches("\\d+"));
  }

  @Test
  public void buildVersion() throws Exception {
    assertNotNull(Version.buildVersion());
  }
}
