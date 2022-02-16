package com.softcrypt.deepkeysmusic.common;

public class ErrorFactory {

    private static final String NETWORK = "No Internet Connection.";
    private static final String EMPTY = " Cannot be empty.";
    private static final String EXIST = " does not exist";
    private static final String INCORRECT = "Incorrect ";
    private static final String CONFIRM = "Please confirm password.";
    private static final String MATCH = " do not match.";
    private static final String INVALID = " is invalid. Please check format.";
    private static final String CANCELLED = "Operation cancelled.";
    private static final String INTERNAL = "internal error. Try again.";
    private static final String OPERATION = ".Could not perform action.";
    private static final String FAILED = " failed. Please try again later.";

    private static String result = "";

    public static String throwableMessage(int error, String item) {
        switch (error) {
            case 1:
                result = getEmptyError(item);
                break;
            case 2:
                result = getInvalidError(item);
                break;
            case 3:
                result = getIncorrectError(item);
                break;
            case 4:
                result = getMatchError(item);
                break;
            case 5:
                result = getNetworkError();
                break;
            case 6:
                result = getCancelledError();
                break;
            case 7:
                result = getActionError(item);
                break;
            case 8:
                result = getExistError(item);
                break;
            case 9:
                result = getConfirmError();
                break;
            case 0:
                result = getError();
                break;
            case 10:
                result = getFailedError(item);
                break;
        }
        return result;
    }

    private static String getConfirmError() {
        return CONFIRM;
    }

    private static String getExistError(String item) {
        return item + EXIST;
    }

    private static String getActionError(String item) {
        return item + OPERATION;
    }

    private static String getEmptyError(String item) {
        return item + EMPTY;
    }

    private static String getInvalidError(String item) {
        return item + INVALID;
    }

    private static String getIncorrectError(String item) {
        return INCORRECT + item;
    }

    private static String getMatchError(String item) {
        return item + MATCH;
    }

    private static String getNetworkError() {
        return NETWORK;
    }

    private static String getError() {
        return INTERNAL;
    }

    private static String getCancelledError() {
        return CANCELLED;
    }

    private static String getFailedError(String item) {
        return item + FAILED;
    }

}
