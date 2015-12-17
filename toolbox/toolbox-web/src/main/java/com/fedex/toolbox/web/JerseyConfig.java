package com.fedex.toolbox.web;

import javax.ws.rs.ApplicationPath;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.datatype.joda.JodaMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

/**
 * Starts the application and initializes components.
 *
 * @author <a href="timothystorm@gmail.com">Timothy Storm</a>
 */
@ApplicationPath("/")
public class JerseyConfig extends ResourceConfig {
    private static final Logger     log      = LogManager.getLogger(JerseyConfig.class);

    protected static final DateTime START_AT = new DateTime(DateTimeZone.UTC);

    public static DateTime getStartAt() {
        return START_AT;
    }

    public static Duration getUpTime() {
        return new Duration(DateTime.now(DateTimeZone.UTC), START_AT);
    }

    public JerseyConfig() {
        log.entry("Starting...");

        // bridge between Jersey and Spring
        register(RequestContextFilter.class);
        
        // scan for Jersey resources recursively from this config
        packages(getClass().getPackage().getName());

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

        log.exit();
    }
}
