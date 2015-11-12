package utils;

import javafx.application.Platform;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by sal on 11/11/15.
 */
public class ErrorHandling
{
    private static final Logger logger = Logger.getGlobal();

    public static void log(Level level, String msg)
    {
        assert logger != null : "Logger is null";

        logger.log(level, msg);
    }

    public static void logAndExit(Level level, String msg)
    {
        log(level, msg);
        Platform.exit();
    }
}
