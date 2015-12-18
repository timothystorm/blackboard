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
public class LengthAddTest {
	private Length _addend1, _addend2, _expectedSum;

	public LengthAddTest(Length addend1, Length addend2, Length expectedSum) {
		_addend1 = addend1;
		_addend2 = addend2;
		_expectedSum = expectedSum;
	}

	@Parameters
	public static Collection<Length[]> testData() {
		return asList(new Length[][]
		{
				// matching units
				{ inch(10), inch(10), inch(20) },
				{ centiMeter(10), centiMeter(10), centiMeter(20) },
				{ milliMeter(10), milliMeter(10), milliMeter(20) },
				{ meter(10), meter(10), meter(20) },
				{ mile(10), mile(10), mile(20) },
				{ kiloMeter(10), kiloMeter(10), kiloMeter(20) },

				// mixed units
				{ inch(10), centiMeter(10), meter(.354) },
				{ inch(10), milliMeter(10), meter(0.264) },
				{ inch(10), meter(10), meter(10.254) },
				{ inch(10), mile(10), meter(16093.694) },
				{ inch(10), kiloMeter(10), meter(10000.254) },
				{ centiMeter(10), milliMeter(10), meter(.11) },
				{ centiMeter(10), meter(10), meter(10.1) },
				{ centiMeter(10), mile(10), meter(16093.54) },
				{ centiMeter(10), kiloMeter(10), meter(10000.1) },
				{ milliMeter(10), meter(10), meter(10.01) },
				{ milliMeter(10), mile(10), meter(16093.45) },
				{ milliMeter(10), kiloMeter(10), meter(10000.01) },
				{ meter(10), mile(10), meter(16103.44) },
				{ meter(10), kiloMeter(10), meter(10010) },
				{ mile(10), kiloMeter(10), meter(26093.44) }
		});
	}

	@Test
	public void test_add() throws Exception {
		assertEquals(_expectedSum.doubleValue(), _addend1.add(_addend2).doubleValue(), .001);
	}
}
