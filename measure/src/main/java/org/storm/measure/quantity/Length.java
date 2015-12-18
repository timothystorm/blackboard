package org.storm.measure.quantity;

import org.storm.measure.quantity.Area.AreaUnit;
import org.storm.measure.quantity.Speed.SpeedUnit;
import org.storm.measure.quantity.Time.TimeUnit;

/**
 * Represents the extent of something along its greatest dimension or the extent
 * of space between two objects or places.
 * 
 * @author Timothy Storm
 */
public class Length extends AbstractQuantity<Length> {
	public static class LengthUnit extends AbstractUnit<Length> {
		public static final LengthUnit CENTIMETER = new LengthUnit(1.0e-2, "cm");
		public static final LengthUnit FEET = new LengthUnit(0.3048, "ft");
		public static final LengthUnit INCH = new LengthUnit(0.0254, "in");
		public static final LengthUnit KILOMETER = new LengthUnit(1.0e+3, "km");
		public static final LengthUnit METER = new LengthUnit(1.0, "m");
		public static final LengthUnit MILE = new LengthUnit(1609.344, "mi");
		public static final LengthUnit MILLIMETER = new LengthUnit(1.0e-3, "mm");
		public static final LengthUnit NAUTICAL_MILE = new LengthUnit(1852, "nmi");
		public static final LengthUnit REF_UNIT = METER;
		public static final LengthUnit YARD = new LengthUnit(0.9144, "yd");

		public LengthUnit(Number conversionFactor, String symbol) {
			super(conversionFactor, symbol);
		}

		@Override
		public AbstractUnit<Length> getReferenceUnit() {
			return REF_UNIT;
		}
	}

	/**
	 * convenient builder for centimeters
	 */
	public static Length centiMeter(Number value) {
		return new Length(value, LengthUnit.CENTIMETER);
	}

	/**
	 * convenient builder for feet
	 */
	public static Length feet(Number value) {
		return new Length(value, LengthUnit.FEET);
	}

	/**
	 * convenient builder for inch
	 */
	public static Length inch(Number value) {
		return new Length(value, LengthUnit.INCH);
	}

	/**
	 * convenient builder for kiloMeter
	 */
	public static Length kiloMeter(Number value) {
		return new Length(value, LengthUnit.KILOMETER);
	}

	/**
	 * convenient builder for meter
	 */
	public static Length meter(Number value) {
		return new Length(value, LengthUnit.METER);
	}

	/**
	 * convenient builder for mile
	 */
	public static Length mile(Number value) {
		return new Length(value, LengthUnit.MILE);
	}

	/**
	 * convenient builder for milliMeter
	 */
	public static Length milliMeter(Number value) {
		return new Length(value, LengthUnit.MILLIMETER);
	}

	/**
	 * convenient builder for nautical mile
	 */
	public static Length nauticalMile(Number value) {
		return new Length(value, LengthUnit.NAUTICAL_MILE);
	}

	/**
	 * convenient builder for yard
	 */
	public static Length yard(Number value) {
		return new Length(value, LengthUnit.YARD);
	}
	
	public Length() {
		super();
	}

	public Length(Length length) {
		super(length);
	}

	public Length(Number value, LengthUnit unit) {
		super(value, unit);
	}

	@Override
	protected AbstractUnit<?> getReferenceUnit() {
		return LengthUnit.REF_UNIT;
	}

	/**
	 * Compound measure length * length (area)
	 */
	public Area multiply(Length length) {
		Length thisLength = convert(LengthUnit.METER);
		Length thatLength = length.convert(LengthUnit.METER);
		return new Area(thisLength.doubleValue() * thatLength.doubleValue(), AreaUnit.SQ_M);
	}
	
	/**
	 * Compound measure length^2 (area)
	 * @return
	 */
	public Area square(){
		Length l = convert(LengthUnit.METER);
		return new Area(l.pow(2), AreaUnit.SQ_M);
	}

	/**
	 * Compound measure length/time (speed)
	 */
	public Speed per(Time time) {
		Length length = convert(LengthUnit.METER);
		return new Speed(length.doubleValue() * time.convert(TimeUnit.SECOND).doubleValue(), SpeedUnit.METER_PER_SECOND);
	}
	
	public static void main(String[] args) {
		System.out.println(inch(300));
	}
}
