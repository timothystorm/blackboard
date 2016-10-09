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
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;

import com.fedex.toolbox.core.dao.PropertyDao;

@Path("/props")
public class PropertyResource {
  @Autowired
  private PropertyDao _dao;

  @Context
  private UriInfo     _uri;

  @DELETE
  @Path("/{key}")
  public Response delete(@PathParam("key") String key) {
    return Response.ok(_dao.delete(key)).build();
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response get() {
    return Response.ok(_dao.findAll()).build();
  }

  @GET
  @Path("/{key}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response get(@PathParam("key") String key) {
    return Response.ok(_dao.findOne(key)).build();
  }

  @POST
  @Path("/{key}")
  public Response post(@PathParam("key") String key, @QueryParam("value") String value) {
    _dao.save(key, value);
    return Response.created(_uri.getAbsolutePath()).build();
  }

  @PUT
  @Path("/{key}")
  protected Response put(String key, String value) {
    return Response.ok(_dao.save(key, value)).build();
  }
}
