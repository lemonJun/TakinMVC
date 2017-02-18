package lemon.mvc.mvc.cache;

public class HttpCache {
    // Header
    public static final String HEADER_LAST_MODIFIED = "Last-Modified";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_CONTENT_ENCODING = "Content-Encoding";
    public static final String HEADER_EXPIRES = "Expires";
    public static final String HEADER_IF_MODIFIED_SINCE = "If-Modified-Since";
    public static final String HEADER_CACHE_CONTROL = "Cache-Control";
    public static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";

    // Fragment parameter
    public static final int FRAGMENT_AUTODETECT = -1;
    public static final int FRAGMENT_NO = 0;
    public static final int FRAGMENT_YES = 1;

    // No cache parameter
    public static final int NOCACHE_OFF = 0;
    public static final int NOCACHE_SESSION_ID_IN_URL = 1;

    // Last Modified parameter
    public static final long LAST_MODIFIED_OFF = 0;
    public static final long LAST_MODIFIED_ON = 1;
    public static final long LAST_MODIFIED_INITIAL = -1;

    // Expires parameter
    public static final long EXPIRES_OFF = 0;
    public static final long EXPIRES_ON = 1;
    public static final long EXPIRES_TIME = -1;

    // Cache Control
    public static final long MAX_AGE_NO_INIT = Long.MIN_VALUE;
    public static final long MAX_AGE_TIME = Long.MAX_VALUE;
}
