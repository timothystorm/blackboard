package storm.crusade.web.aspect;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.message.internal.MediaTypes;
import org.glassfish.jersey.server.model.ModelProcessor;
import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.glassfish.jersey.server.model.ResourceModel;

/**
 * Identifies methods that are annotated with {@link ApiVersion}. It then sets up the Produce/Consume (Accept/Content
 * Type) headers of a resource.
 * 
 * @author <a href="timothystorm@gmail.com">Timothy Storm</a>
 */
public class ApiVersionModelProcessor implements ModelProcessor {
    public static final String VERSION_MEDIA_TYPE = "vnd.storm.screed.v%s+%s";

    @Override
    public ResourceModel processResourceModel(ResourceModel resourceModel, Configuration configuration) {
        ResourceModel.Builder resourceModelBuilder = new ResourceModel.Builder(false);
        for (Resource res : resourceModel.getResources()) {
            resourceModelBuilder.addResource(createResource(res));
        }
        return resourceModelBuilder.build();
    }

    @Override
    public ResourceModel processSubResource(ResourceModel subResourceModel, Configuration configuration) {
        return subResourceModel;
    }

    private Resource createResource(Resource resource) {
        Resource.Builder resourceBuilder = Resource.builder().path(resource.getPath()).name(resource.getName());
        for (Resource res : resource.getChildResources()) {
            resourceBuilder.addChildResource(createResource(res));
        }

        for (ResourceMethod rm : resource.getResourceMethods()) {
            Method method = getClassMethod(rm);

            if (isApiVersionAnnotation(method)) {
                List<MediaType> mediaTypes = getApiVersionMediaTypes(method);
                resourceBuilder.addMethod(rm).consumes(mediaTypes).produces(mediaTypes);
            } else resourceBuilder.addMethod(rm);
        }

        return resourceBuilder.build();
    }

    private boolean isApiVersionAnnotation(final Method method) {
        return method.isAnnotationPresent(ApiVersion.class);
    }

    private Method getClassMethod(final ResourceMethod rm) {
        return rm.getInvocable().getDefinitionMethod();
    }

    private List<MediaType> getApiVersionMediaTypes(Method method) {
        ApiVersion annon = method.getAnnotation(ApiVersion.class);
        List<MediaType> mediaTypes = new ArrayList<>();

        // add version media type
        mediaTypes.add(new MediaType("application", String.format(VERSION_MEDIA_TYPE, annon.value(), annon.format())));

        // add additional media types
        String[] andTypes = annon.and();
        if (andTypes != null && andTypes.length > 0) {
            mediaTypes.addAll(MediaTypes.createFrom(andTypes));
        }

        return mediaTypes;
    }
}
