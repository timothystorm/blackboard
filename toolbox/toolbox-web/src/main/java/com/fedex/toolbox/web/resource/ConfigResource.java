package com.fedex.toolbox.web.resource;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;

import com.fedex.toolbox.core.dao.ConfigDao;

@Path("/config")
public class ConfigResource {
  @Autowired
  private ConfigDao _dao;

  @Context
  private UriInfo   _uri;

  @DELETE
  @Path("/{key}")
  public Response deleteConfig(@PathParam("key") String key) {
    Response response = null;
    try {
      _dao.delete(key);
      response = Response.ok().build();
    } catch (Exception e) {
      response = Response.notModified().build();
    }
    return response;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getAllConfigs() {
    return Response.ok(_dao.findAll()).build();
  }

  @GET
  @Path("/{key}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getConfig(@PathParam("key") String key) {
    return Response.ok(_dao.findOne(key)).build();
  }

  @POST
  @Path("/{key}")
  public Response saveConfig(@PathParam("key") String key, @QueryParam("value") String value) {
    return update(key, value);
  }

  protected Response update(String key, String value) {
    Response response = null;
    try {
      _dao.save(key, value);
      UriBuilder uriBuilder = _uri.getAbsolutePathBuilder();
      uriBuilder.path(key).queryParam("value", value);
      response = Response.created(uriBuilder.build()).build();
    } catch (Exception e) {
      response = Response.notModified().build();
    }
    return response;
  }

  @PUT
  @Path("/{key}")
  public Response updateConfig(@PathParam("key") String key, @QueryParam("value") String value) {
    return update(key, value);
  }
}
