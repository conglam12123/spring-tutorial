package com.gtel.springtutorial.utils;

import com.gtel.springtutorial.constant.RegexConstant;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.regex.Pattern;

public class AppUtils {
    private AppUtils () {}

    public static String standardizePhoneNumber(String phoneNum) {
        if (!Pattern.matches(RegexConstant.VALID_PHONE_NUM, phoneNum)) {
            return phoneNum
                    .replaceFirst("^0", "84")
                    .replaceFirst("\\+", "");
        } else {
            return phoneNum;
        }
    }



    public static long getSecondsUntilMidnight() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = now.toLocalDate().plusDays(1).atStartOfDay();

        return Duration.between(now, midnight).getSeconds();
    }

}
