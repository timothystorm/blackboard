package storm.weather;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ VersionTest.class, storm.weather.dao._Suite.class, storm.weather.service._Suite.class })
public class _AllSuite {}
