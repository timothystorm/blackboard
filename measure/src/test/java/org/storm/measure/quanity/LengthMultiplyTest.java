package org.storm.measure.quanity;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.storm.measure.quantity.Area.squareMeter;
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
import org.storm.measure.quantity.Area;
import org.storm.measure.quantity.Length;
import org.storm.measure.quantity.Quanitity;


@RunWith(Parameterized.class)
public class LengthMultiplyTest {
	private Length _multiplicand, _multiplier;
	private Area _area;

	public LengthMultiplyTest(Length multiplicand, Length multiplier, Area area) {
		_multiplicand = multiplicand;
		_multiplier = multiplier;
		_area = area;
	}

	@Parameters
	public static Collection<Quanitity<?>[]> testData() {
		return asList(new Quanitity<?>[][]
		{
				// matching units
				{ inch(10), inch(10), squareMeter(0.064516) },
				{ centiMeter(10), centiMeter(10), squareMeter(.01) },
				{ milliMeter(10), milliMeter(10), squareMeter(1e-4) },
				{ meter(10), meter(10), squareMeter(100) },
				{ mile(10), mile(10), squareMeter(258998811.0336) },
				{ kiloMeter(10), kiloMeter(10), squareMeter(1e8) },

				// mixed units
				{ inch(10), centiMeter(10), squareMeter(.0254) },
				{ inch(10), milliMeter(10), squareMeter(0.00254) },
				{ inch(10), meter(10), squareMeter(2.54) },
				{ inch(10), mile(10), squareMeter(4087.7334) },
				{ inch(10), kiloMeter(10), squareMeter(2540) },
				{ centiMeter(10), milliMeter(10), squareMeter(.0010) },
				{ centiMeter(10), meter(10), squareMeter(1) },
				{ centiMeter(10), mile(10), squareMeter(1609.344) },
				{ centiMeter(10), kiloMeter(10), squareMeter(1000) },
				{ milliMeter(10), meter(10), squareMeter(0.1) },
				{ milliMeter(10), mile(10), squareMeter(160.9344) },
				{ milliMeter(10), kiloMeter(10), squareMeter(100) },
				{ meter(10), mile(10), squareMeter(160934.4) },
				{ meter(10), kiloMeter(10), squareMeter(100000) },
				{ mile(10), kiloMeter(10), squareMeter(1.609344E8) }
		});
	}

	@Test
	public void test_multiply() throws Exception {
		assertEquals(_area.doubleValue(), _multiplicand.multiply(_multiplier).doubleValue(), .001);
	}
}
