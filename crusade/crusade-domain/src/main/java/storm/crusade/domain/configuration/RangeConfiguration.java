package storm.crusade.domain.configuration;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Stack;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import storm.crusade.domain.utils.ClassPathUtils;
import storm.crusade.domain.utils.MachineUtils;

/**
 * This {@link Configuration} implements a specialized configuration xml format that permits property selection based on
 * the deployment environment. A configuration file looks like this:
 * 
 * <pre>
 * &lt;?xml version="1.0"?>
 * &lt;range:configuration xmlns:range="http://www.range.com/schema/rangeconfiguration"
 *     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 *     xsi:schemaLocation="http://www.range.com/schema/rangeconfiguration range-configuration.xsd "&gt;
 *     &lt;range:context&gt;
 *         &lt;!-- Default level used for development -- /&gt;
 *         &lt;range:hosts level="L0" /&gt;
 *         
 *         &lt;!-- List the hosts names and/or IPs that are part of the level -- /&gt;
 *         &lt;range:hosts level="L1"&gt;
 *            &lt;range:host&gt;uje70831.idev.range.com&lt;/range:host&gt;
 *            &lt;range:host&gt;10.255.61.171&lt;/range:host&gt;
 *            &lt;range:host&gt;dje70831.idev.range.com&lt;/range:host&gt;
 *            &lt;range:host&gt;10.255.61.170&lt;/range:host&gt;
 *         &lt;/range:hosts>
 *         &lt;range:hosts level="L2"&gt;
 *            &lt;range:host&gt;ije70831.idev.range.com&lt;/range:host&gt;
 *            &lt;range:host&gt;10.255.61.172&lt;/range:host&gt;
 *            &lt;range:host&gt;ije70832.idev.range.com&lt;/range:host&gt;
 *            &lt;range:host&gt;10.255.61.173&lt;/range:host&gt;
 *         &lt;/range:hosts>
 *         
 *         &lt;!-- Itemize properties by level -- /&gt;
 *         &lt;range:property key="return-address"&gt;
 *            &lt;range:value level="L0"&gt;dead.letter@range.com&lt;/range:value&gt;
 *            &lt;range:value level="L1"&gt;dev.team@range.com&lt;/range:value&gt;
 *            &lt;range:value level="L2"&gt;qa.team@range.com&lt;/range:value&gt;
 *         &lt;/range:property&gt;
 *         
 *         &lt;range:property key="remote_url"&gt;
 *            &lt;range:value level="L0"&gt;uje0000.idev.range.com&lt;/range:value&gt;
 *            &lt;range:value level="L1"&gt;dje0001.idev.range.com&lt;/range:value&gt;
 *            &lt;range:value level="L2"&gt;ije0001.idev.range.com&lt;/range:value&gt;
 *         &lt;/range:property&gt;
 * &lt;/range:configuration&gt;
 * </pre>
 * 
 * <em>Note:</em>Configuration objects of this type can be read concurrently by multiple threads.
 * 
 * @author <a href="timothy.range@range.com">Timothy Storm</a>
 * @see #save(Writer)
 */
public class RangeConfiguration extends PropertiesConfiguration {
    /**
     * Attributes of range-configuration
     */
    class Attr {
        static final String KEY   = "key";
        static final String LEVEL = "level";
    }

    /**
     * Container for range-config.xml resources.
     */
    private class Config {
        private String                   _hostLevel, _propKey, _propLevel;

        /** <level,[hosts]> */
        Map<String, List<String>>        _hosts = new HashMap<>();

        /** <key, <level, value>> */
        Map<String, Map<String, String>> _props = new HashMap<>();

        void addHost(String host) throws SAXException {
            if (host == null) throw new SAXException("hosts:host element required!");

            String escape = StringEscapeUtils.escapeXml11(host);
            _hosts.get(_hostLevel).add(StringUtils.trim(StringUtils.trim(escape)));
        }

        void addProperty(String property) throws SAXException {
            if (property == null) throw new SAXException("hosts:host element required!");

            String escape = StringEscapeUtils.escapeXml11(property);
            _props.get(_propKey).put(_propLevel, StringUtils.trim(escape));
        }

