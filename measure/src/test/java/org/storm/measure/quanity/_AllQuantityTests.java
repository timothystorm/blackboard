package org.storm.measure.quanity;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ LengthAddTest.class, LengthConversionTest.class, LengthDivideTest.class,
		LengthMultiplyTest.class,
		LengthSquareTest.class, LengthSubtractTest.class,
		LengthTest.class })
public class _AllQuantityTests {}