package org.chiwooplatform.context;

public final class Constants {

    public static final String DEFAULT_CHARSET = "UTF-8";

    public static final String DEFAULT_DELIMITER = ",";

    public static final String CONTENT_TYPE = "Content-Type";

    public static final String LANGUAGE = "lang";

    public static final String LOCAL_TIMEZONE = "Asia/Seoul";

    public static final String UTC_TIMEZONE = "GMT"; // TimeZone.getTimeZone("GMT").getID();

    /**
     * Example: 2014-11-21 16:25:32
     */
    public static final String DEFAULT_TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * Example: 16-06-11 10:10:44
     */
    public static final String LIST_TIMESTAMP_FORMAT = "yy-MM-dd HH:mm:ss";

    /**
     * Example: 2014-11-21T16:25:32-05:00
     */
    public static final String DEFAULT_TIMESTAMP_GMT_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

    /**
     * Example: 2016-04-06T16:04:10.626+0900
     */
    public static final String DEFAULT_TIMESTAMP_GMT_NANOFORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    public static final String TXID = "TXID";

    public static final String SPRING_SECURITY_CONTEXT = "SPRING_SECURITY_CONTEXT";

    public static final String PRINCIPAL = "Principal";

    public static final String AUTH_TOKEN = "x-auth-token";

    public static final String TOKEN = "token";

    public static final String PERM_CODE = "permCd";

    public static final String VALID_MESSAGE_PREFIX = "validation.";

    public static final String I18N_NAMESPACE = "LOCALE_I18N";
}
