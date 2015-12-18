package storm.crusade.web.aspect;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

/**
 * Compresses a resource response.
 * 
 * <p>
 * Example:
 * 
 * <pre>
 * &#064;GET
 * &#064;CompressEntity
 * public Response get() {
 *     return Response.ok(entity).build();
 * }
 * </pre>
 * 
 * @author <a href="timothystorm@gmail.com">Timothy Storm</a>
 */
@CompressEntity
public class CompressEntityInterceptor implements WriterInterceptor {

    private static final String ENCODING_GZIP = "gzip";

    @Override
    public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
        try {
            final OutputStream outputStream = context.getOutputStream();
            context.setOutputStream(new GZIPOutputStream(outputStream));
            context.getHeaders().add(HttpHeaders.CONTENT_ENCODING, ENCODING_GZIP);
        } catch (Exception ignore) {
            // gracefully degrade and allow the entity to return without compression
        }
        context.proceed();
    }
}
