package cn.lbgongfu.multiddns;

/**
 * Created by gf on 2015/12/15.
 */
public class Constants {
    public static final int REQUEST_CODE_SELECT_PARSE_SPAN = 0;
    public static final int REQUEST_CODE_SELECT_DOMAIN = 1;
    public static final int REQUEST_CODE_SELECT_GENDER = 2;

    public static final String KEY_SELECTED_PARSE_SPAN = "selectedParseSpan";
    public static final String KEY_SELECTED_DOMAIN = "selectedDomain";
    public static final String KEY_SELECTED_GENDER = "selectedGender";
    public static final String KEY_CURRENT_USER_ID = "currentUserID";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_USER_PASSWORD = "user_password";

    public static final String ACTION_IP_CHANGED = "cn.lbgongfu.multiddns.action.ip_changed";
    public static final String ACTION_SEND_DEBUG_TEXT = "cn.lbgongfu.multiddns.action.send_debug_text";
    public static final String EXTRA_DEBUG_TEXT = "cn.lbgongfu.multiddns.extra.debug_text";
    public static final String EXTRA_NEW_IP = "cn.lbgongfu.multiddns.extra.new_ip";
}
