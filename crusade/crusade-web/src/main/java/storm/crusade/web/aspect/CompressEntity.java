package storm.crusade.web.aspect;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.ws.rs.NameBinding;

/**
 * <a href="https://jersey.java.net/documentation/latest/filters-and-interceptors.html#d0e9976">Name-Bound</a>
 * annotation that instructs a resource's response to be compressed using {@link CompressEntityInterceptor}
 * 
 * @author <a href="timothystorm@gmail.com">Timothy Storm</a>
 * @see CompressEntityInterceptor
 */
@NameBinding
@Retention(RetentionPolicy.RUNTIME)
public @interface CompressEntity {}
