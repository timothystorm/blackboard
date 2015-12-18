package storm.weather.domain;

import java.io.Serializable;
import java.math.BigInteger;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import storm.weather.Version;

/**
 * Represents a location. For now only the postal code is captured.
 * 
 * @author Timothy Storm
 */
public class Location implements Serializable {
	private static final long serialVersionUID = Version.svuid();

	private Long _id;

	private String _postal, _city;

	public Location() {
		super();
	}

	public Location(String postal) {
		setPostal(postal);
	}

	/**
	 * @param city
	 * @return this location for further building
	 */
	public Location city(String city) {
		setCity(city);
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof Location)) return false;
		if (obj == this) return true;

		Location other = (Location) obj;
		EqualsBuilder equals = new EqualsBuilder();
		equals.append(getId(), other.getId());
		equals.append(getPostal(), other.getPostal());
		return equals.isEquals();
	}

	public String getCity() {
		return _city;
	}

	public Long getId() {
		return _id;
	}

	public String getPostal() {
		return _postal;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder hash = new HashCodeBuilder(17, 31);
		hash.append(getId());
		hash.append(getPostal());
		return hash.toHashCode();
	}

	/**
	 * @param id
	 * @return this location for further building
	 */
	public Location id(Long id) {
		setId(id);
		return this;
	}

	/**
	 * @param postal
	 * @return this location for further building
	 */
	public Location postal(String postal) {
		setPostal(postal);
		return this;
	}

	public void setCity(String city) {
		_city = city;
	}

	public void setId(final Long id) {
		_id = id;
	}

	public void setPostal(final String postal) {
		if (postal == null) {
			_postal = null;
			setId(null);
		} else {
			_postal = postal;
			setId(new BigInteger(_postal.getBytes()).longValue());
		}
	}

	@Override
	public String toString() {
		ToStringBuilder str = new ToStringBuilder(this, ToStringStyle.JSON_STYLE);
		str.append("id", getId());
		str.append("postal", getPostal());
		return str.toString();
	}
}
