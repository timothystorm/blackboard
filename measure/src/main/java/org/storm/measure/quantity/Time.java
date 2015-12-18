package org.storm.measure.quantity;


/**
 * Represents a period of existence or persistence.
 * 
 * @author Timothy Storm
 */
public class Time extends AbstractQuantity<Time> {
	public static class TimeUnit extends AbstractUnit<Time> {

		/* Reference Unit */
		public static final TimeUnit SECOND = new TimeUnit(1.0, "s");
		public static final TimeUnit REF_UNIT = SECOND;
		
		/* SI */
		public static final TimeUnit MILLI_SECOND = new TimeUnit(.001, "ms");
		public static final TimeUnit MINUTE = new TimeUnit(60, "m");
		public static final TimeUnit HOUR = new TimeUnit(60 * 60, "h");
		public static final TimeUnit DAY = new TimeUnit(86400, "d");
		public static final TimeUnit WEEK = new TimeUnit(604800, "w");

		public TimeUnit(Number conversionFactor, String symbol) {
			super(conversionFactor, symbol);
		}

		@Override
		public AbstractUnit<Time> getReferenceUnit() {
			return REF_UNIT;
		}
	}


	public Time() {
		super();
	}

	public Time(Time time) {
		super(time);
	}

	public Time(Number value, TimeUnit unit) {
		super(value, unit);
	}

	@Override
	protected AbstractUnit<?> getReferenceUnit() {
		return TimeUnit.REF_UNIT;
	}

	public static Time milliSecond(Number value) {
		return new Time(value, TimeUnit.MILLI_SECOND);
	}

	public static Time second(Number value) {
		return new Time(value, TimeUnit.SECOND);
	}
	
	public static Time second(){
		return second(1);
	}

	public static Time minute(Number value) {
		return new Time(value, TimeUnit.MINUTE);
	}

	public static Time hour(Number value) {
		return new Time(value, TimeUnit.HOUR);
	}
}
