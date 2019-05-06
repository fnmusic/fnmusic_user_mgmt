package com.fnmusic.user.management.utils;

public class ConstantUtils {

    public static final String[] ACTIONS = new String[] {
            "CREATE_USER",
            "LOGGED_IN",
            "FORGOT_PASSWORD",
            "RESET_PASSWORD"
    };

    public static final String MAILSENDER = "no-reply@fnmusic.com";
    public static final String[] MAIL_TEMPLATE_NAMES = new String[] {
            "ACCOUNT_ACTIVATION",
            "PASSWORD_RESET"
    };

    public static final String[] URL_PATHS = new String[] {
            "https://fnmusic.com/activate/",
            "https://fnmusic.com/resetpassword/"
    };
}
