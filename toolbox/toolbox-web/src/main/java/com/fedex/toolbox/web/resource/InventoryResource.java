package com.fedex.toolbox.web.resource;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fedex.toolbox.core.domain.Environment;
import com.fedex.toolbox.web.service.InventoryService;

@Component
@Path("/inventory")
public class InventoryResource {

    private final InventoryService _inventory;

    @Context
    private UriInfo                _uri;

    @Autowired
    public InventoryResource(InventoryService inventory) {
        _inventory = inventory;
    }

    @GET
    @Path("env")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Environment> getEnvironments() {
        return _inventory.fetch();
    }

    @POST
    @Path("env")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response saveEnvironment(Environment env) {
        _inventory.persist(env);
        String id = String.valueOf(env.getIdentity().getId());
        return Response.created(_uri.getAbsolutePathBuilder().path(id).build()).build();
    }
}
