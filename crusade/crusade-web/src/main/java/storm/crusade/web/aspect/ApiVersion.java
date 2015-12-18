package storm.crusade.web.aspect;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a method should produce/consume a PRDC specific MediaType. Quickly identifies which version a
 * particular resource method supports.
 * 
 * </h3>Overview</h3>
 * 
 * <p>
 * Default strategy is to set the value = '1'
 * 
 * <pre class="code">
 * &#64;GET
 * &#64;ApiVersion
 * public Resource get(String arg) {
 *     // process resource
 *     return Resource.ok(MyDataManager.getData()).build();
 * }
 * </pre>
 * 
 * <p>
 * Specify the version
 * 
 * <pre class="code">
 * &#64;GET
 * &#64;ApiVersion(2)
 * public Resource get(String arg) {
 *     // process resource
 *     return Resource.ok(MyDataManager.getData()).build();
 * }
 * </pre>
 * 
 * <p>
 * Specify the version and additional media types
 * 
 * <pre class="code">
 * &#64;GET
 * &#64;ApiVersion(value = 2, and = { MediaType.APPLICATION_XML })
 * public Resource get(String arg) {
 *     // process resource
 *     return Resource.ok(MyDataManager.getData()).build();
 * }
 * </pre>
 * 
 * p> Specify that only the version media type be accepted
 * 
 * <pre class="code">
 * &#64;GET
 * &#64;ApiVersion(and = {})
 * public Resource get(String arg) {
 *     // process resource
 *     return Resource.ok(MyDataManager.getData()).build();
 * }
 * </pre>
 * 
 * @author <a href="timothystorm@gmail.com">Timothy Storm</a>
 * @see ApiVersionModelProcessor#MEDIA_TYPE
 */
@Documented
@Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiVersion {
    /**
     * The version of the entity. The version is is used to create a version media type.
     * 
     * @see ApiVersionModelProcessor#MEDIA_TYPE
     */
    long value() default 1;

    /**
     * Specify the format of the resource [json, xml, csv, ect...] - default json
     */
    String format() default "json";

    /**
     * Specify additional media types that should be included in the produce/consume header
     */
    String[] and() default {};
}