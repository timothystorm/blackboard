package org.storm.measure.quantity;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;


/**
 * <p>
 * Base class for all quantities. Handles math function (add, subtract, multiply and divide) and comparisons
 * (greater than, greater than or equal, less than, less than or equal, equals and not equals). It also provides several
 * utilities for conversion.
 * </p>
 * <p>
 * All functional methods act upon primitives or types of the same unit, implementations should provide their own 
 * mixed unit functions - ex. Length.yard(100).multiply(Length.yard(60)) = Area.squareYard(6000)
 * <p>
 * 
 * @author Timothy Storm
 */
public abstract class AbstractQuantity<Q extends Quanitity<Q>> implements Quanitity<Q> {
	protected Number _scalar; // value in reference units
	protected AbstractUnit<?> _unit;
	protected Number _value; // value in units

	public AbstractQuantity() {}

	/**
	 * Copy constructor.
	 * 
	 * <pre>
	 * Volume newVolume = new Volume(oldVolume);
	 * </pre>
	 * 
	 * @param quantity
	 *            to be copied
	 */
	public AbstractQuantity(AbstractQuantity<Q> quantity) {
		this(quantity._value, quantity._unit);
	}

	/**
	 * Creates a quanity <Q>
	 * 
	 * @param value
	 * @param unit
	 */
	public AbstractQuantity(Number value, AbstractUnit<?> unit) {
		init(value, unit);
	}

	/**
	 * Returns a quantity <Q>whose value is the (this + augend) in the same quantity as this class.
	 * 
	 * <pre>
	 * Length fourInches = Length.inch(4);
	 * Length.inch(3).add(fourInches) = 7 inches
	 * </pre>
	 * 
	 * @param quantity
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Q add(AbstractQuantity<Q> quantity) {
		try {
			AbstractQuantity<Q> newQuantity = this.getClass().newInstance();
			return (Q) add(newQuantity, this, quantity, quantity.getReferenceUnit());
		} catch (Exception e) {
			throw new RuntimeException("failed to instantiate quantity", e);
		}
	}

	/**
	 * Returns a quantity <Q>whose value is the (this + augend) and and does any quantity conversion if necessary
	 * 
	 * @param quantity
	 * @return
	 */
	private Object add(AbstractQuantity<Q> sum, AbstractQuantity<Q> addend1, AbstractQuantity<Q> addend2,
			AbstractUnit<?> unit) {
		if (addend1._unit == addend2._unit) {
			sum._unit = addend1._unit;
			sum._scalar = addend1._scalar.doubleValue() + addend2._scalar.doubleValue();
			sum._value = addend1._value.doubleValue() + addend2._value.doubleValue();
		} else {
			sum._unit = unit;
			sum._scalar = addend1._scalar.doubleValue() + addend2._scalar.doubleValue();
			sum._value = sum._scalar;
		}
		return sum;
	}

	/**
	 * Returns a quantity <Q>whose value is the (this + augend) in the same quantity as this class.
	 * 
	 * @param value
	 * @return sum
	 */
	public Q add(Number value) {
		return newQuantity(doubleValue() + value.doubleValue(), getUnit());
	}

	public Q as(AbstractUnit<?> unit){
		return convert(unit);
	}
	
	/**
	 * Converts a quanitity <Q>into another unit.
	 * 
	 * <pre>
	 * new Time(1, TimeUnit.HOUR).convert(TimeUnit.MINUTE) = 60 minutes
	 * </pre>
	 * 
	 * @param unit
	 */
	public Q convert(AbstractUnit<?> unit) {
		return newQuantity(getScalar().doubleValue() / unit.getConversionFactorAsDouble(), unit);
	}

	public Number cube(){
		return Math.pow(_value.doubleValue(), 3);
	}
	
	public Q divide(Number value) {
		return newQuantity(doubleValue() / value.doubleValue(), getUnit());
	}

	public Double doubleValue(){
		return _value.doubleValue();
	}

	/**
	 * Compares equality of magnitude.
	 * 
	 * <pre>
	 * Length.inch(3).eq(Length.inch(3)) = true
	 * Length.inch(3).eq(Length.centiMeter(7.62)) = true
	 * </pre>
	 * 
	 * @param quantity
	 * @return
	 */
	public boolean eq(AbstractQuantity<Q> quantity) {
		return (_scalar == quantity._scalar);
	}

	/**
	 * Compares equality of value and unit.
	 * 
	 * <pre>
	 * Length.inch(3).equals(Length.inch(3)) = true
	 * Length.inch(3).equals(Length.centiMeter(7.62)) = false
	 * </pre>
	 * 
	 * @see #eq(AbstractQuantity) for magnitude comparison
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}

		AbstractQuantity<Q> other = (AbstractQuantity<Q>) obj;
		return new EqualsBuilder().append(_value, other.value()).append(_unit, other._unit).isEquals();
	}

	/**
	 * Determines if this quantity is greater than or equal to the provided quantity in magnitude
	 * 
	 * <pre>
	 * Length.inch(1).ge(Lenth.inch(0)) = true
	 * Length.inch(1).ge(Lenth.inch(2)) = false
	 * Length.inch(2).ge(Lenth.inch(1)) = true
	 * 
	 * Length.inch(1).ge(Length.centiMeter(2.54)) = true
	 * </pre>
	 * 
	 * @param quantity
	 * @return
	 */
	public boolean ge(AbstractQuantity<Q> quantity) {
		return (_scalar.doubleValue() >= quantity._scalar.doubleValue());
	}