        /**
         * @return the host level by mapping the config hosts to the current host name/address
         * @throws SAXException
         *             if the host level cannot be determined.
         */
        private String getHostLevel() throws SAXException {
            // identify localhost information
            String defaultLevel = null;

            // iterate the hosts in the config file
            for (Entry<String, List<String>> entry : _hosts.entrySet()) {
                if (entry.getValue().isEmpty()) {
                    // try to locate a default (*) host
                    if (defaultLevel == null) defaultLevel = entry.getKey();
                    else throw new SAXException("Only 1 default hosts allowed <hosts level='*' />");
                } else {
                    // try to find a matching host name/ip
                    for (String host : entry.getValue()) {
                        if (MachineUtils.hostName().equalsIgnoreCase(host)) return entry.getKey();
                        if (MachineUtils.hostAddress().equalsIgnoreCase(host)) return entry.getKey();
                    }
                }
            }

            if (defaultLevel == null) throw new SAXException(String.format("No host level found for [%s] -or [%s]",
                    MachineUtils.hostName(), MachineUtils.hostAddress()));
            return defaultLevel;
        }

        Properties getProperties() throws SAXException {
            Properties props = new Properties();
            String level = getHostLevel();

            // iterate properties and pull out the appropriate level'ed value
            for (Entry<String, Map<String, String>> properties : _props.entrySet()) {
                String key = properties.getKey();
                String value = properties.getValue().get(level);

                // attempt to get a global level value
                if (value == null) value = properties.getValue().get(GLOBAL_LEVEL);
                if (value != null) props.put(key, value);
            }

            return props;
        }

        /**
         * Sets the current host level being parsed
         * 
         * @param level
         * @throws SAXException
         * @see #addHost(String)
         */
        void setHostLevel(String level) throws SAXException {
            if (level == null) throw new SAXException("hosts:level attribute required!");

            String escape = StringEscapeUtils.escapeXml11(level);
            _hosts.put((_hostLevel = StringUtils.trim(escape)), new ArrayList<String>());
        }

        void setPropertyKey(String key) throws SAXException {
            if (key == null) throw new SAXException("properties:key attribute required!");

            String escape = StringEscapeUtils.escapeXml11(key);
            _props.put((_propKey = StringUtils.trim(escape)), new HashMap<String, String>());
        }

        void setPropertyLevel(String level) throws SAXException {
            if (level == null) throw new SAXException("prop:level attribute required!");

            String escape = StringEscapeUtils.escapeXml11(level);
            _propLevel = StringUtils.trim(escape);
        }
    }

    /**
     * Elements of the range-configuration
     */
    class Element {
        static final String CONFIGURATION = "configuration";
        static final String HOST          = "host";
        static final String HOSTS         = "hosts";
        static final String PROPERTY      = "property";
        static final String VALUE         = "value";
    }

    /**
     * XML parse handler for range-config.xml resources [finite state machine]
     */
    private class rangeConfigurationHandler extends DefaultHandler {
        Config        _config;
        Stack<String> _state;
        StringBuilder _str;

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            _str.append(ch, start, length);
        }

        @Override
        public void endDocument() throws SAXException {
            for (Entry<Object, Object> entry : _config.getProperties().entrySet()) {
                addProperty((String) entry.getKey(), StringUtils.trim((String) entry.getValue()));
            }
        }

        @Override
        public void endElement(String uri, String name, String qName) throws SAXException {
            String element = _state.peek();
            if (Element.HOST.equals(element)) _config.addHost(_str.toString());
            if (Element.VALUE.equals(element)) _config.addProperty(_str.toString());

            // setup for the nexte element
            _state.pop();
            _str.setLength(0);
        }

        @Override
        public void startDocument() throws SAXException {
            _state = new Stack<>();
            _config = new Config();
            _str = new StringBuilder();
        }

