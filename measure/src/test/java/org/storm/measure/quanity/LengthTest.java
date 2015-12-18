package org.storm.measure.quanity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.storm.measure.quantity.Length;

public class LengthTest {
	@Test
	public void test_equals() throws Exception {
		Length l1 = Length.inch(1);
		assertTrue(l1.equals(l1));

		Length l2 = Length.inch(1);
		assertTrue(l1.equals(l2));
		assertTrue(l2.equals(l1));

		l2 = l2.add(1);
		assertFalse(l1.equals(l2));
	}

	@Test
	public void test_hashCode() throws Exception {
		Length l1 = Length.inch(1);
		assertTrue(l1.hashCode() == l1.hashCode());

		Length l2 = Length.inch(1);
		assertTrue(l1.hashCode() == l2.hashCode());

		l2 = l2.add(1);
		assertFalse(l1.hashCode() == l2.hashCode());
	}

	@Test
	public void test_copyConstructor() throws Exception {
		Length l1 = Length.inch(1);
		Length copy = new Length(l1);

		assertEquals(l1, copy);
		assertNotSame(l1, copy);
		
		copy = copy.add(1);
		
		assertFalse(l1.equals(copy));
	}
}
