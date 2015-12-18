package org.storm.measure.quantity;

/**
 * <p>
 * This interface represents a determinate {@linkplain Quanitity quantity} (as of length, time, heat, or value) adopted
 * as a standard of measurement.
 * </p>
 * <p>
 * Unit instances should be immutable.
 * </p>
 * 
 * @author Timothy Storm
 * @param <Q>
 *            The type of the quantity measured by this unit.
 */
public interface Quanitity<Q extends Quanitity<Q>> {
	/**
	 * @return  the unit of this quantity value.
	 */
	Unit<Q> unit();
	
	/**
	 * @return the value of this quantity as a number stated in this quantity unit.
	 */
	Number value();
}
