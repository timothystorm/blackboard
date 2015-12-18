package storm.crusade.ejb.remote;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import storm.crusade.domain.utils.Asker;

/**
 * Requires WebLogic jars be available at runtime
 */
public class Version_IT {
    private static Asker _asker;

    @BeforeClass
    public static void init() throws Exception {
        _asker = new Asker();
        _asker.setChoices("URL", true, "remote://localhost:9990");
    }

    @AfterClass
    public static void cleanUp() throws Exception {
        _asker = null;
    }

    @Test
    public void testWebLogic() throws Exception {
        // setup the remote properties
        Properties p = new Properties();
        p.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
        p.put(Context.PROVIDER_URL, _asker.getValue("URL"));

        // fetch and instance the remote object
        InitialContext ctx = new InitialContext(p);
        Object obj = ctx.lookup("bean/name");
        VersionRemote vs = (VersionRemote) PortableRemoteObject.narrow(obj, VersionRemote.class);

        // verify results
        String version = vs.getVersion();

        // for testing purposes only
        // System.out.println(version);

        assertNotNull(version);
        assertFalse(version.isEmpty());
    }
}
