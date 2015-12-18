package storm.weather;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.Test;

public class VersionTest {
    static final String APP_NAME = "weather";

    @Test
    public void builtAt() throws Exception {
        assertNotNull(Version.builtAt());
    }

    @Test
    public void buildVersion() throws Exception {
        String bv = Version.buildVersion();

        assertNotNull(bv);
        if (StringUtils.isNotBlank(bv)) assertTrue(StringUtils.isAlpha(bv));
    }

    @Test
    public void builtBy() throws Exception {
        assertNotNull(Version.builtBy());
    }

    @Test
    public void maintenanceVersion() throws Exception {
        String mv = Version.maintenanceVersion();
        assertNotNull(mv);
        assertTrue(NumberUtils.isDigits(mv));
    }

    @Test
    public void majorVersion() throws Exception {
        String mv = Version.majorVersion();
        assertNotNull(mv);
        assertTrue(NumberUtils.isDigits(mv));
    }

    @Test
    public void minorVersion() throws Exception {
        String mv = Version.minorVersion();
        assertNotNull(mv);
        assertTrue(NumberUtils.isDigits(mv));
    }

    @Test
    public void name() throws Exception {
        assertEquals(APP_NAME, Version.name());
    }

    @Test
    public void svuid() throws Exception {
        long svuid = Version.svuid();
        assertTrue(svuid > 0);
    }

    @Test
    public void version() throws Exception {
        assertNotNull(Version.version());
    }
}