package com.fedex.toolbox.web;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

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
import com.fedex.toolbox.web.resource.ConfigsResource;
import com.fedex.toolbox.web.resource.IndexResource;

/**
 * Starts the application and initializes components.
 *
 * @author Timothy Storm
 */
public class JerseyConfig extends ResourceConfig {
  private static final Logger     log        = LogManager.getLogger(JerseyConfig.class);

  private static final DateFormat UTC_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

  private static final DateTime   START_AT   = new DateTime(DateTimeZone.UTC);

  public static DateTime getStartAt() {
    return START_AT;
  }

  public static Duration getUpTime() {
    return new Duration(DateTime.now(DateTimeZone.UTC), START_AT);
  }

  public JerseyConfig() {
    log.traceEntry();

    // bridge between Jersey and Spring
    register(RequestContextFilter.class);

    // scan for Jersey resources recursively from this config
    // packages(getClass().getPackage().getName());

    // Java <-> JSON
    register(JacksonFeature.class);
    {
      // register mappers
      JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
      JodaMapper jodaMapper = new JodaMapper();
      jodaMapper.setDateFormat(UTC_FORMAT);
      jodaMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
      provider.setMapper(jodaMapper);
      register(provider);
    }

    // register resources
    register(IndexResource.class);
    register(ConfigsResource.class);

    log.traceExit();
  }
}
