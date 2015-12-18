package org.storm.measure.quantity;

import org.storm.measure.quantity.Length.LengthUnit;
import org.storm.measure.quantity.Volume.VolumeUnit;

/**
 * Represents the extent of a planar region or of the surface of a solid
 * measured in square units.
 * 
 * @author Timothy Storm
 */
public class Area extends AbstractQuantity<Area> {
	public static class AreaUnit extends AbstractUnit<Area> {
		/* reference unit */
		public static final AreaUnit SQ_M = new AreaUnit(1.0, "sq m"); 
		public static final AreaUnit REF_UNIT = SQ_M;
		
		/* SI */
		public static final AreaUnit SQ_CM = new AreaUnit(1e-4, "sq cm");
		public static final AreaUnit SQ_MM = new AreaUnit(1e-6, "sq mm");
		public static final AreaUnit SQ_KM = new AreaUnit(1e6, "sq km");
		
		/* Non-SI */
		public static final AreaUnit SQ_IN = new AreaUnit(0.00064516, "sq in");
		public static final AreaUnit SQ_FT = new AreaUnit(0.09290304, "sq ft");
		public static final AreaUnit SQ_MI = new AreaUnit(2589988.1, "sq mi");

		public AreaUnit(Number conversionFactor, String symbol) {
			super(conversionFactor, symbol);
		}

		@Override
		public AbstractUnit<Area> getReferenceUnit() {
			return REF_UNIT;
		}
	}

	public Area() {
		super();
	}

	public Area(Number value, AreaUnit unit) {
		super(value, unit);
	}

	public static Area squareMeter(Number value) {
		return new Area(value, AreaUnit.SQ_M);
	}

	public static Area squareCentiMeter(Number value) {
		return new Area(value, AreaUnit.SQ_CM);
	}

	public static Area squareMilliMeter(Number value) {
		return new Area(value, AreaUnit.SQ_MM);
	}

	public static Area squareKiloMeter(Number value) {
		return new Area(value, AreaUnit.SQ_KM);
	}

	public static Area squareInch(Number value) {
		return new Area(value, AreaUnit.SQ_IN);
	}

	public static Area squareFoot(Number value) {
		return new Area(value,AreaUnit.SQ_FT);
	}

	public static Area squareMile(Number value) {
		return new Area(value, AreaUnit.SQ_MI);
	}

	// mixed unit operations
	public Length divide(Length length) {
		Area area = convert(AreaUnit.SQ_M);
		Length conLength = length.convert(LengthUnit.METER);
		return new Length(area.doubleValue() / conLength.doubleValue(), LengthUnit.METER);
	}

	public Volume multiply(Length length) {
		Area area = convert(AreaUnit.SQ_M);
		Length conLength = length.convert(LengthUnit.METER);
		return new Volume(area.doubleValue() * conLength.doubleValue(), VolumeUnit.CUBIC_METER);
	}

	@Override
	protected AbstractUnit<?> getReferenceUnit() {
		return AreaUnit.REF_UNIT;
	}
}
