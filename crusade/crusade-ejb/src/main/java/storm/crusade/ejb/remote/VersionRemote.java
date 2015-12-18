package storm.crusade.ejb.remote;

import javax.ejb.Remote;

import storm.crusade.ejb.home.VersionBean;

/**
 * Protocol to execute {@link VersionBean}
 * 
 * @author <a href="timothystorm@gmail.com">Timothy Storm</a>
 */
@Remote
public interface VersionRemote {
    public static final String MAPPED_NAME = "crusade.version";

    public String getVersion();
}
