package com.fedex.toolbox.web.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;

@Path("/config")
public class ConfigurationResource {
  @Autowired
  private Configuration _config;
  
  @GET
  @Path("/{key}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getValueOf(@PathParam("key") String key) {
    return Response.ok(_config.getString(key)).build();
  }
}
