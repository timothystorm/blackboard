package org.storm.syspack.db2;

import static org.junit.Assert.*;

import org.junit.Test;
import org.storm.syspack.dao.db2.Level;
import org.storm.syspack.dao.db2.LevelFactory;

public class LevelFactoryTest {
  @Test
  public void test_createSource() throws Exception {
    for (int i = 1; i <= 7; i++) {
      Level level = LevelFactory.createSource(i);
      assertNotNull(level);
      assertNotNull(level.getDriver());
      assertNotNull(level.getUrl());
      assertNotNull(level.getAttributes());
    }
  }
  
  @Test
  public void test_createTarget() throws Exception {
    for (int i = 1; i <= 7; i++) {
      Level level = LevelFactory.createTarget(i);
      assertNotNull(level);
      assertNotNull(level.getDriver());
      assertNotNull(level.getUrl());
      assertNotNull(level.getAttributes());
    }
  }
}
