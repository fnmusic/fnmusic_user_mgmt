package com.fnmusic.user.management.utils;

public class Utils {

    public static final String APPNAME = "fnmusic";

    public static boolean isNullOrEmpty(String object) {
        if ((object != null) || (!object.isEmpty())) {
            return false;
        }

        return true;
    }
}
