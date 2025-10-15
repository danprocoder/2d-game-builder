package com.gamebuilder.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
    public static void d(String tag, String message) {
        System.out.println(getText(tag, "D", message));
    }

    public static void e(String tag, String message) {
        System.err.println(getText(tag, "E", message));
    }

    private static String getText(String tag, String level, String message) {
        String timestamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
        return timestamp + " " + level + " " + tag + ": " + message;
    }
}
