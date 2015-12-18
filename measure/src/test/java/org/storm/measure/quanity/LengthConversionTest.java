package org.storm.measure.quanity;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.storm.measure.quantity.Length.feet;
import static org.storm.measure.quantity.Length.inch;
import static org.storm.measure.quantity.Length.mile;
import static org.storm.measure.quantity.Length.nauticalMile;
import static org.storm.measure.quantity.Length.*;
import static org.storm.measure.quantity.Length.LengthUnit.FEET;
import static org.storm.measure.quantity.Length.LengthUnit.MILE;
import static org.storm.measure.quantity.Length.LengthUnit.NAUTICAL_MILE;
import static org.storm.measure.quantity.Length.LengthUnit.*;

import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.storm.measure.quantity.AbstractUnit;
import org.storm.measure.quantity.Length;

@RunWith(Parameterized.class)
public class LengthConversionTest {
	Length _from, _to;
	AbstractUnit<Length> _convert;

	public LengthConversionTest(Length from, AbstractUnit<Length> convert, Length to) {
		_from = from;
		_convert = convert;
		_to = to;
	}

	@Parameters
	public static Collection<Object[]> testData() {
		return asList(new Object[][]
		{
				// Non-SI conversions
				{ inch(100), FEET, feet(8.3333333) },
				{ inch(100), MILE, mile(0.0015782828) },
				{ inch(100), YARD, yard(2.7777778) },
				{ inch(100), NAUTICAL_MILE, nauticalMile(0.0013714903) },
				{ feet(100), INCH, inch(1200) },
				{ feet(100), MILE, mile(0.018939394) },
				{ feet(100), YARD, yard(33.333333) },
				{ feet(100), NAUTICAL_MILE, nauticalMile(0.016457883) },
				{ yard(100), INCH, inch(3600) },
				{ yard(100), FEET, feet(300) },
				{ yard(100), MILE, mile(0.056818182) },
				{ yard(100), NAUTICAL_MILE, nauticalMile(0.04937365) },

				// SI conversions
				{ centiMeter(100), METER, meter(1) },
				{ centiMeter(100), MILLIMETER, milliMeter(1000) },
				{ centiMeter(100), KILOMETER, kiloMeter(.001) },
				{ meter(100), CENTIMETER, centiMeter(10000) },
				{ meter(100), MILLIMETER, milliMeter(100000) },
				{ meter(100), KILOMETER, kiloMeter(.1) },

				// Mixed conversions
				{ inch(100), METER, meter(2.54) },
				{ inch(100), CENTIMETER, centiMeter(254) },
				{ inch(100), MILLIMETER, milliMeter(2540) },
				{ inch(100), KILOMETER, kiloMeter(.00254) },
				{ centiMeter(100), INCH, inch(39.370079) },
				{ centiMeter(100), FEET, feet(3.2808399) },
				{ centiMeter(100), YARD, yard(1.0936133) },
				{ centiMeter(100), MILE, mile(0.00062137119) },

		});
	}

	@Test
	public void test_convert() throws Exception {
		assertEquals(_to.doubleValue(), _from.as(_convert).doubleValue(), .000001);
	}
}
