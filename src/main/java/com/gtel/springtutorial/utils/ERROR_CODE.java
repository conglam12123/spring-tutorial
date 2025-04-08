package com.gtel.springtutorial.utils;

import lombok.Getter;

@Getter
public enum ERROR_CODE {
    INVALID_FORMAT("ER100", "Invalid format"),
    FIELD_REQUIRED("ER101", "Field required"),
    PASSWORD_NOT_MATCH("ER102", "Password not match"),
    PASSWORD_NOT_STRONG("ER103", "Password is not strong enough"),
    PASSPORT_INVALID("ER104", "Passport invalid"),
    EMAIL_INVALID("ER105", "Email invalid"),
    EMAIL_HAS_BEEN_USED("ER106", "Email has been used"),
    PHONE_NUMBER_INVALID("ER107", "Phone number invalid"),
    VALUE_NOT_ALLOW_RANGE("ER108", "value is not within the allow range"),
    PASSCODE_NOT_MATCH("ER109", "Passcode not match"),
    PASSPORT_HAS_BEEN_USED("ER110", "Passport has been used"),
    INVALID_REQUEST("ER_400", "Invalid Request"),
    INVALID_PARAMETER("ER_401", "Unauthorized"),
    RESOURCE_NOT_FOUND("ER_404", "Resource Not Found"),
    INTERNAL_SERVER_ERROR("ER_500", "Internal Server Error"),
    OTP_EXPIRED("ER_111", "OTP expired!"),
    INCORRECT_OTP("ER_112", "Incorrect OTP!!"),
    NO_TRANSACTION_FOUND("ERR_113", "No transaction found!!");

    private final String errorCode;
    private final String message;

    ERROR_CODE(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
}
