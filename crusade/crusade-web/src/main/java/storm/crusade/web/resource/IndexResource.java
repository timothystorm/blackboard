package storm.crusade.web.resource;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import storm.crusade.beans.Version;
import storm.crusade.domain.utils.MachineUtils;
import storm.crusade.web.aspect.CompressEntity;

@Component
@Path("/")
public class IndexResource {
    private static final Logger        _log         = LogManager.getLogger(IndexResource.class);

    @Context
    private UriInfo                    _uri;

    /* singleton */
    private static Map<String, String> _version;

    private final Object               _versionLock = new Object();

    @GET
    public Response getWadl() {
        URI wadl = _uri.getBaseUriBuilder().path("application.wadl").build();
        return Response.seeOther(wadl).build();
    }

    @GET
    @CompressEntity
    @Path("/version")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getVersion() {
        if (_version == null) {
            synchronized (_versionLock) {
                if (_version == null) {
                    _log.entry();

                    _version = new LinkedHashMap<>();
                    _version.put("name", Version.name());
                    _version.put("version", Version.version());
                    _version.put("built-at", Version.builtAt());
                    _version.put("host", MachineUtils.hostName());
                    _version.put("address", MachineUtils.hostAddress());

                    _log.exit(_version);
                }
            }
        }
        return Response.ok(_version).build();
    }
}
