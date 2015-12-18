package storm.crusade.web.resource;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.springframework.stereotype.Component;

import storm.crusade.domain.utils.MachineUtils;
import storm.crusade.web.App;
import storm.crusade.web.aspect.DeveloperKeyRequired;

/**
 * General information about the system
 * 
 * @author <a href="timothystorm@gmail.com">Timothy Storm</a>
 */
@Component
@Path("/system")
@DeveloperKeyRequired
public class SystemResource {
    private volatile Long     _touch;
    private static final Long TOUCH_PERIOD = TimeUnit.MINUTES.toMillis(1);
    
    @Context
    private UriInfo                    _uri;

    /**
     * @return host information of the runtime system
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInfo() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("startat", App.getStartAt());
        info.put("uptime", App.getUpTime());
        info.put("os_name", System.getProperty("os.name"));
        info.put("os_version", System.getProperty("os.version"));
        info.put("jdk_version", System.getProperty("java.version"));
        info.put("memory", getMemory());

        return Response.ok(info).build();
    }

    private Map<String, Object> getMemory() {
        Map<String, Object> memory = new LinkedHashMap<>();
        memory.put("max", MachineUtils.maxMemory() + "MB");
        memory.put("total", MachineUtils.totalMemory() + "MB");
        memory.put("used", MachineUtils.committedMemory() + "MB");
        memory.put("free", MachineUtils.freeMemory() + "MB");
        return memory;
    }

    /**
     * Requests a run of the GC
     * 
     * @return the memory stats after running GC
     */
    @DELETE
    @Path("/gc")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteMemory() {
        Long now = System.currentTimeMillis();

        // only allow gc to be called periodically
        if (_touch == null || ((_touch + TOUCH_PERIOD) <= now)) {
            _touch = now;
            System.gc();
        }
        return Response.ok(getMemory()).build();
    }

    /**
     * Resets the current developerKey
     * 
     * @return simple answer
     */
    @DELETE
    public Response resetDeveloperKey() {
        App.resetDeveloperKey();
        return Response.ok().build();
    }

    @GET
    @Path("/logs")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLogs() {
        LoggerContext cntx = (LoggerContext) LogManager.getContext(false);
        Configuration config = cntx.getConfiguration();

        Map<String, Map<String, URI>> logConfig = new TreeMap<>();
        for (LoggerConfig c : config.getLoggers().values()) {
            String levelName = c.getLevel().name();
            
            Map<String, URI> logs = logConfig.get(levelName);
            if(logs == null) logConfig.put(levelName, (logs = new TreeMap<>()));
            
            String logName = c.getName();
            if(StringUtils.isBlank(logName)) logName = "root";
            logs.put(logName, _uri.getRequestUriBuilder().path(logName).build());
        }

        return Response.ok(logConfig).build();
    }
    
    @GET
    @Path("/logs/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLogs(@PathParam("name") String name) {
        LoggerContext cntx = (LoggerContext) LogManager.getContext(false);
        Configuration config = cntx.getConfiguration();
        LoggerConfig logConfig = config.getLoggerConfig(name);
        
        Map<String, Object> appenders = new TreeMap<>();
        for(Appender a : logConfig.getAppenders().values()){
            appenders.put(a.getName(), a.getLayout().getContentFormat());
        }
        
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("name", name);
        info.put("level", logConfig.getLevel().name());
        info.put("additive", logConfig.isAdditive());
        info.put("appenders", appenders);

        return Response.ok(info).build();
    }
}
