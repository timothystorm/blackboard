package org.storm.measure.quantity;

import org.storm.measure.quantity.Area.AreaUnit;
import org.storm.measure.quantity.Length.LengthUnit;

/**
 * Represents the amount of space occupied by a three-dimensional object or
 * region of space.
 * 
 * @author Timothy Storm
 */
public class Volume extends AbstractQuantity<Volume> {
	public static class VolumeUnit extends AbstractUnit<Volume> {
		/* Reference unit */
		public static final VolumeUnit CUBIC_METER = new VolumeUnit(1.0, "cM");
		public static final VolumeUnit REF_UNIT = CUBIC_METER;
		
		/* SI */
		public static final VolumeUnit LITRE = new VolumeUnit(0.001, "l");
		public static final VolumeUnit CUBIC_CM = new VolumeUnit(1.0e-6, "cc");
		
		/* Non-SI */
		public static final VolumeUnit GAL_IMP = new VolumeUnit(0.00454609, "gal.");
		public static final VolumeUnit GAL_US = new VolumeUnit(0.0037854118, "gal.");
		public static final VolumeUnit QUART = new VolumeUnit(0.00094635295, "qt.");
		public static final VolumeUnit PINT = new VolumeUnit(0.00047317647, "pt.");
		public static final VolumeUnit FLUID_OZ = new VolumeUnit(2.957353E-5, "fl. oz.");
		public static final VolumeUnit CUP = new VolumeUnit(0.00023658824, "cup");
		public static final VolumeUnit TABLE_SPOON = new VolumeUnit(1.4786765E-5, "tbsp.");
		public static final VolumeUnit TEA_SPOON = new VolumeUnit(4.9289216E-6, "tsp.");

		public VolumeUnit(Number conversionFactor, String symbol) {
			super(conversionFactor, symbol);
		}

		@Override
		public AbstractUnit<Volume> getReferenceUnit() {
			return REF_UNIT;
		}
	}
	
	public Volume() {}

	public Volume(Number value, VolumeUnit unit) {
		super(value, unit);
	}

	// mixed type operations
	public Area divide(Length length) {
		Volume thisLength = convert(VolumeUnit.CUBIC_METER);
		Length thatLength = length.convert(LengthUnit.METER);
		return new Area(thisLength.doubleValue() / thatLength.doubleValue(), AreaUnit.SQ_M);
	}

	public Length divide(Area area) {
		Volume volume = convert(VolumeUnit.CUBIC_METER);
		Area newArea = area.convert(AreaUnit.SQ_M);
		return new Length(volume.doubleValue() / newArea.doubleValue(), LengthUnit.METER);
	}

	@Override
	protected AbstractUnit<?> getReferenceUnit() {
		return AreaUnit.REF_UNIT;
	}
}
