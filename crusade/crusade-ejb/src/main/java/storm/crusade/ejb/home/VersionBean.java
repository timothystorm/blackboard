package storm.crusade.ejb.home;

import javax.ejb.Stateless;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import storm.crusade.domain.utils.MachineUtils;
import storm.crusade.ejb.Version;
import storm.crusade.ejb.remote.VersionRemote;

@Stateless(mappedName = VersionRemote.MAPPED_NAME)
public class VersionBean implements VersionRemote {
    public VersionBean() {}

    public String getVersion() {
        ToStringBuilder str = new ToStringBuilder(ToStringStyle.JSON_STYLE);
        str.append("name", Version.name());
        str.append("version", Version.version());
        str.append("machine", MachineUtils.machineId());
        return str.toString();
    }
}
