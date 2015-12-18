package org.storm.measure.quantity;

public class Speed extends AbstractQuantity<Speed> {

	public static class SpeedUnit extends AbstractUnit<Speed> {
		public static final SpeedUnit FEET_PER_MINUTE = new SpeedUnit(0.00508, "ft/m");
		public static final SpeedUnit FEET_PER_SECOND = new SpeedUnit(0.3048, "ft/s");
		public static final SpeedUnit KILOMETER_PER_HOUR = new SpeedUnit(0.27777778, "km/s");
		public static final SpeedUnit KILOMETER_PER_MINUTE = new SpeedUnit(16.666667, "km/m");
		public static final SpeedUnit KNOTS = new SpeedUnit(0.51444445, "kts");
		public static final SpeedUnit METER_PER_SECOND = new SpeedUnit(1, "m/s");
		public static final SpeedUnit MILE_PER_HOUR = new SpeedUnit(0.44704, "mi/h");
		public static final SpeedUnit MILE_PER_MINUTE = new SpeedUnit(26.8224, "mi/m");
		public static final SpeedUnit REF_UNIT = METER_PER_SECOND;

		public SpeedUnit(Number conversionFactor, String symbol) {
			super(conversionFactor, symbol);
		}

		@Override
		public AbstractUnit<Speed> getReferenceUnit() {
			return REF_UNIT;
		}
	}

	public static Speed feetPerMinute(Number value) {
		return new Speed(value, SpeedUnit.FEET_PER_MINUTE);
	}

	public static Speed feetPerSecond(Number value) {
		return new Speed(value, SpeedUnit.FEET_PER_SECOND);
	}

	public static Speed kilometerPerHour(Number value) {
		return new Speed(value, SpeedUnit.KILOMETER_PER_HOUR);
	}

	public static Speed kilometerPerMinute(Number value) {
		return new Speed(value, SpeedUnit.KILOMETER_PER_MINUTE);
	}

	public static Speed knots(Number value) {
		return new Speed(value, SpeedUnit.KNOTS);
	}

	public static Speed light() {
		return Length.meter(299792458).per(Time.second());
	}

	public static void main(String[] args) {
		System.out.println(sound());
	}

	public static Speed meterPerHour(Number value) {
		return new Speed(value, SpeedUnit.MILE_PER_HOUR);
	}

	public static Speed meterPerSecond(Number value) {
		return Length.meter(value).per(Time.second());
	}

	public static Speed milePerMinute(Number value) {
		return new Speed(value, SpeedUnit.MILE_PER_MINUTE);
	}

	/* utility builders */
	public static Speed sound() {
		return Length.meter(340.29).per(Time.second());
	}

	public Speed() {
		super();
	}

	public Speed(Number value, SpeedUnit unit) {
		super(value, unit);
	}

	public Speed(Speed speed) {
		super(speed);
	}

	@Override
	protected AbstractUnit<?> getReferenceUnit() {
		return SpeedUnit.REF_UNIT;
	}
}