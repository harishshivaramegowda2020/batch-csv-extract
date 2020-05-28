package com.custom.java.preconfig.policy.group.services.starmount.csvFramework;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * date utility class
 */
public class UnmDateUtils {

    public static final String YYYYMMDD_PATTERN = "yyyyMMdd";
    public static final String TXT_EXTENSION = ".txt";

    /**
     * Returns current date and time up to seconds.
     * Milliseconds are trimmed because MSSQL has insufficient precision for them.
     *
     * @return see description
     */
    public static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat(UnmDateUtils.YYYYMMDD_PATTERN);
        Calendar c = Calendar.getInstance();
        return sdf.format(c.getTime());
    }

    public static String format(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        return DateFormatUtils.format(date, pattern);
    }

    public enum FolderType {
        inbound("inbound"),
        outbound("outbound"),
        archive("archive"),
        rejected("rejected");

        private String name;

        FolderType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
