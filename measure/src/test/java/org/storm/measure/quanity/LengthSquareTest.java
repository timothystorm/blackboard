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
public class LengthSquareTest {
	private Length _length;
	private Area _area;

	public LengthSquareTest(Length length, Area area) {
		_length = length;
		_area = area;
	}

	@Parameters
	public static Collection<Quanitity<?>[]> testData() {
		return asList(new Quanitity<?>[][]
		{
				{ inch(10), squareMeter(0.064516) },
				{ centiMeter(10), squareMeter(0.01) },
				{ milliMeter(10), squareMeter(0.0001) },
				{ meter(10), squareMeter(100) },
				{ mile(10), squareMeter(2.589988110336e8) },
				{ kiloMeter(10), squareMeter(1e8) },
		});
	}

	@Test
	public void test_multiply() throws Exception {
		assertEquals(_area.doubleValue(), _length.square().doubleValue(), .000001);
	}
}
