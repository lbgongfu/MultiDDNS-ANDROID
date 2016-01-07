package cn.lbgongfu.multiddns.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by gf on 2016/1/6.
 */
public class VerifyUtil {
    private static final String EMAIL_PATTERN = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
    private static final String PHONE_NUMBER_PATTERN = "^\\d{11}$";

    public static boolean isEmailValid(String email)
    {
        Pattern regex = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = regex.matcher(email);
        return matcher.matches();
    }

    public static boolean isPhoneNumberValid(String phoneNumber)
    {
        Pattern regex = Pattern.compile(PHONE_NUMBER_PATTERN);
        Matcher matcher = regex.matcher(phoneNumber);
        return matcher.matches();
    }
}
