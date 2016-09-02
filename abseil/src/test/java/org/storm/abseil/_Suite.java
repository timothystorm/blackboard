package org.storm.abseil;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ AbseilShutdownTest.class, AbseilTimeoutTest.class, AbseilFinishTest.class })
public class _Suite {}
