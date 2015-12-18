package storm.crusade.domain.configuration;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Test behavior of {@link RangeConfiguration}
 */
public class RangeConfigurationTest {

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    /**
     * Test any and all scenarios that can be thought up
     */
    @Test
    public void broadBrush() throws Exception {
        File tmpFile = tmpFolder.newFile();
        FileUtils.write(tmpFile, broadBrushXml());

        RangeConfiguration config = new RangeConfiguration(tmpFile);
        assertEquals("good", config.getProperty("global"));
    }

    private String broadBrushXml() {
        StringBuilder xml = new StringBuilder();
        xml.append("<configuration>");
        { // range
            xml.append("<range>");
            { // hosts [level:0]
                xml.append("<hosts level=\"0\"/>");
            }
            xml.append("</range>");
        }

        { // property
            xml.append("<property key=\"global\">");
            { // value [*:good]
                xml.append("<value level=\"*\">");
                xml.append("good");
                xml.append("</value>");
            }
            xml.append("</property>");
        }
        xml.append("</configuration>");

        return xml.toString();
    }

    /**
     * Let's eat our own dog food
     */
    @Test
    public void dogFood() throws Exception {
        // create new configuration
        RangeConfiguration configOut = new RangeConfiguration();
        configOut.addProperty("myKey", "myValue");

        // write configuration to file
        File tmpFile = tmpFolder.newFile();
        configOut.save(tmpFile);

        // for debugging purposes
        // System.out.println(FileUtils.readFileToString(tmpFile));

        // read in configuration
        RangeConfiguration configIn = new RangeConfiguration(tmpFile);
        assertEquals("myValue", configIn.getProperty("myKey"));
    }
}
