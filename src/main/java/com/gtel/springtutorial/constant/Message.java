package com.gtel.springtutorial.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Message {
    public static final String OK = "Yêu cầu thực hiện thành công!";

    public static final String ACTIVATION_SUCCESS = "Account activated!";

    public static final String PASSWORD_UPDATE_SUCCESS = "Password changed successfully!";

    public static final String OLD_PASSWORD_NOT_MATCH = "Old password is not correct! Please try again!";

}
