package storm.weather.domain;

/**
 * Contract for domain classes that require a unique ID. Implementations determine what 'unique' means; for example,
 * unique for the JVM or unique for all persisted elements. Useful for items in a datastore.
 * 
 * @author Timothy Storm
 */
public interface Identifiable {
	/**
	 * @return unique identifier
	 */
	Long getId();
}
