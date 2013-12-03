package common;

import java.util.TimeZone;

public interface SystemConstants {

    public String ENCODING = "UTF-8";

    public TimeZone DEFAULT_LOCALE = TimeZone.getTimeZone("GMT+8:00");

    public String DATETIME_FORMAT = "yyyy/MM/dd HH:mm:ss";

    public long REPLY_TIME_LIMIT = 1000 * 30;

    public String REPLACE_PREFIX = "%{";

    public String REPLACE_SUFFIX = "}";

    public String LINE_SEPARATOR = "\n";
}