        @Override
        public void startElement(String uri, String name, String qName, Attributes attr) throws SAXException {
            _state.push(StringUtils.lowerCase(name));

            String element = _state.peek();
            if (Element.HOSTS.equals(element)) _config.setHostLevel(attr.getValue(StringUtils.EMPTY, Attr.LEVEL));
            if (Element.PROPERTY.equals(element)) _config.setPropertyKey(attr.getValue(StringUtils.EMPTY, Attr.KEY));
            if (Element.VALUE.equals(element)) _config.setPropertyLevel(attr.getValue(StringUtils.EMPTY, Attr.LEVEL));
        }
    }

    static final String ENCODING     = "UTF-8";
    static final String GLOBAL_LEVEL = "*";
    static final String NAMESPACE    = "http://www.range.com/schema/rangeconfiguration";

    // initialization block to set the encoding before loading the file in the constructors
    {
        setEncoding(ENCODING);
    }

    /**
     * Creates an empty StormConfiguration object which can be used to synthesize a new Properties file by adding values
     * and then saving().
     */
    public RangeConfiguration() {
        super();
    }

    /**
     * Creates and loads the xml properties from the specified file. The specified file can contain "include" properties
     * which then are loaded and merged into the properties.
     *
     * @param file
     *            The properties file to load.
     * @throws ConfigurationException
     *             Error while loading the properties file
     */
    public RangeConfiguration(File file) throws ConfigurationException {
        super(file);
    }

    /**
     * Creates and loads the xml properties from the specified file. The specified file can contain "include" properties
     * which then are loaded and merged into the properties.
     *
     * @param fileName
     *            The name of the properties file to load.
     * @throws ConfigurationException
     *             Error while loading the properties file
     */
    public RangeConfiguration(String fileName) throws ConfigurationException {
        super(fileName);
    }

    /**
     * Creates and loads the xml properties from the specified URL. The specified file can contain "include" properties
     * which then are loaded and merged into the properties.
     *
     * @param url
     *            The location of the properties file to load.
     * @throws ConfigurationException
     *             Error while loading the properties file
     */
    public RangeConfiguration(URL url) throws ConfigurationException {
        super(url);
    }

    @Override
    public synchronized void load(Reader in) throws ConfigurationException {
        try {
            // setup the schema
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(ClassPathUtils.loadResource("range-configuration.xsd"));

            // setup the parser factory
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setSchema(schema);
            factory.setValidating(true);
            factory.setNamespaceAware(true);

            // parse the input
            SAXParser parser = factory.newSAXParser();
            parser.parse(new InputSource(in), new rangeConfigurationHandler());
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new ConfigurationException(e);
        }
    }

    /**
     * Writes a template to be enriched with the particulars. This can also be used to create a template for further
     * configurations.
     * 
     * <p>
     * Create a new configuration...
     * 
     * <pre>
     * StormConfiguration config = new StormConfiguration();
     * config.addProperty("key1", "value1");
     * config.addProperty("key2", "value2");
     * 
     * File configFile = new File("my-config.xml");
     * config.save(configFile);
     * </pre>
     * 
     * <p>
     * Generate configuration file...
     * 
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;
     * &lt;range:configuration xmlns:range="http://www.range.com/schema/rangeconfiguration" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.range.com/schema/rangeconfiguration range-configuration.xsd"&gt;
     *   &lt;range:context&gt;
     *       &lt;range:hosts level="0"&gt;
     *         &lt;range:host&gt;CFSIT111111.corp.ds.range.com&lt;/range:host&gt;
     *       &lt;/range:hosts&gt;
     *   &lt;/range:context&gt;
     *   
     *   &lt;range:property key="key1"&gt;
     *       &lt;range:value level="0"&gt;value1&lt;/range:value&gt;
     *   &lt;/range:property&gt;
     *   
     *   &lt;range:property key="key2"&gt;
     *       &lt;range:value level="0"&gt;value2&lt;/range:value&gt;
     *   &lt;/range:property&gt;
     *&lt;/range:configuration&gt;
     * </pre>
     */
    @Override
    public void save(Writer out) throws ConfigurationException {
        PrintWriter writer = new PrintWriter(out);

        String encoding = getEncoding() != null ? getEncoding() : ENCODING;
        writer.println("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>");
        writer.println("<range:configuration xmlns:range=\"http://www.range.com/schema/rangeconfiguration\" ");
        writer.println("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
        writer.println(
                "xsi:schemaLocation=\"http://www.range.com/schema/rangeconfiguration range-configuration.xsd\" >");

        writer.println("<range:context>");
        {
            writer.println("<range:hosts level=\"0\">");
            {
                writer.println("<range:host>");
                writer.println(MachineUtils.hostName());
                writer.println("</range:host>");
            }
            writer.println("</range:hosts>");
        }

        writer.println("</range:context>");

        for (Iterator<String> keys = getKeys(); keys.hasNext();) {
            String key = keys.next();
            Object value = getProperty(key);

            // escape the key
            String k = StringEscapeUtils.escapeXml11(key);
            if (value != null) {
                // escape the value
                String v = StringEscapeUtils.escapeXml11(String.valueOf(value));
                v = StringUtils.replace(v, String.valueOf(getListDelimiter()), "\\" + getListDelimiter());
                writer.println("<range:property key=\"" + k + "\">");
                writer.println("<range:value level=\"0\">" + v + "</range:value>");
                writer.println("</range:property>");
            }

        }

        writer.println("</range:configuration>");
        writer.flush();
    }
}
