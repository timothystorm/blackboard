package storm.crusade.web.aspect;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import storm.crusade.web.App;
import storm.crusade.web.Constants;

@DeveloperKeyRequired
@Priority(Priorities.AUTHORIZATION)
public class DeveloperKeyRequiredFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        MultivaluedMap<String, String> query = requestContext.getUriInfo().getQueryParameters();
        String appCode = query.getFirst(Constants.DEVELOPER_KEY);

        if (!App.isValidDeverloperKey(appCode)) {
            ResponseBuilder resp = Response.status(Response.Status.UNAUTHORIZED);
            resp.entity("Not authorized to access resource");
            requestContext.abortWith(resp.build());
        }
    }
}
