package org.storm.measure.quantity;

/**
 * Represents the capacity of a physical system to do work.
 * 
 * @author Timothy Storm
 */
public class Energy extends AbstractQuantity<Energy> {
	public static class EnergyUnit extends AbstractUnit<Energy> {
		/* reference unit */
		public static final EnergyUnit JOULE = new EnergyUnit(1.0, "J");
		public static final EnergyUnit REF_UNIT = JOULE;

		/* Non-SI */
		public static final EnergyUnit CALORIE = new EnergyUnit(4.1868, "cal");
		public static final EnergyUnit BTU = new EnergyUnit(1055.0559, "btu");

		public EnergyUnit(Number conversionFactor, String symbol) {
			super(conversionFactor, symbol);
		}

		@Override
		public AbstractUnit<Energy> getReferenceUnit() {
			return REF_UNIT;
		}
	}

	public Energy() {
		super();
	}

	public Energy(Number value, EnergyUnit unit) {
		super(value, unit);
	}

	@Override
	protected AbstractUnit<?> getReferenceUnit() {
		return EnergyUnit.REF_UNIT;
	}
}
