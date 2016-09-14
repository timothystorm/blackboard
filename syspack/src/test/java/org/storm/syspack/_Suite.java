package org.storm.syspack;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.storm.syspack.db2.LevelFactoryTest;

@RunWith(Suite.class)
@SuiteClasses({ LevelFactoryTest.class, org.storm.syspack.io._Suite.class })
public class _Suite {}
