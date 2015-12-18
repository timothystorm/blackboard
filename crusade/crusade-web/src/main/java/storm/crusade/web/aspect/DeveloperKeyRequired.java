package storm.crusade.web.aspect;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.ws.rs.NameBinding;

/**
 * <a href="https://jersey.java.net/documentation/latest/filters-and-interceptors.html#d0e9976">Name-Bound</a>
 * annotation that instructs that a resource should only access if a correct developer key is provided. This can be
 * found in the startup logs.
 * 
 * @author <a href="timothystorm@gmail.com">Timothy Storm</a>
 * @see DeveloperKeyRequiredFilter
 */
@NameBinding
@Retention(RetentionPolicy.RUNTIME)
public @interface DeveloperKeyRequired {}
