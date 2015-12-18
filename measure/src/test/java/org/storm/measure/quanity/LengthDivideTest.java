package org.storm.measure.quanity;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.storm.measure.quantity.Length.centiMeter;
import static org.storm.measure.quantity.Length.inch;
import static org.storm.measure.quantity.Length.kiloMeter;
import static org.storm.measure.quantity.Length.meter;
import static org.storm.measure.quantity.Length.mile;
import static org.storm.measure.quantity.Length.milliMeter;

import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.storm.measure.quantity.Length;


@RunWith(Parameterized.class)
public class LengthDivideTest {
	private Length _divident, _quotient;
	private Double _divisor;

	public LengthDivideTest(Length divident, Double divisor, Length quotient) {
		_divident = divident;
		_divisor = divisor;
		_quotient = quotient;
	}

	@Parameters
	public static Collection<Object[]> testData() {
		return asList(new Object[][]
		{
				{ inch(10), 2D, inch(5) },
				{ centiMeter(10), 2D, centiMeter(5) },
				{ milliMeter(10), 2D, milliMeter(5) },
				{ meter(10), 2D, meter(5) },
				{ mile(10), 2D, mile(5) },
				{ kiloMeter(10), 2D, kiloMeter(5) },
		});
	}

	@Test
	public void test_add() throws Exception {
		assertEquals(_quotient.doubleValue(), _divident.divide(_divisor).doubleValue(), .001);
	}
}
