package com.fedex.toolbox.web;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class Version {
	/** cache application properties so they are not reloaded every time they are accessed */
    static Properties            _propsCache;

    /** cache svuid (serialization ID) so it isn't recalculated every time it is accessed */
    static Long                  _svuidCache;

    /**
     * <pre>
     * [0-9]{1,3} : [major] 1 to 3 digits (required) 
     * [0-9]{1,3} : [minor] 1 to 3 digits (required) 
     * [0-9]{1,3} : [maint] 1 to 3 digits (required)
     * \\w+       : [build] any number of word characters (optional)
     * </pre>
     */
    private static final Pattern VERSION_PATTERN = Pattern
            .compile("([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})\\-?(\\w+)$");

    public static final String buildVersion() {
        return versionPart(4);
    }

    /**
     * @return project build datetime
     */
    public static final String builtAt() {
        return getProperty("application.created");
    }

    public static final String builtBy() {
        return getProperty("application.builtBy");
    }

    /**
     * @return {@link ClassLoader} to use for resource loading
     */
    private static ClassLoader getDefaultClassLoader() {
        ClassLoader loader = null;
        try {
            loader = Thread.currentThread().getContextClassLoader();
        } catch (Throwable threadclassloader_notfound) {}

        if (loader == null) {
            loader = Version.class.getClassLoader();
            if (loader == null) {
                try {
                    loader = ClassLoader.getSystemClassLoader();
                } catch (Throwable giveup) {}
            }
        }
        return loader;
    }

    /**
     * @param key
     *            of the property
     * @return property mapped to the key, or null if not found
     */
    private static String getProperty(final String key) {
        if (_propsCache == null) {
            synchronized (Version.class) {
                if (_propsCache == null) {
                    ClassLoader loader = getDefaultClassLoader();
                    try {
                        InputStream is = loader.getResourceAsStream("version.properties");
                        _propsCache = new Properties();
                        _propsCache.load(is);
                    } catch (Throwable ex) {
                        _propsCache = null;
                    }
                }
            }
        }
        return _propsCache == null ? null : _propsCache.getProperty(key);
    }

    public static void main(String[] args) {
        StringBuilder info = new StringBuilder();
        info.append("Specification-Title:").append(name()).append(StringUtils.LF);
        info.append("Specification-Version:").append(version()).append(StringUtils.LF);
        info.append("Built-At:").append(builtAt()).append(StringUtils.LF);
        info.append("Built-By:").append(builtBy()).append(StringUtils.LF);
        System.out.print(info.toString());
    }

    public static final String maintenanceVersion() {
        return versionPart(3);
    }

    public static final String majorVersion() {
        return versionPart(1);
    }

    public static final String minorVersion() {
        return versionPart(2);
    }

    /**
     * @return project.artifactId from pom
     */
    public static final String name() {
        return getProperty("application.name");
    }

    /**
     * @return byte hashing of {@link #version()}
     */
    public static final long svuid() {
        if (_svuidCache == null) {
            synchronized (Version.class) {
                if (_svuidCache == null) {
                    String v = version();
                    if (v == null) throw new IllegalStateException("invalid version!");
                    BigInteger n = new BigInteger(v.getBytes());
                    _svuidCache = n.longValue();
                }
            }
        }
        return _svuidCache;
    }

    /**
     * @return project.version from pom
     */
    public static final String version() {
        return getProperty("application.version");
    }

    /**
     * Extracts the version parts and returns the requested part (1:major, 2:minor, 3:maintenance, 4:build). If the part
     * if less than 0 then the full version is returned.
     * 
     * @param part
     *            of the version required
     * @return version part or empty string if not found
     */
    private static final String versionPart(int part) {
        // don't allow negative part
        int p = Math.max(0, part);

        Matcher matcher = VERSION_PATTERN.matcher(version());
        if (matcher.find() && matcher.groupCount() >= p) return matcher.group(p);

        // something wonky
        return StringUtils.EMPTY;
    }
}
