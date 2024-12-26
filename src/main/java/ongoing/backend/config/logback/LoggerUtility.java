package ongoing.backend.config.logback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerUtility {

  private static final Logger logger = LoggerFactory.getLogger(LoggerUtility.class);

  public static void logInfo(String message) {
    if (logger.isInfoEnabled()) {
      logger.info(message);
    }
  }

  public static void logDebug(String message) {
    if (logger.isDebugEnabled()) {
      logger.debug(message);
    }
  }

  public static void logWarn(String message) {
    if (logger.isWarnEnabled()) {
      logger.warn(message);
    }
  }

  public static void logError(String message, Throwable throwable) {
    if (logger.isErrorEnabled()) {
      logger.error(message, throwable);
    }
  }

  public static void logError(String message) {
    if (logger.isErrorEnabled()) {
      logger.error(message);
    }
  }


}
