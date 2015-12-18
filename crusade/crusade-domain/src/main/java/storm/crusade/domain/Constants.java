package storm.crusade.domain;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Constants {
    public static final String     NEW_LINE = System.getProperty("line.separator");

    /* UTC date/time formatter */
    public static final DateFormat UTC      = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
}
