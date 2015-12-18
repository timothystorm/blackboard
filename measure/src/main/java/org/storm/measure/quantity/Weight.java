package org.storm.measure.quantity;

public class Weight extends AbstractQuantity<Weight> {
	public static class WeightUnit extends AbstractUnit<Weight> {
		public static final WeightUnit GRAM = new WeightUnit(1, "g");
		public static final WeightUnit REF_UNIT = GRAM;

		/* SI */
		public static final WeightUnit KILO_GRAM = new WeightUnit(1000, "kg");

		/* Non-SI */
		public static final WeightUnit POUND = new WeightUnit(453.59237, "lbs");
		public static final WeightUnit STONE = new WeightUnit(6350.2932, "st");

		public WeightUnit(Number conversionFactor, String symbol) {
			super(conversionFactor, symbol);
		}

		@Override
		public AbstractUnit<Weight> getReferenceUnit() {
			return REF_UNIT;
		}
	}

	public Weight() {

	}

	public Weight(Weight weight) {
		super(weight);
	}

	public Weight(Number value, WeightUnit unit) {
		super(value, unit);
	}

	/* utility builders */
	public static Weight pound(Number value) {
		return new Weight(value, WeightUnit.POUND);
	}

	public static Weight kiloGram(Number value) {
		return new Weight(value, WeightUnit.KILO_GRAM);
	}

	public static Weight stone(Number value) {
		return new Weight(value, WeightUnit.STONE);
	}

	@Override
	protected AbstractUnit<?> getReferenceUnit() {
		return WeightUnit.REF_UNIT;
	}

	public static void main(String[] args) {
		System.out.println(pound(182.4).as(WeightUnit.KILO_GRAM));
	}
}
