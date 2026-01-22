package com.floodguard.backend.constant;


/**
 * CommonConstant class
 *
 * @author INIT
 */
public class CommonConstant {

    public static final String[] IGNORE_PROPERTIES = {"id", "createdAt", "deletedAt", "updatedBy"};
    public static final Integer NOT_OK = 209;
    public static final String URL = "url";
    public static final String PARAM_MESSAGE = "message";
    public static final String PARAM_ID = "id";
    public static final String PARAM_RESULT = "result";
    //special chars
    public static final String BLANK = "";
    public static final String COLON = ":";
    public static final String COMA = ",";
    public static final String VERTICAL_BAR = "|";
    public static final String HYPHEN = "-";
    public static final String UNDER_SCORE = "_";
    public static final String CARET = "^";
    public static final String DOLLAR_SIGN = "$";
    public static final String BRACKETS_OPEN = "(";
    public static final String BRACKETS_CLOSE = ")";
    public static final String SEMICOLON = ";";

    public static final String ACTIVE = "1";
    public static final String DISABLE = "0";

    public static final String ROLE_ADMIN = "ADMIN_ADMIN";
    public static final String ROLE_USER = "USER";

    // UNIT
    public static final String SUCCESS = "Success";
    public static final String ERROR = "Error";

    // action job
    public static final String ACTION_PAUSE = "pause";
    public static final String ACTION_DELETE = "delete";
    public static final String ACTION_RESUME = "resume";
    public static final String ACTION_STOP = "stop";
    public static final String ACTION_START_NOW = "startNow";
    public static final String ACTION_JOB_EXIST = "exist";
    // SETTING COMMON
    public static final int TOTAL_NUMBER_INSERT = 1000;
    // DIVISION VALUE
    public static final int USER_STATUS_ACTIVE = 1;
    public static final int USER_STATUS_CANCEL = 2;

    public static final Integer STATUS_INACTIVE = 0;
    public static final Integer STATUS_ACTIVE = 1;

    public static final Boolean STATUS_TRUE = true;
    public static final Boolean STATUS_FALSE = false;
    // PART SCREEN
    public static final String PART_UPLOAD = "PART_UPLOAD";
    public static final String FOLDER_IMAGE_UPLOAD = "FOLDER_IMAGE_UPLOAD";
    public static final String SEARCH_LIST_DEPARTMENT = "SEARCH_LIST_DEPARTMENT";
    public static final String SEARCH_LIST_ORDER_REQUEST = "SEARCH_LIST_ORDER_REQUEST";

    public static final Integer ORDER_REQUEST_STATUS_NEW = 0;
}
