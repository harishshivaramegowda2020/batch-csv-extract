package com.custom.java.preconfig.policy.group.services.starmount.job;

import com.custom.scheduler.audit.Auditor;
import org.slf4j.Logger;

public class UnmJobUtils {

    private UnmJobUtils() {
    }

    public static void logInfoMessage(Logger logger, Auditor auditor, String message) {
        logger.info(message);
        auditor.info(message);
    }

    public static void logInfoMessage(Logger logger, Auditor auditor, String message, Throwable ex) {
        logger.info(message, ex);
        auditor.info(message);
    }

    public static void logErrorMessage(Logger logger, Auditor auditor, String message) {
        logger.error(message);
        auditor.error(message);
    }

    public static void logErrorMessage(Logger logger, Auditor auditor, String message, Throwable ex) {
        logger.error(message, ex);
        auditor.error(message);
    }
}