	/**
	 * Implementations should provide their reference unit - ex. Length = meter, Time = second
	 */
	protected abstract AbstractUnit<?> getReferenceUnit();

	/**
	 * Gets the scalar (value converted to the reference unit) of this quanity.
	 * 
	 * @return
	 */
	protected Number getScalar() {
		return _scalar.doubleValue();
	}
	
	/**
	 * @return the unit this quantity was created with
	 */
	protected AbstractUnit<?> getUnit() {
		return _unit;
	}

	/**
	 * @return the value this quanitity was created with
	 */
	public Number getValue() {
		return _value;
	}

	public Boolean gt(AbstractQuantity<Q> quantity) {
		return (_scalar.doubleValue() > quantity._scalar.doubleValue());
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@SuppressWarnings("unchecked")
	private Q init(Number value, AbstractUnit<?> unit) {
		_value = value;
		_unit = unit;
		_scalar = value.doubleValue() * unit.getConversionFactorAsDouble();
		return (Q) this;
	}

	public Boolean le(AbstractQuantity<Q> quantity) {
		return (_scalar.doubleValue() <= quantity._scalar.doubleValue());
	}

	public Boolean lt(AbstractQuantity<Q> quantity) {
		return (_scalar.doubleValue() < quantity._scalar.doubleValue());
	}

	public Q multiply(Number value) {
		return newQuantity(doubleValue() * value.doubleValue(), getUnit());
	}

	public Boolean ne(AbstractQuantity<Q> quantity) {
		return (_scalar != quantity._scalar);
	}

	/**
	 * Returns back a new quantity 'Q' with the provided value and in the provided unit.
	 * 
	 * <pre>
	 * newQuantity(10, LengthUnit.INCH);
	 * newQuantity(15, TimeUnit.MINUTE);
	 * </pre>
	 * 
	 * @param value
	 * @param unit
	 */
	@SuppressWarnings("unchecked")
	public Q newQuantity(Number value, AbstractUnit<?> unit) {
		try {
			AbstractQuantity<Q> quantity = this.getClass().newInstance();
			return quantity.init(value, unit);
		} catch (Exception e) {
			throw new RuntimeException("failed to instantiate quantity", e);
		}
	}

	/**
	 * Shows this quantity in the provided unit with rounding according to precision.
	 * 
	 * <pre>
	 * Length.inch(4000).showInUnits(LengthUnit.FEET, 2) = 333.33 ft
	 * </pre>
	 * 
	 * @param unit
	 * @param precision
	 */
	public String showInUnits(AbstractUnit<?> unit, Integer precision) {
		double result = _scalar.doubleValue() / unit.getConversionFactorAsDouble();

		String str = (new Double(result)).toString();
		char cs[] = str.toCharArray();
		int i = 0;
		while (i < cs.length && (cs[i] >= '0' && cs[i] <= '9' || cs[i] == '.'))
			i++;
		BigDecimal bd = new BigDecimal(new String(cs, 0, i));
		BigDecimal bd2 = bd.setScale(precision, RoundingMode.HALF_UP);
		str = bd2.toString();

		String exp = "";
		if (i < cs.length)
			exp = new String(cs, i, cs.length - i);

		return str + exp + ' ' + unit.getSymbol();
	}

	protected Number sqrt(){
		return Math.sqrt(_value.doubleValue());
	}

	protected Number pow(Integer by){
		return  Math.pow(_value.doubleValue(), by);
	}

	@SuppressWarnings("unchecked")
	public Q subtract(AbstractQuantity<Q> quantity) {
		try {
			AbstractQuantity<Q> newQuantity = this.getClass().newInstance();
			return (Q) subtract(newQuantity, this, quantity, quantity.getReferenceUnit());
		} catch (Exception e) {
			throw new RuntimeException("failed to instantiate quantity", e);
		}
	}
	
	private Object subtract(AbstractQuantity<Q> difference, AbstractQuantity<Q> minuend1, AbstractQuantity<Q> minuend2,
			AbstractUnit<?> unit) {
		if (minuend1._unit == minuend2._unit) {
			difference._unit = minuend1._unit;
			difference._scalar = minuend1._scalar.doubleValue() - minuend2._scalar.doubleValue();
			difference._value = minuend1._value.doubleValue() - minuend2._value.doubleValue();
		} else {
			difference._unit = unit;
			difference._scalar = minuend1._scalar.doubleValue() - minuend2._scalar.doubleValue();
			difference._value = difference._scalar;
		}
		return difference;
	}
	
	public Q subtract(Number value) {
		return newQuantity(doubleValue() - value.doubleValue(), getUnit());
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		return sb.append(new Double(_value.doubleValue())).append(' ').append(_unit.getSymbol()).toString();
	}
	
	@SuppressWarnings("unchecked")
	public Unit<Q> unit(){
		return (Unit<Q>) _unit;
	}
	
	public Number value() {
		return _value;
	}
	
	public Q convertToReferenceUnit() {
		return newQuantity(_scalar, _unit.getReferenceUnit());
	}
}
