package com.gtel.springtutorial.model.request;

import lombok.Data;

@Data
public class ChangePasswordRequest {
    private String phoneNumber;
    private String oldPassword;
    private String newPassword;
}
