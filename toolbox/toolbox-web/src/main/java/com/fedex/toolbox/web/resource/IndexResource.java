package com.fedex.toolbox.web.resource;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.fedex.toolbox.core.utils.MachineUtils;
import com.fedex.toolbox.web.Version;

@Component
@Path("/")
public class IndexResource {
	private static final Logger log = LogManager.getLogger(IndexResource.class);

	/* singleton */
	private static Map<String, String> _version;

	private final Object _versionLock = new Object();

	@GET
	@Path("/version")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getVersion() {
		if (_version == null) {
			synchronized (_versionLock) {
				if (_version == null) {
					log.entry();

					_version = new LinkedHashMap<>();
					_version.put("name", Version.name());
					_version.put("version", Version.version());
					_version.put("built-at", Version.builtAt());
					_version.put("built-by", Version.builtBy());
					_version.put("host", MachineUtils.hostName());
					_version.put("address", MachineUtils.hostAddress());

					log.exit(_version);
				}
			}
		}
		return Response.ok(_version).build();
	}
}
