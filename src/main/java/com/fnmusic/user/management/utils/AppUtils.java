package com.fnmusic.user.management.utils;

/**
 * Created by Stephen.Enunwah on 4/5/2019
 */
public class AppUtils {

    public static boolean isNullOrEmpty(String value) {

        if (value == null || value.isEmpty()) {
            return true;
        }

        return false;
    }

    public static boolean isNullOrEmpty(Object[] value) {

        if (value == null || value.length == 0) {
            return true;
        }

        return false;
    }

}
