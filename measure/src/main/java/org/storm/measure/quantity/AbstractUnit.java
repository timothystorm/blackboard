package org.storm.measure.quantity;

public abstract class AbstractUnit<Q extends Quanitity<Q>> implements Unit<Q> {
	private String _symbol; // e.g. "cm"
	private Number _conversionFactor; // e.g. 1E-10

	protected AbstractUnit() {}

	public AbstractUnit(Number conversionFactor, String symbol) {
		setSymbol(symbol);
		setConversionFactor(conversionFactor);
	}

	public AbstractUnit(AbstractUnit<Q> unit) {
		setSymbol(unit._symbol);
		setConversionFactor(unit._conversionFactor);
	}

	protected AbstractUnit(String symbol) {
		this(0, symbol);
	}

	public String getSymbol() {
		return _symbol;
	}

	public abstract AbstractUnit<Q> getReferenceUnit();

	public Number getConversionFactor() {
		return _conversionFactor;
	}

	public Double getConversionFactorAsDouble() {
		return _conversionFactor.doubleValue();
	}

	private void setConversionFactor(Number conversionFactor) {
		_conversionFactor = conversionFactor;
	}

	private void setSymbol(String symbol) {
		_symbol = symbol;
	}

	@Override
	public boolean equals(Object that) {
		if (this == that)
			return true;
		if (!(that instanceof AbstractUnit<?>))
			return false;
		AbstractUnit<?> thatUnit = (AbstractUnit<?>) that;
		return getSymbol().equals(thatUnit.getSymbol());
	}

	@Override
	public int hashCode() {
		return getSymbol().hashCode();
	}
}
