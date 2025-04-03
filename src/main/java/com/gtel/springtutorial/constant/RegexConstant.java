package com.gtel.springtutorial.constant;

public final class RegexConstant {

    public static final String VALID_PHONE_NUM = "^([1-9]\\d{0,2})\\d{4,12}$";

    public static final String IS_PHONE_NUM = "^\\d{10,11}";

    public static final String PASSWORD_PATTERN = "^(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_])[A-Za-z\\d\\W_]{8,}$";

    private RegexConstant() {
    }
}

