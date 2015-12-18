package storm.crusade.web;

import java.util.UUID;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.datatype.joda.JodaMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

import storm.crusade.beans.Version;
import storm.crusade.web.aspect.ApiVersionModelProcessor;
import storm.crusade.web.aspect.CompressEntityInterceptor;
import storm.crusade.web.aspect.DeveloperKeyRequiredFilter;
import storm.crusade.web.resource.IndexResource;
import storm.crusade.web.resource.SystemResource;

/**
 * Starts the application and initializes components.
 *
 * @author <a href="timothystorm@gmail.com">Timothy Storm</a>
 */
public class App extends ResourceConfig {
    private static final Logger     log      = LogManager.getLogger(App.class);

    private static String           DEVELOPER_KEY_VALUE;

    protected static final DateTime START_AT = new DateTime(DateTimeZone.UTC);

    public static DateTime getStartAt() {
        return START_AT;
    }

    public static Duration getUpTime() {
        return new Duration(DateTime.now(DateTimeZone.UTC), START_AT);
    }

    public static boolean isValidDeverloperKey(final String developerKey) {
        if (developerKey == null) return false;
        return DEVELOPER_KEY_VALUE.equals(developerKey);
    }

    /**
     * Resets the application code at startup or in case it leaks and needs to be reset
     */
    public static void resetDeveloperKey() {
        synchronized (App.class) {
            DEVELOPER_KEY_VALUE = String.valueOf(Math.abs(UUID.randomUUID().getMostSignificantBits())).trim();
            log.info(String.format("%s [%s=%s]", Version.name(), Constants.DEVELOPER_KEY, DEVELOPER_KEY_VALUE));
        }
    }

    public App() {
        // bridge between Jersey and Spring
        register(RequestContextFilter.class);

        // Java to<->from JSON
        register(JacksonFeature.class);
        {
            // register mappers
            JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
            JodaMapper jodaMapper = new JodaMapper();
            jodaMapper.setDateFormat(Constants.UTC);
            jodaMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            provider.setMapper(jodaMapper);
            register(provider);
        }

        // Resource(s)
        register(IndexResource.class);
        register(SystemResource.class);

        // Interceptor(s)
        register(CompressEntityInterceptor.class);

        // Filter(s)
        register(DeveloperKeyRequiredFilter.class);

        // Processor(s)
        register(ApiVersionModelProcessor.class);

        resetDeveloperKey();
    }
}
