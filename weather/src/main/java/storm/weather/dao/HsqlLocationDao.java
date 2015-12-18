package storm.weather.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import storm.weather.AppConfig;
import storm.weather.domain.Location;

/**
 * HSQL implementation of {@link LocationDao}
 * 
 * @author Timothy Storm
 * @see AppConfig#getDataSource()
 */
@Repository
public class HsqlLocationDao implements LocationDao {

	@Autowired
	private NamedParameterJdbcTemplate _template;

	public HsqlLocationDao() {}

	@Autowired
	public void setJdbcTemplate(final NamedParameterJdbcTemplate template) {
		_template = template;
	}

	/**
	 * Maps a result set into a {@link Location}
	 */
	private static class LocationMapper implements RowMapper<Location> {

		/**
		 * {@inheritDoc}
		 */
		public Location mapRow(ResultSet rs, int rowNum) throws SQLException {
			Location loc = new Location();
			loc.setPostal(rs.getString("postal"));
			return loc;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Location> readAll() {
		return _template.query("SELECT postal FROM locations", Collections.<String, Object> emptyMap(),
				new LocationMapper());
	}

	/**
	 * Counts the number of occurrences of a given {@link Location
	 * 
	 * @param location
	 *            to count
	 * @return number of occurrences
	 */
	protected int count(Location location) {
		return _template.queryForObject("SELECT COUNT(*) FROM locations WHERE id = :id", map(location), Integer.class);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * Duplicates are ignored
	 */
	public int save(final Location location) {
		if (location == null) throw new NullPointerException();
		if (count(location) > 0) return 0;
		return _template.update("INSERT INTO locations(id, postal) VALUES(:id, :postal)", map(location));
	}

	/**
	 * Maps a {@link Location} to a {@link SqlParameterSource} which is used for paramters in a query
	 * 
	 * @param loc
	 *            to be mapped
	 * @return parameter source
	 */
	private MapSqlParameterSource map(Location loc) {
		MapSqlParameterSource param = new MapSqlParameterSource();
		param.addValue("id", loc.getId());
		param.addValue("postal", loc.getPostal());
		return param;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int delete(Location location) {
		if (location == null) throw new NullPointerException();
		if (count(location) > 0) return _template.update("DELETE FROM locations where id = :id", map(location));
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int deleteAll() {
		return _template.update("DELETE FROM locations", Collections.emptyMap());
	}

}
