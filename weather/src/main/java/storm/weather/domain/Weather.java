package storm.weather.domain;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import storm.weather.Version;

/**
 * Represents the weather at a given location. For now only the temperature is captured
 * 
 * @author Timothy Storm
 */
public class Weather implements Serializable {
	private static final long serialVersionUID = Version.svuid();

	private Location _location;
	private Integer _temp;

	public Weather() {}

	public Weather(Location location) {
		setLocation(location);
	}

	/**
	 * {@inheritDoc}
	 *
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof Weather)) return false;
		if (obj == this) return true;

		Weather other = (Weather) obj;
		EqualsBuilder equals = new EqualsBuilder();
		equals.append(getLocation(), other.getLocation());
		equals.append(getTemp(), other.getTemp());
		return equals.isEquals();
	}

	public Location getLocation() {
		return _location;
	}

	public Integer getTemp() {
		return _temp;
	}

	/**
	 * {@inheritDoc}
	 *
	 */
	@Override
	public int hashCode() {
		HashCodeBuilder hash = new HashCodeBuilder(17, 31);
		hash.append(getLocation());
		hash.append(getTemp());
		return hash.toHashCode();
	}

	/**
	 * @param location
	 * @return this weather for further building
	 */
	public Weather location(Location location) {
		setLocation(location);
		return this;
	}

	public void setLocation(final Location location) {
		_location = location;
	}

	public void setTemp(final Integer temp) {
		_temp = temp;
	}

	/**
	 * @param temp
	 * @return this weather for further building
	 */
	public Weather temp(Integer temp) {
		setTemp(temp);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 */
	@Override
	public String toString() {
		ToStringBuilder str = new ToStringBuilder(this, ToStringStyle.JSON_STYLE);
		str.append("location", getLocation());
		str.append("temperature", getTemp());
		return str.toString();
	}
}
