package com.gamebuilder.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
    private static LogThread logThread = LogThread.getInstance();

    /**
     * use this when you want to log information that can be used for debugging
     *
     * @param tag used to identify the source of a log message
     * @param message the log message
    */
    public static void d(String tag, String message) {
        logThread.log(getText(tag, "D", message));
    }

    /**
     * use this when you want to log verbose information 
     *
     * @param tag used to identify the source of a log message
     * @param message the log message
    */
    public static void v(String tag, String message) {
        logThread.log(getText(tag, "V", message));
    }

    /**
     * used when an error occurs
     *
     * @param tag used to identify the source of a log message
     * @param message the log message
     */
    public static void e(String tag, String message) {
        logThread.log(getText(tag, "E", message));
    }

    private static String getText(String tag, String level, String message) {
        String timestamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
        return timestamp + " " + level + " " + tag + ": " + message;
    }
}
