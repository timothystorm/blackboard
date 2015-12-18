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
public class LengthSubtractTest {
	private Length _minuend, _subtrahend, _difference;

	public LengthSubtractTest(Length minuend, Length subtahend, Length difference) {
		_minuend = minuend;
		_subtrahend = subtahend;
		_difference = difference;
	}

	@Parameters
	public static Collection<Length[]> testData() {
		return asList(new Length[][]
		{
				// matching units
				{ inch(20), inch(10), inch(10) },
				{ centiMeter(20), centiMeter(10), centiMeter(10) },
				{ milliMeter(20), milliMeter(10), milliMeter(10) },
				{ meter(20), meter(10), meter(10) },
				{ mile(20), mile(10), mile(10) },
				{ kiloMeter(20), kiloMeter(10), kiloMeter(10) },

				// mixed units
				{ inch(20), centiMeter(10), meter(.408) }, 
				{ inch(20), milliMeter(10), meter(0.498) },
				{ inch(20), meter(10), meter(-9.492) },
				{ inch(20), mile(10), meter(-16092.932) },
				{ inch(20), kiloMeter(10), meter(-9999.492) },
				{ centiMeter(20), milliMeter(10), meter(.19) },
				{ centiMeter(20), meter(10), meter(-9.8) },
				{ centiMeter(20), mile(10), meter(-16093.24) },
				{ centiMeter(20), kiloMeter(10), meter(-9999.8) },
				{ milliMeter(20), meter(10), meter(-9.98) },
				{ milliMeter(20), mile(10), meter(-16093.42) },
				{ milliMeter(20), kiloMeter(10), meter(-9999.98) },
				{ meter(20), mile(10), meter(-16073.44) },
				{ meter(20), kiloMeter(10), meter(-9980) },
				{ mile(20), kiloMeter(10), meter(22186.88) }
		});
	}

	@Test
	public void test_subtract() throws Exception {
		assertEquals(_difference.doubleValue(), _minuend.subtract(_subtrahend).doubleValue(), .001);
	}
}
