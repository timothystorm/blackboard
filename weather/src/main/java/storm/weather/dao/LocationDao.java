package storm.weather.dao;

import java.util.List;

import storm.weather.domain.Location;

/**
 * Contract interface for managing location data
 * 
 * @author Timothy Storm
 */
public interface LocationDao {
	/**
	 * Fetches all locations stored in the datastore or an empty set if no locations are persisted.
	 * 
	 * @return locations in the datastore or empty set if non are found - never null.
	 */
	List<Location> readAll();

	/**
	 * Saves the location into the datastore. Implementations determine behavior on duplicates.
	 * 
	 * @param location
	 *            to be persisted in the datastore
	 * @return the number of locations stored
	 * @throws NullPointerException
	 *             if location is null
	 */
	int save(Location locations);

	/**
	 * Deletes a {@link Location} from the datastore
	 * 
	 * @param locations
	 *            to delete
	 * @return count of locations deleted
	 */
	int delete(Location locations);

	/**
	 * Deletes all {@link Location}s form the datastore.
	 * 
	 * @return number of locations deleted
	 */
	int deleteAll();
}
