package org.storm.measure.quanity;

import static org.junit.Assert.*;
import static org.storm.measure.quantity.Length.*;

import org.junit.Test;
import org.storm.measure.quantity.Length;
import org.storm.measure.quantity.Length.LengthUnit;

public class WackyLengthTest {

	@Test
	public void test_averageDistanceOfMarsFromSun() throws Exception {
		Length toMars = kiloMeter(230e6); // 230 million Km
		
		assertEquals(inch(9055118110236.22), toMars.as(LengthUnit.INCH));
	}
}
