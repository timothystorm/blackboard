package org.storm.papyrus.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration2.BaseConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.storm.papyrus.category.Unit;

@Category(Unit.class)
@RunWith(MockitoJUnitRunner.class)
public class BasePapyrusTest {
  @Mock
  ConfigurationFactory _mockFactory;
  
  BasePapyrus _papyrus;
  
  @Before
  public void before() throws Exception {
    _papyrus = new BasePapyrus(_mockFactory);
  }

  @Test
  public void deleteProperty() throws Exception {
    Configuration conf = new BaseConfiguration();
    conf.addProperty("test", "value");
    Mockito.when(_mockFactory.createConfiguration("scope"))
    .thenReturn(conf);
    
    assertEquals("value", _papyrus.deleteProperty("scope", "test"));
    assertNull(conf.getProperty("test"));
  }
  
  @Test
  public void getProperties() throws Exception {
    Configuration conf = new BaseConfiguration();
    conf.addProperty("test", "value");
    Mockito.when(_mockFactory.createConfiguration("scope"))
           .thenReturn(conf);
    
    Map<String, Object> props = _papyrus.getProperties("scope");
    assertNotNull(props);
    assertEquals(props.get("test"), "value");
  }
  
  @Test
  public void getProperty() throws Exception {
    Configuration conf = new BaseConfiguration();
    conf.addProperty("test", "value");
    Mockito.when(_mockFactory.createConfiguration("scope"))
           .thenReturn(conf);
    
    assertEquals(_papyrus.getProperty("scope", "test"), "value");
  }
  
  @Test
  public void saveProperty() throws Exception {
    Configuration conf = new BaseConfiguration();
    Mockito.when(_mockFactory.createConfiguration("scope"))
    .thenReturn(conf);
    
    _papyrus.saveProperty("scope", "test", "value");
    assertEquals(conf.getProperty("test"), "value");
  }
  
  @Test
  public void saveProperties() throws Exception {
    Configuration conf = new BaseConfiguration();
    Mockito.when(_mockFactory.createConfiguration("scope"))
    .thenReturn(conf);
    
    Map<String, Object> props = new HashMap<>();
    props.put("key1", "value1");
    props.put("key2", "value2");
    
    _papyrus.saveProperties("scope", props);
    assertEquals(_papyrus.getProperty("scope", "key1"), "value1");
    assertEquals(_papyrus.getProperty("scope", "key2"), "value2");
  }
}
